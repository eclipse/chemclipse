/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - use dedicated NISTIdentificationTarget for improved performance
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.TargetCombinedComparator;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationResults;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResult;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationResult;
import org.eclipse.chemclipse.model.implementation.IdentificationResults;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.ICompound;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.ICompounds;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.IHit;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.NistResultFileParser;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.HLMFilenameFilter;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.IExtendedRuntimeSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.INistSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.RuntimeSupportFactory;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.INistSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.model.implementation.PeakIdentificationResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;

public class Identifier {

	private static final Logger logger = Logger.getLogger(Identifier.class);
	//
	private static final String MSL_CONVERTER_ID = "org.eclipse.chemclipse.msd.converter.supplier.amdis.massspectrum.msl";
	private static final String MSP_CONVERTER_ID = "org.eclipse.chemclipse.msd.converter.supplier.amdis.massspectrum.msp";
	private static final String DESCRIPTION = "NIST Peak Identifier";
	private static final String LIBRARY = "NIST";
	private static final String COMPOUND_IN_LIB_FACTOR = "InLib Factor: ";
	//
	private final TargetCombinedComparator targetCombinedComparator;
	//
	/*
	 * Mass Spectrum/Peak Identifier
	 */
	private static final String PROCESS_ID = "ID-";
	private static final String BACKUP_CONTROL_EXTENSION = ".openchrom.bak";

	public Identifier() {
		targetCombinedComparator = new TargetCombinedComparator(SortOrder.DESC);
	}

	/**
	 * Performs a mass spectrum identification.
	 *
	 * @param massSpectrumList
	 * @param massSpectrumIdentifierSettings
	 * @param monitor
	 * @return IMassSpectrumIdentificationResults
	 * @throws FileNotFoundException
	 */
	public IMassSpectra runMassSpectrumIdentification(List<IScanMSD> massSpectrumList, MassSpectrumIdentifierSettings massSpectrumIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		IIdentificationResults identificationResults = new IdentificationResults();
		IMassSpectra massSpectra = new MassSpectra();
		/*
		 * Get the OS NIST support. Use Wine in a non MS-Windows system.
		 */
		File nistFolder = PreferenceSupplier.getNistInstallationFolder();
		IStatus status = PreferenceSupplier.validateLocation(nistFolder);
		if(status.isOK()) {
			IExtendedRuntimeSupport runtimeSupport = RuntimeSupportFactory.getRuntimeSupport(nistFolder);
			setNumberOfTargetsToReport(runtimeSupport, massSpectrumIdentifierSettings.getNumberOfTargets(), monitor);
			/*
			 * Get the mass spectra and label them.
			 */
			Map<String, String> identifierTable = setMassSpectrumIdentifier(massSpectrumList);
			/*
			 * Remove unnecessary files. Afterwards, prepare the peaks and analyze
			 * them using the NIST application.
			 */
			logger.info("Backup control files.");
			backupControlFiles(runtimeSupport);
			logger.info("Clean files.");
			cleanFiles(runtimeSupport, monitor);
			//
			try {
				/*
				 * At least 1 mass spectrum is needed.
				 */
				logger.info("Get the mass spectra.");
				massSpectra = getMassSpectra(massSpectrumList);
				if(massSpectra.size() > 0) {
					int numberOfUnknownEntriesToProcess = massSpectra.size();
					logger.info("Process: " + numberOfUnknownEntriesToProcess);
					runtimeSupport.getNistSupport().setNumberOfUnknownEntriesToProcess(numberOfUnknownEntriesToProcess);
					logger.info("Prepare");
					prepareFiles(runtimeSupport, massSpectra, monitor);
					long maxProcessTime = (long)(massSpectrumIdentifierSettings.getTimeoutInMinutes() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					logger.info("Max Process time: " + maxProcessTime);
					logger.info("Run Identification");
					ICompounds compounds = runNistApplication(runtimeSupport, maxProcessTime, monitor);
					logger.info("Assign Compounds");
					identificationResults = assignMassSpectrumCompounds(compounds.getCompounds(), massSpectrumList, identificationResults, massSpectrumIdentifierSettings, identifierTable, monitor);
				}
			} catch(FileIsNotWriteableException e) {
				logger.warn(e);
			} catch(IOException e) {
				logger.warn(e);
			} catch(NoConverterAvailableException e) {
				logger.warn(e);
			}
			/*
			 * Clean temporary files finally to not pollute the workspace for other
			 * applications.
			 */
			resetMassSpectrumIdentifier(massSpectrumList, identifierTable);
			cleanFiles(runtimeSupport, monitor);
			restoreControlFiles(runtimeSupport);
			//
		}
		return massSpectra;
	}

	/**
	 * Performs a peak identification.
	 *
	 * @param peaks
	 * @param peakIdentifierSettings
	 * @param monitor
	 * @return IPeakIdentificationResults
	 * @throws FileNotFoundException
	 */
	public IPeakIdentificationResults runPeakIdentification(List<? extends IPeakMSD> peaks, PeakIdentifierSettings peakIdentifierSettings, IProcessingInfo<?> processingInfo, IProgressMonitor monitor) throws FileNotFoundException {

		IPeakIdentificationResults identificationResults = new PeakIdentificationResults();
		/*
		 * Get the OS NIST support. Use Wine in a non MS-Windows system.
		 */
		File nistFolder = PreferenceSupplier.getNistInstallationFolder();
		IStatus status = PreferenceSupplier.validateLocation(nistFolder);
		if(status.isOK()) {
			IExtendedRuntimeSupport runtimeSupport = RuntimeSupportFactory.getRuntimeSupport(nistFolder);
			setNumberOfTargetsToReport(runtimeSupport, peakIdentifierSettings.getNumberOfTargets(), monitor);
			/*
			 * Get the mass spectra and label them.
			 */
			Map<String, String> identifierTable = setPeakIdentifier(peaks);
			/*
			 * Remove unnecessary files. Afterwards, prepare the peaks and analyze
			 * them using the NIST application.
			 */
			backupControlFiles(runtimeSupport);
			cleanFiles(runtimeSupport, monitor);
			//
			try {
				/*
				 * At least 1 mass spectrum is needed.
				 */
				IMassSpectra massSpectra = getMassSpectraFromPeakList(peaks);
				if(massSpectra.size() > 0) {
					int numberOfUnknownEntriesToProcess = massSpectra.size();
					runtimeSupport.getNistSupport().setNumberOfUnknownEntriesToProcess(numberOfUnknownEntriesToProcess);
					prepareFiles(runtimeSupport, massSpectra, monitor);
					long maxProcessTime = (long)(peakIdentifierSettings.getTimeoutInMinutes() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					ICompounds compounds = runNistApplication(runtimeSupport, maxProcessTime, monitor);
					identificationResults = assignPeakCompounds(compounds, peaks, identificationResults, peakIdentifierSettings, identifierTable, processingInfo, monitor);
				}
			} catch(FileIsNotWriteableException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "The peaks couldn't be identified, caused by a file is not writeable exception.");
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "The peaks couldn't be identified, caused by a IO exception.");
			} catch(NoConverterAvailableException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "The peaks couldn't be identified, caused the converter to write the mass spectrum was not available.");
			}
			/*
			 * Clean temporary files finally to not pollute the workspace for other
			 * applications.
			 */
			resetPeakIdentifier(peaks, identifierTable);
			cleanFiles(runtimeSupport, monitor);
			restoreControlFiles(runtimeSupport);
			//
		}
		return identificationResults;
	}

	public void openNistForPeakIdentification(List<IPeakMSD> peaks, INistSettings nistSettings, IProgressMonitor monitor) throws FileNotFoundException {

		IMassSpectra massSpectra = getMassSpectraFromPeakList(peaks);
		openNistForMassSpectrumIdentification(massSpectra, nistSettings, monitor);
	}

	public void openNistForMassSpectrumIdentification(IMassSpectra massSpectra, INistSettings nistSettings, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * Get the OS NIST support. Use Wine in a non MS-Windows system.
		 */
		File nistFolder = PreferenceSupplier.getNistInstallationFolder();
		IStatus status = PreferenceSupplier.validateLocation(nistFolder);
		if(status.isOK()) {
			IExtendedRuntimeSupport runtimeSupport = RuntimeSupportFactory.getRuntimeSupport(nistFolder);
			File file = new File(runtimeSupport.getApplicationPath() + File.separator + PreferenceSupplier.MSP_EXPORT_FILE_NAME);
			/*
			 * Get the mass spectra and label them.
			 */
			List<IScanMSD> massSpectrumList = getMassSpectra(massSpectra);
			Map<String, String> identifierTable = setMassSpectrumIdentifier(massSpectrumList);
			/*
			 * Remove unnecessary files. Afterwards, prepare the peaks/mass spectra
			 * and analyze them using the NIST GUI application.
			 */
			backupControlFiles(runtimeSupport);
			cleanFiles(runtimeSupport, monitor);
			prepareMSPFile(file, massSpectra, monitor);
			openNistApplication(runtimeSupport, monitor);
			/*
			 * Clean temporary files finally to not pollute the workspace for other
			 * applications.
			 */
			resetMassSpectrumIdentifier(massSpectrumList, identifierTable);
			cleanFiles(runtimeSupport, monitor);
			restoreControlFiles(runtimeSupport);
		}
	}

	private Map<String, String> setMassSpectrumIdentifier(List<IScanMSD> massSpectrumList) {

		Map<String, String> identifierTable = new HashMap<>();
		int id = 1;
		for(IScanMSD massSpectrum : massSpectrumList) {
			setIdentifier(massSpectrum, id++, identifierTable);
		}
		return identifierTable;
	}

	private void resetMassSpectrumIdentifier(List<IScanMSD> massSpectrumList, Map<String, String> identifierTable) {

		for(IScanMSD massSpectrum : massSpectrumList) {
			resetIdentifier(massSpectrum, identifierTable);
		}
	}

	private IMassSpectra getMassSpectra(List<IScanMSD> massSpectrumList) {

		IMassSpectra massSpectra = new MassSpectra();
		for(IScanMSD massSpectrum : massSpectrumList) {
			massSpectra.addMassSpectrum(massSpectrum);
		}
		return massSpectra;
	}

	private List<IScanMSD> getMassSpectra(IMassSpectra massSpectra) {

		List<IScanMSD> massSpectrumList = new ArrayList<>();
		for(int i = 1; i <= massSpectra.size(); i++) {
			massSpectrumList.add(massSpectra.getMassSpectrum(i));
		}
		return massSpectrumList;
	}

	private IMassSpectra getMassSpectraFromPeakList(List<? extends IPeakMSD> peaks) {

		IMassSpectra massSpectra = new MassSpectra();
		for(IPeakMSD peak : peaks) {
			IScanMSD massSpectrum = peak.getExtractedMassSpectrum();
			massSpectra.addMassSpectrum(massSpectrum);
		}
		return massSpectra;
	}

	private Map<String, String> setPeakIdentifier(List<? extends IPeakMSD> peaks) {

		Map<String, String> identifierTable = new HashMap<>();
		int id = 1;
		for(IPeakMSD peak : peaks) {
			IScanMSD massSpectrum = peak.getExtractedMassSpectrum();
			setIdentifier(massSpectrum, id++, identifierTable);
		}
		return identifierTable;
	}

	private void resetPeakIdentifier(List<? extends IPeakMSD> peaks, Map<String, String> identifierTable) {

		for(IPeakMSD peak : peaks) {
			IScanMSD massSpectrum = peak.getExtractedMassSpectrum();
			resetIdentifier(massSpectrum, identifierTable);
		}
	}

	private void setIdentifier(IScanMSD massSpectrum, int id, Map<String, String> identifierTable) {

		String processIdentifier = PROCESS_ID + id;
		String identifier = massSpectrum.getIdentifier();
		identifierTable.put(processIdentifier, identifier);
		massSpectrum.setIdentifier(processIdentifier);
	}

	private void resetIdentifier(IScanMSD massSpectrum, Map<String, String> identifierTable) {

		String key = massSpectrum.getIdentifier();
		massSpectrum.setIdentifier(identifierTable.get(key));
	}

	private void setNumberOfTargetsToReport(IExtendedRuntimeSupport runtimeSupport, int numberOfTargets, IProgressMonitor monitor) {

		runtimeSupport.getNistSupport().setNumberOfTargets(numberOfTargets);
	}

	// -----------------------------------------------private methods
	/**
	 * Deletes unnecessary files.
	 */
	private void cleanFiles(IExtendedRuntimeSupport runtimeSupport, IProgressMonitor monitor) {

		monitor.subTask("Delete unecessary files.");
		INistSupport nistSupport = runtimeSupport.getNistSupport();
		deleteFile(nistSupport.getNistlogFile());
		deleteFile(nistSupport.getSrcreadyFile());
		deleteFile(nistSupport.getSrcresltFile());
		deleteFile(nistSupport.getAutoimpFile());
		deleteFile(nistSupport.getFilespecFile());
		deleteFile(nistSupport.getMassSpectraFile());
		/*
		 * Delete the HLM files.
		 */
		String nistApplicationPath = runtimeSupport.getApplicationPath();
		File directory = new File(nistApplicationPath);
		HLMFilenameFilter hlmFileNameFilter = new HLMFilenameFilter();
		for(String hlmFile : directory.list(hlmFileNameFilter)) {
			deleteFile(nistApplicationPath + File.separator + hlmFile);
			logger.info("Delete: " + nistApplicationPath + File.separator + hlmFile);
		}
	}

	/**
	 * Deletes the file.
	 *
	 * @param path
	 */
	private void deleteFile(String path) {

		File file = new File(path);
		logger.info("Delete: " + path);
		file.delete();
	}

	private void backupControlFiles(IExtendedRuntimeSupport runtimeSupport) {

		INistSupport nistSupport = runtimeSupport.getNistSupport();
		/*
		 * autoimp.msd
		 */
		File autoimpFile = new File(nistSupport.getAutoimpFile());
		logger.info("autoimp.msd: " + autoimpFile.getAbsolutePath());
		File autoimpFileBackup = new File(nistSupport.getAutoimpFile() + BACKUP_CONTROL_EXTENSION);
		if(autoimpFile.exists()) {
			boolean statusRename = autoimpFile.renameTo(autoimpFileBackup);
			logger.info("Status backup autoimp.msd: " + statusRename);
		}
		/*
		 * filespec.fil
		 */
		File filespecFile = new File(nistSupport.getFilespecFile());
		logger.info("filespec.fil: " + filespecFile.getAbsolutePath());
		File filespecFileBackup = new File(nistSupport.getFilespecFile() + BACKUP_CONTROL_EXTENSION);
		if(filespecFile.exists()) {
			boolean statusRename = filespecFile.renameTo(filespecFileBackup);
			logger.info("Status backup filespec.fil: " + statusRename);
		}
	}

	private void restoreControlFiles(IExtendedRuntimeSupport runtimeSupport) {

		INistSupport nistSupport = runtimeSupport.getNistSupport();
		/*
		 * autoimp.msd
		 */
		File autoimpFileBackup = new File(nistSupport.getAutoimpFile() + BACKUP_CONTROL_EXTENSION);
		File autoimpFile = new File(nistSupport.getAutoimpFile());
		if(autoimpFileBackup.exists()) {
			boolean statusRename = autoimpFileBackup.renameTo(autoimpFile);
			logger.info("Status restore autoimp.msd: " + statusRename);
		}
		/*
		 * filespec.fil
		 */
		File filespecFileBackup = new File(nistSupport.getFilespecFile() + BACKUP_CONTROL_EXTENSION);
		File filespecFile = new File(nistSupport.getFilespecFile());
		if(filespecFileBackup.exists()) {
			boolean statusRename = filespecFileBackup.renameTo(filespecFile);
			logger.info("Status restore filespec.fil: " + statusRename);
		}
	}

	/**
	 * Prepares the files.
	 *
	 * @param runtimeSupport
	 * @param monitor
	 * @param peaks
	 * @throws NoConverterAvailableException
	 * @throws IOException
	 * @throws FileIsNotWriteableException
	 * @throws FileNotFoundException
	 */
	private void prepareFiles(IExtendedRuntimeSupport runtimeSupport, IMassSpectra massSpectra, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException, NoConverterAvailableException {

		File file;
		PrintWriter pw;
		INistSupport nistSupport = runtimeSupport.getNistSupport();
		/*
		 * Export the mass spectra (MASSSPECTRA.MSL) file.
		 */
		file = new File(nistSupport.getMassSpectraFile());
		monitor.subTask("Write the peak mass spectra.");
		DatabaseConverter.convert(file, massSpectra, false, MSL_CONVERTER_ID, monitor);
		/*
		 * The AUTOIMP.MSD contains a reference (path) to the FILESPEC.FIL file.
		 */
		pw = new PrintWriter(nistSupport.getAutoimpFile());
		pw.println(nistSupport.getFilespecFile());
		pw.flush();
		pw.close();
		/*
		 * The FILESPEC.FIL contains a reference (path) to the MASSSPECTRA.MSL
		 * file, and additionally an append directive.
		 */
		pw = new PrintWriter(nistSupport.getFilespecFile());
		/*
		 * APPEND - adds the peaks to the existing ones OVERWRITE - overwrites
		 * the stored peaks with the existing ones
		 */
		pw.println(nistSupport.getMassSpectraFile() + " OVERWRITE");
		pw.flush();
		pw.close();
	}

	private void prepareMSPFile(File file, IMassSpectra massSpectra, IProgressMonitor monitor) {

		/*
		 * Export the mass spectra (MASSSPECTRA.MSP) file.
		 */
		monitor.subTask("Write the peak mass spectra to msp file.");
		DatabaseConverter.convert(file, massSpectra, false, MSP_CONVERTER_ID, monitor);
	}

	/**
	 * Runs the NIST application.
	 *
	 * @param runtimeSupport
	 * @param monitor
	 * @throws IOException
	 */
	private ICompounds runNistApplication(final IExtendedRuntimeSupport runtimeSupport, final long maxProcessTime, final IProgressMonitor monitor) throws IOException {

		monitor.subTask("Start the NIST-DB application.");
		runtimeSupport.executeRunCommand();
		try {
			monitor.subTask("Waiting for the result file ... this could take a while.");
			// long actualProcessTime = 0;
			long start = System.currentTimeMillis();
			long max = start + maxProcessTime;
			waitForFile(new File(runtimeSupport.getNistSupport().getSrcreadyFile()), monitor, max);
			logger.info("Process time to get NIST results (ms): " + (System.currentTimeMillis() - start));
			NistResultFileParser nistResultFileParser = new NistResultFileParser();
			File results = new File(runtimeSupport.getNistSupport().getSrcresltFile());
			waitForFile(results, monitor, max);
			return nistResultFileParser.getCompounds(results);
		} finally {
			runtimeSupport.executeKillCommand();
		}
	}

	private void waitForFile(final File file, final IProgressMonitor monitor, long max) throws IOException {

		try {
			// wait for the file to appear
			while(!file.exists()) {
				Thread.sleep(100);
				if(monitor.isCanceled() || System.currentTimeMillis() > max) {
					throw new OperationCanceledException();
				}
			}
			// wait until filesize does not change anymore...
			long fs;
			do {
				fs = file.length();
				TimeUnit.SECONDS.sleep(1);
				if(monitor.isCanceled() || System.currentTimeMillis() > max) {
					throw new OperationCanceledException();
				}
			} while(fs != file.length());
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new OperationCanceledException("interrupted");
		}
	}

	private void openNistApplication(final IExtendedRuntimeSupport runtimeSupport, final IProgressMonitor monitor) {

		monitor.subTask("Start the NIST-DB application.");
		try {
			runtimeSupport.executeOpenCommand();
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	/**
	 * Assign the compounds to the peaks.
	 */
	private IPeakIdentificationResults assignPeakCompounds(ICompounds compounds, List<? extends IPeakMSD> peaks, IPeakIdentificationResults identificationResults, PeakIdentifierSettings peakIdentifierSettings, Map<String, String> identifierTable, IProcessingInfo<?> processingInfo, IProgressMonitor monitor) {

		monitor.subTask("Assign the identified peaks.");
		/*
		 * Add the identification result (all hits for one peak) to the results.
		 */
		identificationResults = getPeakIdentificationResults(compounds, peaks, peakIdentifierSettings, identifierTable, processingInfo);
		return identificationResults;
	}

	/**
	 * Get the result.
	 *
	 * @param peak
	 * @param compound
	 * @param peakIdentifierSettings
	 * @return {@link INistPeakIdentificationResults}
	 */
	public IPeakIdentificationResults getPeakIdentificationResults(ICompounds compounds, List<? extends IPeakMSD> peaks, PeakIdentifierSettings peakIdentifierSettings, Map<String, String> identifierTable, IProcessingInfo<?> processingInfo) {

		IPeakIdentificationResults identificationResults = new PeakIdentificationResults();
		IPeakIdentificationResult identificationResult;
		IIdentificationTarget identificationEntry;
		//
		float minMatchFactor = PreferenceSupplier.getMinMatchFactor();
		float minReverseMatchFactor = PreferenceSupplier.getMinReverseMatchFactor();
		int numberOfTargets = peakIdentifierSettings.getNumberOfTargets();
		//
		for(ICompound compound : compounds.getCompounds()) {
			/*
			 * Each compound has a specific identifier. If a peak couldn't be
			 * identified, no result will be set.
			 */
			String identifier = compound.getIdentfier();
			IPeakMSD peak = getPeakByIdentifier(peaks, identifier);
			if(peak != null) {
				/*
				 * Extract all hits and set the identification result.
				 */
				List<IIdentificationTarget> peakTargets = new ArrayList<>();
				int maxIndex = compound.size();
				identificationResult = new PeakIdentificationResult();
				for(int index = 1; index <= maxIndex; index++) {
					/*
					 * The targets shall not be stored in the peak in all cases.
					 */
					identificationEntry = getPeakIdentificationEntry(compound, index);
					/*
					 * Add only results with values higher than min MF and RMF.
					 */
					IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
					float matchFactor = comparisonResult.getMatchFactor();
					float reverseMatchFactor = comparisonResult.getReverseMatchFactor();
					if(matchFactor >= minMatchFactor && reverseMatchFactor >= minReverseMatchFactor) {
						/*
						 * Store the peak target
						 */
						identificationEntry.setIdentifier(IConstants.NIST_IDENTIFIER);
						if(peakIdentifierSettings.getStoreTargets()) {
							peakTargets.add(identificationEntry);
						}
						identificationResult.add(identificationEntry);
					}
				}
				/*
				 * Assign only the best hits.
				 */
				Collections.sort(peakTargets, targetCombinedComparator);
				int size = numberOfTargets <= peakTargets.size() ? numberOfTargets : peakTargets.size();
				for(int i = 0; i < size; i++) {
					peak.getTargets().add(peakTargets.get(i));
				}
				/*
				 * Add the identification result to the results list.
				 */
				identificationResults.add(identificationResult);
				/*
				 * Processing info
				 */
				IProcessingMessage processingMessage = new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The peak was identified successfully: " + peak.getModelDescription());
				processingInfo.addMessage(processingMessage);
			} else {
				processingInfo.addErrorMessage(DESCRIPTION, "The result id was not set, hence no result could be extracted.");
			}
		}
		return identificationResults;
	}

	private IPeakMSD getPeakByIdentifier(List<? extends IPeakMSD> peaks, String identifier) {

		if(identifier.equals("")) {
			return null;
		} else {
			for(IPeakMSD peak : peaks) {
				if(peak.getExtractedMassSpectrum().getIdentifier().equals(identifier)) {
					return peak;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the identification entry or null if something has gone wrong.
	 *
	 * @param compound
	 * @param index
	 * @return {@link INistPeakIdentificationEntry}
	 */
	public IIdentificationTarget getPeakIdentificationEntry(ICompound compound, int index) {

		IHit hit = compound.getHit(index);
		//
		IIdentificationTarget identificationEntry = null;
		IPeakComparisonResult comparisonResult;
		/*
		 * Get the library information.
		 */
		IPeakLibraryInformation libraryInformation = new PeakLibraryInformation();
		libraryInformation.setName(getName(hit.getName()));
		libraryInformation.setCasNumber(hit.getCAS());
		libraryInformation.setMiscellaneous(COMPOUND_IN_LIB_FACTOR + compound.getCompoundInLibraryFactor());
		libraryInformation.setContributor(LIBRARY);
		libraryInformation.setReferenceIdentifier(LIBRARY);
		/*
		 * Get the match factor and reverse match factor values.
		 */
		comparisonResult = new PeakComparisonResult(hit.getMF(), hit.getRMF(), 0.0f, 0.0f, hit.getProb());
		try {
			identificationEntry = new NISTIdentificationTarget(libraryInformation, comparisonResult);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		return identificationEntry;
	}

	private String getName(String name) {

		name = name.replaceAll("à", ".alpha.");
		name = name.replaceAll("á", ".beta.");
		return name;
	}

	/**
	 * Assigns the mass spectrum compounds.
	 *
	 * @param compounds
	 * @param massSpectra
	 * @param identificationResults
	 * @param massSpectrumIdentifierSettings
	 * @param monitor
	 * @return INistMassSpectrumIdentificationResults
	 */
	private IIdentificationResults assignMassSpectrumCompounds(List<ICompound> compounds, List<IScanMSD> massSpectra, IIdentificationResults identificationResults, MassSpectrumIdentifierSettings massSpectrumIdentifierSettings, Map<String, String> identifier, IProgressMonitor monitor) {

		/*
		 * If the compounds and peaks are different, there must have gone
		 * something wrong.
		 */
		if(compounds.size() != massSpectra.size()) {
			return identificationResults;
		}
		IScanMSD massSpectrum;
		ICompound compound;
		IIdentificationResult identificationResult;
		monitor.subTask("Assign the identified mass spectra.");
		/*
		 * Add the targets to each mass spectrum and to the identification
		 * results list.
		 */
		for(int index = 0; index < massSpectra.size(); index++) {
			/*
			 * Get the peak and corresponding compound.
			 */
			massSpectrum = massSpectra.get(index);
			compound = compounds.get(index);
			/*
			 * Add the identification result (all hits for one peak) to the
			 * results.
			 */
			identificationResult = getMassSpectrumIdentificationResult(massSpectrum, compound, massSpectrumIdentifierSettings);
			identificationResults.add(identificationResult);
		}
		return identificationResults;
	}

	/**
	 * Returns the mass spectrum identification result.
	 *
	 * @param massSpectrum
	 * @param compound
	 * @param massSpectrumIdentifierSettings
	 * @return INistMassSpectrumIdentificationResult
	 */
	public IIdentificationResult getMassSpectrumIdentificationResult(IScanMSD massSpectrum, ICompound compound, MassSpectrumIdentifierSettings massSpectrumIdentifierSettings) {

		int numberOfTargets = massSpectrumIdentifierSettings.getNumberOfTargets();
		List<IIdentificationTarget> massSpectrumTargets = new ArrayList<>();
		IIdentificationResult identificationResult = new IdentificationResult();
		//
		for(int index = 1; index <= compound.size(); index++) {
			/*
			 * The targets shall not be stored in the peak in all cases.
			 */
			IHit hit = compound.getHit(index);
			IIdentificationTarget identificationEntry = getMassSpectrumIdentificationEntry(hit, compound);
			if(massSpectrumIdentifierSettings.getStoreTargets()) {
				massSpectrumTargets.add(identificationEntry);
			}
			identificationResult.add(identificationEntry);
		}
		/*
		 * Assign only the best hits.
		 */
		Collections.sort(massSpectrumTargets, targetCombinedComparator);
		int size = numberOfTargets <= massSpectrumTargets.size() ? numberOfTargets : massSpectrumTargets.size();
		for(int i = 0; i < size; i++) {
			massSpectrum.getTargets().add(massSpectrumTargets.get(i));
		}
		//
		return identificationResult;
	}

	/**
	 * Returns the mass spectrum identification entry.
	 *
	 * @param hit
	 * @return INistMassSpectrumIdentificationEntry
	 */
	public IIdentificationTarget getMassSpectrumIdentificationEntry(IHit hit, ICompound compound) {

		IIdentificationTarget identificationEntry = null;
		ILibraryInformation libraryInformation;
		IComparisonResult comparisonResult;
		/*
		 * Get the library information.
		 */
		libraryInformation = new LibraryInformation();
		libraryInformation.setName(getName(hit.getName()));
		libraryInformation.setCasNumber(hit.getCAS());
		libraryInformation.setMiscellaneous(COMPOUND_IN_LIB_FACTOR + compound.getCompoundInLibraryFactor());
		libraryInformation.setContributor(LIBRARY);
		libraryInformation.setReferenceIdentifier(LIBRARY);
		/*
		 * Get the match factor and reverse match factor values.
		 */
		comparisonResult = new ComparisonResult(hit.getMF(), hit.getRMF(), 0.0f, 0.0f, hit.getProb());
		try {
			identificationEntry = new NISTIdentificationTarget(libraryInformation, comparisonResult);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
		return identificationEntry;
	}
	// -----------------------------------------------private methods
}
