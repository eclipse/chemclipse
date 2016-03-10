/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFilePeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.TargetBuilder;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.SortOrder;
import org.eclipse.chemclipse.model.comparator.TargetCombinedComparator;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;

public class FileIdentifier {

	public static final String IDENTIFIER = "File Identifier";
	private static final Logger logger = Logger.getLogger(FileIdentifier.class);
	//
	private TargetCombinedComparator targetCombinedComparator;
	private TargetBuilder targetBuilder;
	private FileDatabases fileDatabases;

	public FileIdentifier() {
		//
		targetCombinedComparator = new TargetCombinedComparator(SortOrder.DESC);
		targetBuilder = new TargetBuilder();
		fileDatabases = new FileDatabases();
	}

	public IMassSpectra runIdentification(List<IScanMSD> massSpectraList, IFileMassSpectrumIdentifierSettings fileIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		IMassSpectra massSpectra = new MassSpectra();
		massSpectra.addMassSpectra(massSpectraList);
		/*
		 * Try to identify the mass spectra.
		 */
		Map<String, IMassSpectra> databases = fileDatabases.getDatabases(fileIdentifierSettings.getMassSpectraFiles(), monitor);
		for(Map.Entry<String, IMassSpectra> database : databases.entrySet()) {
			compareMassSpectraAgainstDatabase(massSpectra, fileIdentifierSettings, database, monitor);
		}
		/*
		 * Add m/z list on demand if no match was found.
		 */
		for(IScanMSD unknown : massSpectra.getList()) {
			List<IMassSpectrumTarget> massSpectrumTargets = unknown.getTargets();
			if(massSpectrumTargets.size() == 0) {
				if(fileIdentifierSettings.isAddUnknownMzListTarget()) {
					targetBuilder.setMassSpectrumTargetUnknown(unknown, IDENTIFIER);
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
	public IPeakIdentificationResults runPeakIdentification(List<IPeakMSD> peaks, IFilePeakIdentifierSettings peakIdentifierSettings, IPeakIdentifierProcessingInfo processingInfo, IProgressMonitor monitor) throws FileNotFoundException {

		IPeakIdentificationResults identificationResults = new PeakIdentificationResults();
		/*
		 * Load the mass spectra database only if the raw file or its content has changed.
		 */
		Map<String, IMassSpectra> databases = fileDatabases.getDatabases(peakIdentifierSettings.getMassSpectraFiles(), monitor);
		for(Map.Entry<String, IMassSpectra> database : databases.entrySet()) {
			comparePeaksAgainstDatabase(peakIdentifierSettings, peaks, database, monitor);
		}
		/*
		 * Assign a m/z list on demand if no match has been found.
		 */
		for(IPeakMSD peakMSD : peaks) {
			if(peakMSD.getTargets().size() == 0) {
				if(peakIdentifierSettings.isAddUnknownMzListTarget()) {
					targetBuilder.setPeakTargetUnknown(peakMSD, IDENTIFIER);
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
			 */
			if(identificationTarget.getIdentifier().equals(IDENTIFIER)) {
				massSpectra.addMassSpectra(fileDatabases.getDatabaseMassSpectra(identificationTarget, monitor));
			}
		}
		//
		return massSpectra;
	}

	private void compareMassSpectraAgainstDatabase(IMassSpectra massSpectra, IFileMassSpectrumIdentifierSettings fileIdentifierSettings, Map.Entry<String, IMassSpectra> database, IProgressMonitor monitor) {

		/*
		 * Run the identification.
		 */
		String databaseName = database.getKey();
		List<IScanMSD> references = database.getValue().getList();
		//
		int countUnknown = 1;
		for(IScanMSD unknown : massSpectra.getList()) {
			/*
			 * Sort the ions of the unknown mass spectrum.
			 */
			List<IMassSpectrumTarget> massSpectrumTargets = new ArrayList<IMassSpectrumTarget>();
			List<IIon> ions = unknown.getIons();
			Collections.sort(ions, fileDatabases.getIonAbundanceComparator());
			/*
			 * Get the library
			 */
			for(int index = 0; index < references.size(); index++) {
				/*
				 * Compare?
				 */
				boolean compare = true;
				if(fileIdentifierSettings.isUsePreOptimization()) {
					compare = fileDatabases.useReferenceForComparison(ions, databaseName, index, fileIdentifierSettings.getThresholdPreOptimization(), true);
				}
				//
				if(compare) {
					/*
					 * Only compare this spectrum.
					 */
					try {
						monitor.subTask("Compare " + countUnknown + " / " + index);
						IScanMSD reference = references.get(index);
						IMassSpectrumComparatorProcessingInfo infoCompare = MassSpectrumComparator.compare(unknown, reference, fileIdentifierSettings.getMassSpectrumComparatorId());
						IMassSpectrumComparisonResult comparisonResult = infoCompare.getMassSpectrumComparisonResult();
						if(isValidTarget(comparisonResult, fileIdentifierSettings.getMinMatchFactor(), fileIdentifierSettings.getMinReverseMatchFactor())) {
							/*
							 * Add the target.
							 */
							IMassSpectrumTarget massSpectrumTarget = targetBuilder.getMassSpectrumTarget(reference, comparisonResult, IDENTIFIER, databaseName);
							massSpectrumTargets.add(massSpectrumTarget);
						}
					} catch(TypeCastException e1) {
						logger.warn(e1);
					}
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
					unknown.addTarget(massSpectrumTargets.get(i));
				}
			}
			//
			countUnknown++;
		}
	}

	private void comparePeaksAgainstDatabase(IFilePeakIdentifierSettings fileIdentifierSettings, List<IPeakMSD> peaks, Map.Entry<String, IMassSpectra> database, IProgressMonitor monitor) {

		/*
		 * Run the identification.
		 */
		String databaseName = database.getKey();
		List<IScanMSD> references = database.getValue().getList();
		//
		int countUnknown = 1;
		for(IPeakMSD peakMSD : peaks) {
			/*
			 * Sort the ions of the unknown mass spectrum.
			 */
			List<IPeakTarget> peakTargets = new ArrayList<IPeakTarget>();
			List<IIon> ions = peakMSD.getExtractedMassSpectrum().getIons();
			Collections.sort(ions, fileDatabases.getIonAbundanceComparator());
			/*
			 * Get the library
			 */
			IScanMSD unknown = peakMSD.getPeakModel().getPeakMassSpectrum();
			for(int index = 0; index < references.size(); index++) {
				/*
				 * Compare?
				 */
				boolean compare = true;
				if(fileIdentifierSettings.isUsePreOptimization()) {
					compare = fileDatabases.useReferenceForComparison(ions, databaseName, index, fileIdentifierSettings.getThresholdPreOptimization(), true);
				}
				//
				if(compare) {
					/*
					 * Only compare this spectrum.
					 */
					try {
						monitor.subTask("Compare " + countUnknown + " / " + index);
						IScanMSD reference = references.get(index);
						IMassSpectrumComparatorProcessingInfo infoCompare = MassSpectrumComparator.compare(unknown, reference, fileIdentifierSettings.getMassSpectrumComparatorId());
						IMassSpectrumComparisonResult comparisonResult = infoCompare.getMassSpectrumComparisonResult();
						if(isValidTarget(comparisonResult, fileIdentifierSettings.getMinMatchFactor(), fileIdentifierSettings.getMinReverseMatchFactor())) {
							/*
							 * Add the target.
							 */
							IPeakTarget peakTarget = targetBuilder.getPeakTarget(reference, comparisonResult, IDENTIFIER, databaseName);
							peakTargets.add(peakTarget);
						}
					} catch(TypeCastException e1) {
						logger.warn(e1);
					}
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
					peakMSD.addTarget(peakTargets.get(i));
				}
			}
			//
			countUnknown++;
		}
	}

	private boolean isValidTarget(IComparisonResult comparisonResult, float minMatchFactor, float minReverseMatchFactor) {

		if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
			return true;
		}
		return false;
	}
}
