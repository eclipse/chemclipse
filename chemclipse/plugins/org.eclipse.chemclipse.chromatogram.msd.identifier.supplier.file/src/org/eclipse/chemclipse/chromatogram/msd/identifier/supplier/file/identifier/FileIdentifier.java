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
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFilePeakIdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.comparator.SortOrder;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumLibraryInformation;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;

public class FileIdentifier {

	private static final Logger logger = Logger.getLogger(FileIdentifier.class);
	private static final String IDENTIFIER = "File Identifier";
	/*
	 * Don't reload the database on each request, only if neccessary.
	 */
	private static long fileSize = 0;
	private static String fileName = "";
	private static IMassSpectra massSpectraDatabase = null;

	public IMassSpectra runIdentification(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		IMassSpectra massSpectra = new MassSpectra();
		if(massSpectrumIdentifierSettings instanceof IFileMassSpectrumIdentifierSettings) {
			IFileMassSpectrumIdentifierSettings settings = (IFileMassSpectrumIdentifierSettings)massSpectrumIdentifierSettings;
			/*
			 * Run the identification.
			 */
			String comparatorId = settings.getMassSpectrumComparatorId();
			float minMatchFactor = settings.getMinMatchFactor();
			float minReverseMatchFactor = settings.getMinReverseMatchFactor();
			int numberOfTargets = settings.getNumberOfTargets();
			/*
			 * Load the mass spectra database only if the raw file or its content has changed.
			 */
			IMassSpectra database = getDatabase(settings.getMassSpectraFile(), monitor);
			/*
			 * Compare
			 */
			int countUnknown = 1;
			for(IScanMSD unknown : massSpectraList) {
				List<IMassSpectrumTarget> massSpectrumTargets = new ArrayList<IMassSpectrumTarget>();
				int countReference = 1;
				for(IScanMSD reference : database.getList()) {
					try {
						monitor.subTask("Compare " + countUnknown + " / " + countReference++);
						IMassSpectrumComparatorProcessingInfo infoCompare = MassSpectrumComparator.compare(unknown, reference, comparatorId);
						IMassSpectrumComparisonResult comparisonResult = infoCompare.getMassSpectrumComparisonResult();
						//
						if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
							/*
							 * Add the target.
							 */
							massSpectrumTargets.add(getMassSpectrumTarget(reference, comparisonResult));
						}
					} catch(TypeCastException e1) {
						logger.warn(e1);
					}
				}
				/*
				 * Assign only the best hits.
				 */
				Collections.sort(massSpectrumTargets, new IdentificationTargetComparator(SortOrder.DESC));
				int size = (numberOfTargets <= massSpectrumTargets.size()) ? numberOfTargets : massSpectrumTargets.size();
				for(int i = 0; i < size; i++) {
					unknown.addTarget(massSpectrumTargets.get(i));
					massSpectra.addMassSpectrum(unknown);
				}
				//
				countUnknown++;
			}
		} else {
			throw new FileNotFoundException("Can't get the file from the settings.");
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
		 * Run the identification.
		 */
		String comparatorId = peakIdentifierSettings.getMassSpectrumComparatorId();
		float minMatchFactor = peakIdentifierSettings.getMinMatchFactor();
		float minReverseMatchFactor = peakIdentifierSettings.getMinReverseMatchFactor();
		int numberOfTargets = peakIdentifierSettings.getNumberOfTargets();
		/*
		 * Load the mass spectra database only if the raw file or its content has changed.
		 */
		IMassSpectra database = getDatabase(peakIdentifierSettings.getMassSpectraFile(), monitor);
		/*
		 * Compare
		 */
		int countUnknown = 1;
		for(IPeakMSD peakMSD : peaks) {
			List<IPeakTarget> peakTargets = new ArrayList<IPeakTarget>();
			int countReference = 1;
			IScanMSD unknown = peakMSD.getPeakModel().getPeakMassSpectrum();
			for(IScanMSD reference : database.getList()) {
				try {
					monitor.subTask("Compare " + countUnknown + " / " + countReference++);
					IMassSpectrumComparatorProcessingInfo infoCompare = MassSpectrumComparator.compare(unknown, reference, comparatorId);
					IMassSpectrumComparisonResult comparisonResult = infoCompare.getMassSpectrumComparisonResult();
					//
					if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
						/*
						 * Add the target.
						 */
						peakTargets.add(getPeakTarget(reference, comparisonResult));
					}
				} catch(TypeCastException e1) {
					logger.warn(e1);
				}
			}
			/*
			 * Assign only the best hits.
			 */
			Collections.sort(peakTargets, new IdentificationTargetComparator(SortOrder.DESC));
			int size = (numberOfTargets <= peakTargets.size()) ? numberOfTargets : peakTargets.size();
			for(int i = 0; i < size; i++) {
				peakMSD.addTarget(peakTargets.get(i));
			}
			//
			countUnknown++;
		}
		//
		return identificationResults;
	}

	public IScanMSD getMassSpectrum(IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			/*
			 * Extract the target library information.
			 */
			ILibraryInformation libraryInformationTarget = identificationTarget.getLibraryInformation();
			String name = libraryInformationTarget.getName();
			String casNumber = libraryInformationTarget.getCasNumber();
			//
			for(IScanMSD reference : massSpectraDatabase.getList()) {
				if(reference instanceof IRegularLibraryMassSpectrum) {
					IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)reference;
					ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
					if(libraryInformation.getName().equals(name)) {
						return reference;
					} else if(libraryInformation.getCasNumber().equals(casNumber)) {
						return reference;
					}
				}
			}
		}
		//
		return null;
	}

	private IMassSpectra getDatabase(String databasePath, IProgressMonitor monitor) throws FileNotFoundException {

		try {
			File file = new File(databasePath);
			if(file.exists()) {
				/*
				 * Make further checks.
				 */
				if(massSpectraDatabase == null) {
					loadMassSpectraFromFile(file, monitor);
				} else {
					/*
					 * Has the content been edited?
					 */
					if(file.length() != fileSize || !file.getName().equals(fileName)) {
						loadMassSpectraFromFile(file, monitor);
					}
				}
			} else {
				massSpectraDatabase = null;
			}
		} catch(TypeCastException e) {
			logger.warn(e);
		}
		//
		if(massSpectraDatabase == null) {
			throw new FileNotFoundException();
		}
		//
		return massSpectraDatabase;
	}

	private void loadMassSpectraFromFile(File file, IProgressMonitor monitor) throws TypeCastException {

		IMassSpectrumImportConverterProcessingInfo infoConvert = MassSpectrumConverter.convert(file, PreferenceSupplier.CONVERTER_ID, monitor);
		massSpectraDatabase = infoConvert.getMassSpectra();
		fileName = file.getName();
		fileSize = file.length();
	}

	// TODO Merge
	public IMassSpectrumTarget getMassSpectrumTarget(IScanMSD reference, IMassSpectrumComparisonResult comparisonResult) {

		String name = "???";
		String cas = "???";
		String comments = "???";
		//
		if(reference instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)reference;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			name = libraryInformation.getName();
			cas = libraryInformation.getCasNumber();
			comments = libraryInformation.getComments();
		}
		IMassSpectrumTarget identificationEntry = null;
		ILibraryInformation libraryInformation;
		/*
		 * Get the library information.
		 */
		libraryInformation = new MassSpectrumLibraryInformation();
		libraryInformation.setName(name);
		libraryInformation.setCasNumber(cas);
		libraryInformation.setMiscellaneous(comments);
		//
		try {
			identificationEntry = new MassSpectrumTarget(libraryInformation, comparisonResult);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		identificationEntry.setIdentifier(IDENTIFIER);
		return identificationEntry;
	}

	// TODO Merge
	public IPeakTarget getPeakTarget(IScanMSD reference, IMassSpectrumComparisonResult comparisonResult) {

		String name = "???";
		String cas = "???";
		String comments = "???";
		//
		if(reference instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)reference;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			name = libraryInformation.getName();
			cas = libraryInformation.getCasNumber();
			comments = libraryInformation.getComments();
		}
		IPeakTarget identificationEntry = null;
		ILibraryInformation libraryInformation;
		/*
		 * Get the library information.
		 */
		libraryInformation = new PeakLibraryInformation();
		libraryInformation.setName(name);
		libraryInformation.setCasNumber(cas);
		libraryInformation.setMiscellaneous(comments);
		//
		try {
			identificationEntry = new PeakTarget(libraryInformation, comparisonResult);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		identificationEntry.setIdentifier(IDENTIFIER);
		return identificationEntry;
	}
}
