/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.internal.identifier;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.DatabasesCache;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.PenaltyCalculationSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.TargetBuilder;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.TargetCombinedComparator;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class FileIdentifier {

	public static final String IDENTIFIER = "File Identifier";
	private static final Logger logger = Logger.getLogger(FileIdentifier.class);
	//
	private TargetCombinedComparator targetCombinedComparator;
	private TargetBuilder targetBuilder;
	private DatabasesCache databasesCache;

	public FileIdentifier() {
		//
		targetCombinedComparator = new TargetCombinedComparator(SortOrder.DESC);
		targetBuilder = new TargetBuilder();
		databasesCache = new DatabasesCache(PreferenceSupplier.getMassSpectraFiles());
	}

	public IMassSpectra runIdentification(List<IScanMSD> massSpectraList, MassSpectrumIdentifierSettings fileIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		IMassSpectra massSpectra = new MassSpectra();
		massSpectra.addMassSpectra(massSpectraList);
		/*
		 * The alternate identifier is used, when another plugin tries to use this file identification process.
		 * The LibraryService uses the identifier to get a mass spectrum of a given target.
		 * It would then use this plugin instead of the plugin who used this identifier.
		 */
		String identifier = IDENTIFIER;
		String alternateIdentifierId = fileIdentifierSettings.getAlternateIdentifierId();
		if(!alternateIdentifierId.equals("")) {
			identifier = alternateIdentifierId;
		}
		/*
		 * Try to identify the mass spectra.
		 */
		FileListUtil fileListUtil = new FileListUtil();
		Map<String, IMassSpectra> databases = databasesCache.getDatabases(fileListUtil.getFiles(fileIdentifierSettings.getMassSpectraFiles()), monitor);
		for(Map.Entry<String, IMassSpectra> database : databases.entrySet()) {
			compareMassSpectraAgainstDatabase(massSpectra, fileIdentifierSettings, identifier, database, monitor);
		}
		/*
		 * Add m/z list on demand if no match was found.
		 */
		for(IScanMSD unknown : massSpectra.getList()) {
			if(unknown.getTargets().size() == 0) {
				if(fileIdentifierSettings.isAddUnknownMzListTarget()) {
					targetBuilder.setMassSpectrumTargetUnknown(unknown, identifier);
				}
			}
		}
		//
		return massSpectra;
	}

	/**
	 * Run the peak identification.
	 * 
	 * @param peaks
	 * @param peakIdentifierSettings
	 * @param processingInfo
	 * @param monitor
	 * @return {@link IPeakIdentificationResults}
	 * @throws FileNotFoundException
	 */
	public IPeakIdentificationResults runPeakIdentification(List<IPeakMSD> peaks, PeakIdentifierSettings peakIdentifierSettings, IProcessingInfo processingInfo, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * The alternate identifier is used, when another plugin tries to use this file identification process.
		 * The LibraryService uses the identifier to get a mass spectrum of a given target.
		 * It would then use this plugin instead of the plugin who used this identifier.
		 */
		IPeakIdentificationResults identificationResults = new PeakIdentificationResults();
		String identifier = IDENTIFIER;
		String alternateIdentifierId = peakIdentifierSettings.getAlternateIdentifierId();
		if(!alternateIdentifierId.equals("")) {
			identifier = alternateIdentifierId;
		}
		/*
		 * Load the mass spectra database only if the raw file or its content has changed.
		 */
		FileListUtil fileListUtil = new FileListUtil();
		Map<String, IMassSpectra> databases = databasesCache.getDatabases(fileListUtil.getFiles(peakIdentifierSettings.getMassSpectraFiles()), monitor);
		for(Map.Entry<String, IMassSpectra> database : databases.entrySet()) {
			comparePeaksAgainstDatabase(peakIdentifierSettings, peaks, identifier, database, monitor);
		}
		/*
		 * Assign a m/z list on demand if no match has been found.
		 */
		for(IPeakMSD peakMSD : peaks) {
			if(peakMSD.getTargets().size() == 0) {
				if(peakIdentifierSettings.isAddUnknownMzListTarget()) {
					targetBuilder.setPeakTargetUnknown(peakMSD, identifier);
				}
			}
		}
		//
		return identificationResults;
	}

	/**
	 * Returns identified mass spectra from the database.
	 * 
	 * @param identificationTarget
	 * @param monitor
	 * @return {@link IMassSpectra}
	 */
	public IMassSpectra getMassSpectra(IIdentificationTarget identificationTarget, IProgressMonitor monitor) {

		IMassSpectra massSpectra = new MassSpectra();
		if(identificationTarget != null) {
			/*
			 * Extract the target library information.
			 * Old *.ocb version don't store the identifier id.
			 * Hence, try to get mass spectrum anyhow.
			 */
			String identifier = identificationTarget.getIdentifier();
			if(identifier.equals(IDENTIFIER) || identifier.equals("")) {
				massSpectra.addMassSpectra(databasesCache.getDatabaseMassSpectra(identificationTarget, monitor));
			}
		}
		//
		return massSpectra;
	}

	private void compareMassSpectraAgainstDatabase(IMassSpectra massSpectra, MassSpectrumIdentifierSettings fileIdentifierSettings, String identifier, Map.Entry<String, IMassSpectra> database, IProgressMonitor monitor) {

		/*
		 * Run the identification.
		 */
		String databaseName = database.getKey();
		List<IScanMSD> references = database.getValue().getList();
		//
		boolean usePreOptimization = fileIdentifierSettings.isUsePreOptimization();
		double thresholdPreOptimization = fileIdentifierSettings.getThresholdPreOptimization();
		//
		int countUnknown = 1;
		for(IScanMSD unknown : massSpectra.getList()) {
			/*
			 * Cancel the operation on demand.
			 */
			if(monitor.isCanceled()) {
				return;
			}
			//
			List<IIdentificationTarget> massSpectrumTargets = new ArrayList<IIdentificationTarget>();
			for(int index = 0; index < references.size(); index++) {
				try {
					/*
					 * Compare the unknown against each library spectrum.
					 * Update the monitor only for each unknown mass spectrum.
					 */
					monitor.subTask("Compare " + countUnknown);
					//
					IScanMSD reference = references.get(index);
					IProcessingInfo infoCompare = MassSpectrumComparator.compare(unknown, reference, fileIdentifierSettings.getMassSpectrumComparatorId(), usePreOptimization, thresholdPreOptimization);
					IComparisonResult comparisonResult = infoCompare.getProcessingResult(IComparisonResult.class);
					applyPenaltyOnDemand(unknown, reference, comparisonResult, fileIdentifierSettings);
					if(isValidTarget(comparisonResult, fileIdentifierSettings.getMinMatchFactor(), fileIdentifierSettings.getMinReverseMatchFactor())) {
						/*
						 * Add the target.
						 */
						IIdentificationTarget massSpectrumTarget = targetBuilder.getMassSpectrumTarget(reference, comparisonResult, identifier, databaseName);
						massSpectrumTargets.add(massSpectrumTarget);
					}
				} catch(TypeCastException e1) {
					logger.warn(e1);
				}
				//
			}
			/*
			 * Assign targets only.
			 */
			if(massSpectrumTargets.size() > 0) {
				Collections.sort(massSpectrumTargets, targetCombinedComparator);
				int numberOfTargets = fileIdentifierSettings.getNumberOfTargets();
				int size = (numberOfTargets <= massSpectrumTargets.size()) ? numberOfTargets : massSpectrumTargets.size();
				for(int i = 0; i < size; i++) {
					unknown.getTargets().add(massSpectrumTargets.get(i));
				}
			}
			//
			countUnknown++;
		}
	}

	private void comparePeaksAgainstDatabase(PeakIdentifierSettings fileIdentifierSettings, List<IPeakMSD> peaks, String identifier, Map.Entry<String, IMassSpectra> database, IProgressMonitor monitor) {

		/*
		 * Run the identification.
		 */
		String databaseName = database.getKey();
		List<IScanMSD> references = database.getValue().getList();
		//
		boolean usePreOptimization = fileIdentifierSettings.isUsePreOptimization();
		double thresholdPreOptimization = fileIdentifierSettings.getThresholdPreOptimization();
		//
		int countUnknown = 1;
		for(IPeakMSD peakMSD : peaks) {
			/*
			 * Cancel the operation on demand.
			 */
			if(monitor.isCanceled()) {
				return;
			}
			//
			List<IIdentificationTarget> peakTargets = new ArrayList<IIdentificationTarget>();
			IScanMSD unknown = peakMSD.getPeakModel().getPeakMassSpectrum();
			for(int index = 0; index < references.size(); index++) {
				/*
				 * Compare the unknown against each library spectrum.
				 */
				try {
					monitor.subTask("Compare " + countUnknown);
					IScanMSD reference = references.get(index);
					IProcessingInfo infoCompare = MassSpectrumComparator.compare(unknown, reference, fileIdentifierSettings.getMassSpectrumComparatorId(), usePreOptimization, thresholdPreOptimization);
					IComparisonResult comparisonResult = infoCompare.getProcessingResult(IComparisonResult.class);
					applyPenaltyOnDemand(unknown, reference, comparisonResult, fileIdentifierSettings);
					if(isValidTarget(comparisonResult, fileIdentifierSettings.getMinMatchFactor(), fileIdentifierSettings.getMinReverseMatchFactor())) {
						/*
						 * Add the target.
						 */
						IIdentificationTarget peakTarget = targetBuilder.getPeakTarget(reference, comparisonResult, identifier, databaseName);
						peakTargets.add(peakTarget);
					}
				} catch(TypeCastException e1) {
					logger.warn(e1);
				}
			}
			/*
			 * Assign targets only.
			 */
			if(peakTargets.size() > 0) {
				Collections.sort(peakTargets, targetCombinedComparator);
				int numberOfTargets = fileIdentifierSettings.getNumberOfTargets();
				int size = (numberOfTargets <= peakTargets.size()) ? numberOfTargets : peakTargets.size();
				for(int i = 0; i < size; i++) {
					peakMSD.getTargets().add(peakTargets.get(i));
				}
			}
			//
			countUnknown++;
		}
	}

	private void applyPenaltyOnDemand(IScanMSD unknown, IScanMSD reference, IComparisonResult comparisonResult, IIdentifierSettingsMSD identifierSettings) {

		float penalty = 0.0f;
		String penaltyCalculation = identifierSettings.getPenaltyCalculation();
		//
		switch(penaltyCalculation) {
			case IIdentifierSettingsMSD.PENALTY_CALCULATION_RETENTION_TIME:
				penalty = PenaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown.getRetentionTime(), reference.getRetentionTime(), identifierSettings.getRetentionTimeWindow(), identifierSettings.getPenaltyCalculationLevelFactor(), identifierSettings.getMaxPenalty());
				break;
			case IIdentifierSettingsMSD.PENALTY_CALCULATION_RETENTION_INDEX:
				penalty = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, identifierSettings.getRetentionIndexWindow(), identifierSettings.getPenaltyCalculationLevelFactor(), identifierSettings.getMaxPenalty());
				break;
		}
		/*
		 * Apply the penalty on demand.
		 */
		if(penalty != 0.0f) {
			comparisonResult.setPenalty(penalty);
		}
	}

	private boolean isValidTarget(IComparisonResult comparisonResult, float minMatchFactor, float minReverseMatchFactor) {

		if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
			return true;
		}
		return false;
	}
}
