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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
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

	public static final String IDENTIFIER = "File Identifier";
	private static final Logger logger = Logger.getLogger(FileIdentifier.class);
	/*
	 * Don't reload the database on each request, only if neccessary.
	 */
	private static Map<String, Long> fileSizes;
	private static Set<String> fileNames;
	private static Map<String, IMassSpectra> massSpectraDatabases;
	private static Map<String, List<Set<Integer>>> allDatabaseTopIons = null;
	private static Map<String, Map<String, IScanMSD>> allDatabaseNames = null;
	private static Map<String, Map<String, IScanMSD>> allDatabaseCasNumbers = null;
	//
	private static final int NUMBER_TOP_IONS = 12;
	private static final double LIMIT_PERCENTAGE_HITS = 0.1d; // 10% of the highest m/z values must be matched
	//
	private IonAbundanceComparator ionAbundanceComparator;
	private TargetCombinedComparator targetCombinedComparator;

	public FileIdentifier() {
		//
		initializeDatabaseMaps();
		ionAbundanceComparator = new IonAbundanceComparator(SortOrder.DESC);
		targetCombinedComparator = new TargetCombinedComparator(SortOrder.DESC);
	}

	public IMassSpectra runIdentification(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		IMassSpectra massSpectra = new MassSpectra();
		if(massSpectrumIdentifierSettings instanceof IFileMassSpectrumIdentifierSettings) {
			/*
			 * Add all unknowns to the mass spectra list.
			 */
			massSpectra.addMassSpectra(massSpectraList);
			/*
			 * Load the mass spectra database only if the raw file or its content has changed.
			 */
			IFileMassSpectrumIdentifierSettings fileIdentifierSettings = (IFileMassSpectrumIdentifierSettings)massSpectrumIdentifierSettings;
			Map<String, IMassSpectra> databases = getDatabases(fileIdentifierSettings.getMassSpectraFiles(), monitor);
			for(Map.Entry<String, IMassSpectra> database : databases.entrySet()) {
				compareMassSpectraAgainstDatabase(massSpectra, fileIdentifierSettings, database, monitor);
			}
			/*
			 * Add m/z list on demand.
			 */
			for(IScanMSD unknown : massSpectra.getList()) {
				List<IMassSpectrumTarget> massSpectrumTargets = unknown.getTargets();
				if(massSpectrumTargets.size() == 0) {
					if(fileIdentifierSettings.isAddUnknownMzListTarget()) {
						setMassSpectrumTargetUnknown(unknown);
					}
				}
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
		 * Load the mass spectra database only if the raw file or its content has changed.
		 */
		Map<String, IMassSpectra> databases = getDatabases(peakIdentifierSettings.getMassSpectraFiles(), monitor);
		for(Map.Entry<String, IMassSpectra> database : databases.entrySet()) {
			comparePeaksAgainstDatabase(peakIdentifierSettings, peaks, database, monitor);
		}
		/*
		 * Assign a m/z list on demand if no match has been assigned.
		 */
		for(IPeakMSD peakMSD : peaks) {
			if(peakMSD.getTargets().size() == 0) {
				if(peakIdentifierSettings.isAddUnknownMzListTarget()) {
					setPeakTargetUnknown(peakMSD);
				}
			}
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
					Map<String, Map<String, IScanMSD>> allDatabaseNames = getDatabaseNamesMap(monitor);
					Map<String, Map<String, IScanMSD>> allDatabaseCasNumbers = getDatabaseCasNamesMap(monitor);
					String database = identificationTarget.getLibraryInformation().getDatabase();
					Map<String, IScanMSD> databaseNames = allDatabaseNames.get(database);
					Map<String, IScanMSD> databaseCasNumbers = allDatabaseCasNumbers.get(database);
					//
					IScanMSD reference = null;
					/*
					 * Find reference By Name
					 */
					if(databaseNames != null) {
						String name = libraryInformationTarget.getName();
						reference = databaseNames.get(name);
						if(reference != null) {
							massSpectra.addMassSpectrum(reference);
						}
					}
					/*
					 * Find reference by CAS#
					 */
					if(reference == null) {
						if(databaseCasNumbers != null) {
							String casNumber = libraryInformationTarget.getCasNumber();
							reference = databaseCasNumbers.get(casNumber);
							if(reference != null) {
								massSpectra.addMassSpectrum(reference);
							}
						}
					}
				}
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		}
		//
		return massSpectra;
	}

	private void compareMassSpectraAgainstDatabase(IMassSpectra massSpectra, IFileMassSpectrumIdentifierSettings fileIdentifierSettings, Map.Entry<String, IMassSpectra> database, IProgressMonitor monitor) {

		/*
		 * Run the identification.
		 */
		String databaseName = database.getKey();
		List<Set<Integer>> databaseTopIons = allDatabaseTopIons.get(databaseName);
		//
		String comparatorId = fileIdentifierSettings.getMassSpectrumComparatorId();
		float minMatchFactor = fileIdentifierSettings.getMinMatchFactor();
		float minReverseMatchFactor = fileIdentifierSettings.getMinReverseMatchFactor();
		int numberOfTargets = fileIdentifierSettings.getNumberOfTargets();
		//
		int countUnknown = 1;
		for(IScanMSD unknown : massSpectra.getList()) {
			List<IMassSpectrumTarget> massSpectrumTargets = new ArrayList<IMassSpectrumTarget>();
			int countReference = 1;
			/*
			 * Sort the ions of the unknown mass spectrum.
			 */
			List<IIon> ions = unknown.getIons();
			Collections.sort(ions, ionAbundanceComparator);
			//
			List<IScanMSD> references = database.getValue().getList();
			for(int i = 0; i < references.size(); i++) {
				//
				monitor.subTask("Compare " + countUnknown + " / " + countReference++);
				//
				IScanMSD reference = references.get(i);
				Set<Integer> referenceIons = databaseTopIons.get(i);
				if(useReferenceForComparison(ions, referenceIons)) {
					/*
					 * Only compare this spectrum.
					 */
					try {
						IMassSpectrumComparatorProcessingInfo infoCompare = MassSpectrumComparator.compare(unknown, reference, comparatorId);
						IMassSpectrumComparisonResult comparisonResult = infoCompare.getMassSpectrumComparisonResult();
						//
						if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
							/*
							 * Add the target.
							 */
							massSpectrumTargets.add(getMassSpectrumTarget(reference, comparisonResult, databaseName));
						}
					} catch(TypeCastException e1) {
						logger.warn(e1);
					}
				}
			}
			/*
			 * Assign a m/z list on demand if no match has been assigned.
			 */
			if(massSpectrumTargets.size() > 0) {
				Collections.sort(massSpectrumTargets, targetCombinedComparator);
				int size = (numberOfTargets <= massSpectrumTargets.size()) ? numberOfTargets : massSpectrumTargets.size();
				for(int i = 0; i < size; i++) {
					unknown.addTarget(massSpectrumTargets.get(i));
				}
			}
			//
			countUnknown++;
		}
	}

	private void comparePeaksAgainstDatabase(IFilePeakIdentifierSettings peakIdentifierSettings, List<IPeakMSD> peaks, Map.Entry<String, IMassSpectra> database, IProgressMonitor monitor) {

		/*
		 * Run the identification.
		 */
		String databaseName = database.getKey();
		List<Set<Integer>> databaseTopIons = allDatabaseTopIons.get(databaseName);
		//
		String comparatorId = peakIdentifierSettings.getMassSpectrumComparatorId();
		float minMatchFactor = peakIdentifierSettings.getMinMatchFactor();
		float minReverseMatchFactor = peakIdentifierSettings.getMinReverseMatchFactor();
		int numberOfTargets = peakIdentifierSettings.getNumberOfTargets();
		//
		int countUnknown = 1;
		for(IPeakMSD peakMSD : peaks) {
			/*
			 * Sort the ions of the unknown mass spectrum.
			 */
			List<IIon> ions = peakMSD.getExtractedMassSpectrum().getIons();
			Collections.sort(ions, ionAbundanceComparator);
			//
			List<IPeakTarget> peakTargets = new ArrayList<IPeakTarget>();
			int countReference = 1;
			IScanMSD unknown = peakMSD.getPeakModel().getPeakMassSpectrum();
			List<IScanMSD> references = database.getValue().getList();
			for(int i = 0; i < references.size(); i++) {
				//
				monitor.subTask("Compare " + countUnknown + " / " + countReference++);
				//
				IScanMSD reference = references.get(i);
				Set<Integer> referenceIons = databaseTopIons.get(i);
				if(useReferenceForComparison(ions, referenceIons)) {
					/*
					 * Only compare this spectrum.
					 */
					try {
						IMassSpectrumComparatorProcessingInfo infoCompare = MassSpectrumComparator.compare(unknown, reference, comparatorId);
						IMassSpectrumComparisonResult comparisonResult = infoCompare.getMassSpectrumComparisonResult();
						//
						if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
							/*
							 * Add the target.
							 */
							peakTargets.add(getPeakTarget(reference, comparisonResult, database.getKey()));
						}
					} catch(TypeCastException e1) {
						logger.warn(e1);
					}
				}
			}
			/*
			 * Assign only the best hits or the m/z list of the unknown.
			 */
			if(peakTargets.size() > 0) {
				Collections.sort(peakTargets, targetCombinedComparator);
				int size = (numberOfTargets <= peakTargets.size()) ? numberOfTargets : peakTargets.size();
				for(int i = 0; i < size; i++) {
					peakMSD.addTarget(peakTargets.get(i));
				}
			}
			//
			countUnknown++;
		}
	}

	private Map<String, IMassSpectra> getDatabases(List<String> databaseList, IProgressMonitor monitor) throws FileNotFoundException {

		List<String> databaseNames = new ArrayList<String>();
		for(String database : databaseList) {
			try {
				File file = new File(database);
				String databaseName = file.getName();
				databaseNames.add(databaseName);
				if(file.exists()) {
					/*
					 * Make further checks.
					 */
					if(massSpectraDatabases.get(databaseName) == null) {
						loadMassSpectraFromFile(file, monitor);
					} else {
						/*
						 * Has the content been edited?
						 */
						if(file.length() != fileSizes.get(databaseName) || !fileNames.contains(databaseName)) {
							loadMassSpectraFromFile(file, monitor);
						}
					}
				}
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		}
		/*
		 * Remove unused databases and info maps.
		 */
		Set<String> databaseKeys = massSpectraDatabases.keySet();
		for(String databaseKey : databaseKeys) {
			if(!databaseNames.contains(databaseKey)) {
				massSpectraDatabases.remove(databaseKey);
				allDatabaseNames.remove(databaseKey);
				allDatabaseCasNumbers.remove(databaseKey);
				allDatabaseTopIons.remove(databaseKey);
			}
		}
		/*
		 * Post-check
		 */
		if(massSpectraDatabases.size() == 0) {
			throw new FileNotFoundException();
		}
		//
		return massSpectraDatabases;
	}

	private Map<String, Map<String, IScanMSD>> getDatabaseNamesMap(IProgressMonitor monitor) throws FileNotFoundException {

		getDatabases(PreferenceSupplier.getMassSpectraFiles(), monitor);
		return allDatabaseNames;
	}

	private Map<String, Map<String, IScanMSD>> getDatabaseCasNamesMap(IProgressMonitor monitor) throws FileNotFoundException {

		getDatabases(PreferenceSupplier.getMassSpectraFiles(), monitor);
		return allDatabaseCasNumbers;
	}

	private void loadMassSpectraFromFile(File file, IProgressMonitor monitor) throws TypeCastException {

		IMassSpectrumImportConverterProcessingInfo infoConvert = MassSpectrumConverter.convert(file, monitor);
		IMassSpectra massSpectraDatabase = infoConvert.getMassSpectra();
		/*
		 * Add the datababse to databases.
		 */
		String databaseName = file.getName();
		massSpectraDatabases.put(databaseName, massSpectraDatabase);
		fileNames.add(databaseName);
		fileSizes.put(databaseName, file.length());
		/*
		 * Initialize the reference maps.
		 */
		Map<String, IScanMSD> databaseNames = allDatabaseNames.get(databaseName);
		if(databaseNames == null) {
			databaseNames = new HashMap<String, IScanMSD>();
			allDatabaseNames.put(databaseName, databaseNames);
		}
		//
		Map<String, IScanMSD> databaseCasNumbers = allDatabaseCasNumbers.get(databaseName);
		if(databaseCasNumbers == null) {
			databaseCasNumbers = new HashMap<String, IScanMSD>();
			allDatabaseCasNumbers.put(databaseName, databaseCasNumbers);
		}
		List<Set<Integer>> databaseTopIons = allDatabaseTopIons.get(databaseName);
		if(databaseTopIons == null) {
			databaseTopIons = new ArrayList<Set<Integer>>();
			allDatabaseTopIons.put(databaseName, databaseTopIons);
		}
		//
		for(IScanMSD reference : massSpectraDatabase.getList()) {
			/*
			 * Extract the list of n top ions.
			 */
			List<IIon> ions = reference.getIons();
			Collections.sort(ions, ionAbundanceComparator);
			Set<Integer> referenceTopIons = new HashSet<Integer>();
			databaseTopIons.add(referenceTopIons);
			int size = (ions.size() < NUMBER_TOP_IONS) ? ions.size() : NUMBER_TOP_IONS;
			for(int i = 0; i < size; i++) {
				referenceTopIons.add((int)AbstractIon.getIon(ions.get(i).getIon(), 0));
			}
			//
			if(reference instanceof IRegularLibraryMassSpectrum) {
				IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)reference;
				ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
				databaseNames.put(libraryInformation.getName(), reference);
				databaseCasNumbers.put(libraryInformation.getCasNumber(), reference);
			}
		}
	}

	// TODO Merge
	private IMassSpectrumTarget getMassSpectrumTarget(IScanMSD reference, IMassSpectrumComparisonResult comparisonResult, String database) {

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
		libraryInformation.setDatabase(database);
		//
		try {
			identificationEntry = new MassSpectrumTarget(libraryInformation, comparisonResult);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		identificationEntry.setIdentifier(IDENTIFIER);
		return identificationEntry;
	}

	private void setMassSpectrumTargetUnknown(IScanMSD unknown) {

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
	private IPeakTarget getPeakTarget(IScanMSD reference, IMassSpectrumComparisonResult comparisonResult, String database) {

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
		libraryInformation.setDatabase(database);
		//
		try {
			peakTarget = new PeakTarget(libraryInformation, comparisonResult);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		peakTarget.setIdentifier(IDENTIFIER);
		return peakTarget;
	}

	private void setPeakTargetUnknown(IPeakMSD peakMSD) {

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

	private boolean useReferenceForComparison(List<IIon> ions, Set<Integer> referenceIons) {

		int hits = 0;
		int size = (ions.size() < NUMBER_TOP_IONS) ? ions.size() : NUMBER_TOP_IONS;
		if(size > 0) {
			for(int j = 0; j < size; j++) {
				int mz = (int)AbstractIon.getIon(ions.get(j).getIon(), 0);
				if(referenceIons.contains(mz)) {
					hits++;
				}
			}
			//
			double percentageHits = hits / (double)size;
			if(percentageHits >= LIMIT_PERCENTAGE_HITS) {
				return true;
			}
		}
		//
		return false;
	}

	private void initializeDatabaseMaps() {

		if(fileSizes == null) {
			fileSizes = new HashMap<String, Long>();
		}
		//
		if(fileNames == null) {
			fileNames = new HashSet<String>();
		}
		//
		if(massSpectraDatabases == null) {
			massSpectraDatabases = new HashMap<String, IMassSpectra>();
		}
		//
		if(allDatabaseTopIons == null) {
			allDatabaseTopIons = new HashMap<String, List<Set<Integer>>>();
		}
		//
		if(allDatabaseNames == null) {
			allDatabaseNames = new HashMap<String, Map<String, IScanMSD>>();
		}
		//
		if(allDatabaseCasNumbers == null) {
			allDatabaseCasNumbers = new HashMap<String, Map<String, IScanMSD>>();
		}
	}
}
