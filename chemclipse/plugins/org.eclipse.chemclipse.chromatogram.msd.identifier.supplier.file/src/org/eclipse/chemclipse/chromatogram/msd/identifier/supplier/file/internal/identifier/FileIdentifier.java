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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFilePeakIdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.SortOrder;
import org.eclipse.chemclipse.model.comparator.TargetCombinedComparator;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
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
	//
	private static IMassSpectra massSpectraDatabase = null;
	private static Map<String, IScanMSD> databaseNames = null;
	private static Map<String, IScanMSD> databaseCasNumbers = null;
	//
	private IonAbundanceComparator ionAbundanceComparator;
	private TargetCombinedComparator targetCombinedComparator;

	public FileIdentifier() {
		ionAbundanceComparator = new IonAbundanceComparator(SortOrder.DESC);
		targetCombinedComparator = new TargetCombinedComparator(SortOrder.DESC);
	}

	public IMassSpectra runIdentification(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		IMassSpectra massSpectra = new MassSpectra();
		//
		if(massSpectrumIdentifierSettings instanceof IFileMassSpectrumIdentifierSettings) {
			IFileMassSpectrumIdentifierSettings fileIdentifierSettings = (IFileMassSpectrumIdentifierSettings)massSpectrumIdentifierSettings;
			/*
			 * Run the identification.
			 */
			String comparatorId = fileIdentifierSettings.getMassSpectrumComparatorId();
			float minMatchFactor = fileIdentifierSettings.getMinMatchFactor();
			float minReverseMatchFactor = fileIdentifierSettings.getMinReverseMatchFactor();
			int numberOfTargets = fileIdentifierSettings.getNumberOfTargets();
			/*
			 * Load the mass spectra database only if the raw file or its content has changed.
			 */
			IMassSpectra database = getDatabase(fileIdentifierSettings.getMassSpectraFile(), monitor);
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
				 * Assign only the best hits or the m/z list of the unknown.
				 */
				if(massSpectrumTargets.size() == 0) {
					if(fileIdentifierSettings.isAddUnknownMzListTarget()) {
						setMassSpectrumTargetUnknown(unknown);
					}
				} else {
					Collections.sort(massSpectrumTargets, targetCombinedComparator);
					int size = (numberOfTargets <= massSpectrumTargets.size()) ? numberOfTargets : massSpectrumTargets.size();
					for(int i = 0; i < size; i++) {
						unknown.addTarget(massSpectrumTargets.get(i));
					}
				}
				//
				massSpectra.addMassSpectrum(unknown);
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
			 * Assign only the best hits or the m/z list of the unknown.
			 */
			if(peakTargets.size() == 0) {
				if(peakIdentifierSettings.isAddUnknownMzListTarget()) {
					setPeakTargetUnknown(peakMSD);
				}
			} else {
				Collections.sort(peakTargets, targetCombinedComparator);
				int size = (numberOfTargets <= peakTargets.size()) ? numberOfTargets : peakTargets.size();
				for(int i = 0; i < size; i++) {
					peakMSD.addTarget(peakTargets.get(i));
				}
			}
			//
			countUnknown++;
		}
		//
		return identificationResults;
	}

	public IMassSpectra getMassSpectra(IIdentificationTarget identificationTarget, IProgressMonitor monitor) {

		IMassSpectra massSpectra = new MassSpectra();
		try {
			//
			if(identificationTarget != null) {
				/*
				 * Extract the target library information.
				 */
				ILibraryInformation libraryInformationTarget = identificationTarget.getLibraryInformation();
				if(identificationTarget.getIdentifier().equals(IDENTIFIER)) {
					/*
					 * Only evaluate the target if it contains the signature
					 * of this plugin.
					 */
					Map<String, IScanMSD> databaseNames = getDatabaseNamesMap(monitor);
					Map<String, IScanMSD> databaseCasNumbers = getDatabaseCasNamesMap(monitor);
					//
					IScanMSD reference;
					String name = libraryInformationTarget.getName();
					//
					reference = databaseNames.get(name);
					if(reference != null) {
						massSpectra.addMassSpectrum(reference);
					}
					//
					String casNumber = libraryInformationTarget.getCasNumber();
					reference = databaseCasNumbers.get(casNumber);
					if(reference != null) {
						massSpectra.addMassSpectrum(reference);
					}
				}
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		}
		//
		return massSpectra;
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

	private Map<String, IScanMSD> getDatabaseNamesMap(IProgressMonitor monitor) throws FileNotFoundException {

		getDatabase(PreferenceSupplier.getMassSpectraFile(), monitor);
		return databaseNames;
	}

	private Map<String, IScanMSD> getDatabaseCasNamesMap(IProgressMonitor monitor) throws FileNotFoundException {

		getDatabase(PreferenceSupplier.getMassSpectraFile(), monitor);
		return databaseCasNumbers;
	}

	private void loadMassSpectraFromFile(File file, IProgressMonitor monitor) throws TypeCastException {

		IMassSpectrumImportConverterProcessingInfo infoConvert = MassSpectrumConverter.convert(file, monitor);
		massSpectraDatabase = infoConvert.getMassSpectra();
		//
		fileName = file.getName();
		fileSize = file.length();
		/*
		 * Initialize the reference maps.
		 */
		databaseNames = new HashMap<String, IScanMSD>();
		databaseCasNumbers = new HashMap<String, IScanMSD>();
		for(IScanMSD reference : massSpectraDatabase.getList()) {
			if(reference instanceof IRegularLibraryMassSpectrum) {
				IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)reference;
				ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
				databaseNames.put(libraryInformation.getName(), reference);
				databaseCasNumbers.put(libraryInformation.getCasNumber(), reference);
			}
		}
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

	public void setMassSpectrumTargetUnknown(IScanMSD unknown) {

		try {
			ILibraryInformation libraryInformation = getLibraryInformationUnknown(unknown.getIons());
			IComparisonResult comparisonResult = getComparisonResultUnknown();
			IMassSpectrumTarget massSpectrumTarget = new MassSpectrumTarget(libraryInformation, comparisonResult);
			massSpectrumTarget.setIdentifier(IDENTIFIER);
			unknown.addTarget(massSpectrumTarget);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
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
		IPeakTarget peakTarget = null;
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
			peakTarget = new PeakTarget(libraryInformation, comparisonResult);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		peakTarget.setIdentifier(IDENTIFIER);
		return peakTarget;
	}

	public void setPeakTargetUnknown(IPeakMSD peakMSD) {

		try {
			IScanMSD unknown = peakMSD.getExtractedMassSpectrum();
			ILibraryInformation libraryInformation = getLibraryInformationUnknown(unknown.getIons());
			IComparisonResult comparisonResult = getComparisonResultUnknown();
			IPeakTarget peakTarget = new PeakTarget(libraryInformation, comparisonResult);
			peakTarget.setIdentifier(IDENTIFIER);
			peakMSD.addTarget(peakTarget);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
	}

	private IComparisonResult getComparisonResultUnknown() {

		return new ComparisonResult(100.0f, 100.0f);
	}

	private ILibraryInformation getLibraryInformationUnknown(List<IIon> ions) {

		ILibraryInformation libraryInformation = new LibraryInformation();
		Collections.sort(ions, ionAbundanceComparator);
		StringBuilder builder = new StringBuilder();
		builder.append("Unknown [");
		int size = (ions.size() >= 5) ? 5 : ions.size();
		for(int i = 0; i < size; i++) {
			builder.append((int)ions.get(i).getIon());
			if(i < size - 1) {
				builder.append(",");
			}
		}
		builder.append("]");
		libraryInformation.setName(builder.toString());
		return libraryInformation;
	}
}
