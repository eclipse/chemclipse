/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to API Changes
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.io.IFileHelper;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.io.ChromatogramReaderCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IMethod;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ChromatogramComparisonResult;
import org.eclipse.chemclipse.model.identifier.ChromatogramLibraryInformation;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IChromatogramLibraryInformation;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.QuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.InternalStandard;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io.ChromatogramReaderMSD;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io.IChromatogramMSDZipReader;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io.IReaderProxy;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorScanProxy;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorScanProxy;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonTransitionIsNullException;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io.ChromatogramReaderWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.BaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IBaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramReader_1301 extends AbstractChromatogramReader implements IChromatogramMSDZipReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader_1301.class);

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSD chromatogram = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				// monitor.subTask(IConstants.IMPORT_CHROMATOGRAM);
				chromatogram = readFromZipFile(zipFile, "", file, monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return chromatogram;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramOverview chromatogramOverview = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				chromatogramOverview = readOverviewFromZipFile(zipFile, "", monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return chromatogramOverview;
	}

	@Override
	public IChromatogramMSD read(ZipInputStream zipInputStream, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		return readZipData(zipInputStream, directoryPrefix, null, monitor);
	}

	@Override
	public IChromatogramMSD read(ZipFile zipFile, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		return readFromZipFile(zipFile, directoryPrefix, null, monitor);
	}

	private IChromatogramMSD readFromZipFile(ZipFile zipFile, String directoryPrefix, File file, IProgressMonitor monitor) throws IOException {

		return readZipData(zipFile, directoryPrefix, file, monitor);
	}

	/*
	 * Object = ZipFile or ZipInputStream
	 * @param object
	 * @param file
	 * @return
	 */
	private IChromatogramMSD readZipData(Object object, String directoryPrefix, File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Read Chromatogram", 100);
		try {
			boolean closeStream;
			boolean useScanProxies;
			//
			if(object instanceof ZipFile) {
				/*
				 * ZipFile
				 */
				closeStream = true;
				IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
				useScanProxies = preferences.getBoolean(PreferenceSupplier.P_USE_SCAN_PROXIES, PreferenceSupplier.DEF_USE_SCAN_PROXIES);
			} else if(object instanceof ZipInputStream) {
				/*
				 * ZipInputStream
				 */
				closeStream = false;
				useScanProxies = false;
			} else {
				return null;
			}
			//
			readMethod(getDataInputStream(object, directoryPrefix + IFormat.FILE_SYSTEM_SETTINGS_MSD), closeStream, chromatogram);
			if(useScanProxies) {
				readScanProxies((ZipFile)object, directoryPrefix, file, chromatogram);
			} else {
				readScans(getDataInputStream(object, directoryPrefix + IFormat.FILE_SCANS_MSD), closeStream, chromatogram);
			}
			readBaseline(getDataInputStream(object, directoryPrefix + IFormat.FILE_BASELINE_MSD), closeStream, chromatogram);
			subMonitor.worked(20);
			readPeaks(getDataInputStream(object, directoryPrefix + IFormat.FILE_PEAKS_MSD), closeStream, chromatogram);
			readArea(getDataInputStream(object, directoryPrefix + IFormat.FILE_AREA_MSD), closeStream, chromatogram);
			subMonitor.worked(20);
			readIdentification(getDataInputStream(object, directoryPrefix + IFormat.FILE_IDENTIFICATION_MSD), closeStream, chromatogram);
			readHistory(getDataInputStream(object, directoryPrefix + IFormat.FILE_HISTORY_MSD), closeStream, chromatogram);
			subMonitor.worked(20);
			readMiscellaneous(getDataInputStream(object, directoryPrefix + IFormat.FILE_MISC_MSD), closeStream, chromatogram);
			readSeparationColumn(getDataInputStream(object, directoryPrefix + IFormat.FILE_SEPARATION_COLUMN_MSD), closeStream, chromatogram);
			setAdditionalInformation(file, chromatogram);
			subMonitor.worked(20);
			//
			try {
				/*
				 * Read the referenced chromatograms.
				 * Get the size could lead to an exception if no reference info is stored.
				 */
				int size = readChromatogramReferenceInfo(getDataInputStream(object, directoryPrefix + IFormat.FILE_REFERENCE_INFO), closeStream);
				readReferencedChromatograms(object, directoryPrefix, chromatogram, size, closeStream, monitor);
				subMonitor.worked(20);
			} catch(IOException e) {
				logger.info(e);
			}
		} finally {
			SubMonitor.done(subMonitor);
		}
		//
		return chromatogram;
	}

	private void readMethod(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		IMethod method = chromatogram.getMethod();
		//
		method.setInstrumentName(readString(dataInputStream));
		method.setIonSource(readString(dataInputStream));
		method.setSamplingRate(dataInputStream.readDouble());
		method.setSolventDelay(dataInputStream.readInt());
		method.setSourceHeater(dataInputStream.readDouble());
		method.setStopMode(readString(dataInputStream));
		method.setStopTime(dataInputStream.readInt());
		method.setTimeFilterPeakWidth(dataInputStream.readInt());
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private IChromatogramOverview readOverviewFromZipFile(ZipFile zipFile, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, directoryPrefix + IFormat.FILE_TIC_MSD);
		//
		IVendorChromatogram chromatogram = new VendorChromatogram();
		readScansOverview(dataInputStream, chromatogram);
		//
		dataInputStream.close();
		//
		return chromatogram;
	}

	private void readScansOverview(DataInputStream dataInputStream, IChromatogramMSD chromatogram) throws IOException {

		IVendorScan massSpectrum;
		IVendorIon ion;
		/*
		 * Retention Times - Total Signals
		 */
		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; scan++) {
			massSpectrum = new VendorScan();
			int retentionTime = dataInputStream.readInt(); // Retention Time
			float abundance = dataInputStream.readFloat(); // Total Signal
			try {
				ion = new VendorIon(AbstractIon.TIC_ION, abundance);
				massSpectrum.setRetentionTime(retentionTime);
				massSpectrum.addIon(ion);
				chromatogram.addScan(massSpectrum);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
	}

	private void setAdditionalInformation(File file, IChromatogramMSD chromatogram) {

		chromatogram.setConverterId(IFormat.CONVERTER_ID_CHROMATOGRAM);
		chromatogram.setFile(file);
		// Delay
		int startRetentionTime = chromatogram.getStartRetentionTime();
		int scanDelay = startRetentionTime;
		chromatogram.setScanDelay(scanDelay);
		// Interval
		int endRetentionTime = chromatogram.getStopRetentionTime();
		int scanInterval = endRetentionTime / chromatogram.getNumberOfScans();
		chromatogram.setScanInterval(scanInterval);
	}

	private void readScanProxies(ZipFile zipFile, String directoryPrefix, File file, IChromatogramMSD chromatogram) throws IOException {

		IIonTransitionSettings ionTransitionSettings = chromatogram.getIonTransitionSettings();
		//
		DataInputStream dataInputStream = getDataInputStream(zipFile, directoryPrefix + IFormat.FILE_SCANPROXIES_MSD);
		int scans = dataInputStream.readInt();
		//
		for(int scan = 1; scan <= scans; scan++) {
			//
			int offset = dataInputStream.readInt(); // Offset
			int retentionTime = dataInputStream.readInt(); // Retention Time
			int numberOfIons = dataInputStream.readInt(); // Number of Ions
			float totalSignal = dataInputStream.readFloat(); // Total Signal
			float retentionIndex = dataInputStream.readFloat(); // Retention Index
			int timeSegmentId = dataInputStream.readInt(); // Time Segment Id
			int cycleNumber = dataInputStream.readInt(); // Cycle Number
			//
			IVendorScanProxy massSpectrum = new VendorScanProxy(file, offset, IFormat.CHROMATOGRAM_VERSION_1301, ionTransitionSettings);
			massSpectrum.setRetentionTime(retentionTime);
			massSpectrum.setNumberOfIons(numberOfIons);
			massSpectrum.setTotalSignal(totalSignal);
			massSpectrum.setRetentionIndex(retentionIndex);
			massSpectrum.setTimeSegmentId(timeSegmentId);
			massSpectrum.setCycleNumber(cycleNumber);
			chromatogram.addScan(massSpectrum);
		}
		//
		dataInputStream.close();
	}

	private void readScans(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		IIonTransitionSettings ionTransitionSettings = chromatogram.getIonTransitionSettings();
		/*
		 * Scans
		 */
		IReaderProxy readerProxy = new ReaderProxy_1301();
		int scans = dataInputStream.readInt();
		//
		for(int scan = 1; scan <= scans; scan++) {
			IVendorScan massSpectrum = new VendorScan();
			readerProxy.readMassSpectrum(massSpectrum, dataInputStream, ionTransitionSettings);
			chromatogram.addScan(massSpectrum);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readBaseline(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		/*
		 * Get the Baseline
		 */
		int scans = dataInputStream.readInt(); // Number of Scans
		List<IBaselineElement> baselineElements = new ArrayList<IBaselineElement>();
		for(int scan = 1; scan <= scans; scan++) {
			int retentionTime = dataInputStream.readInt(); // Retention Time
			float backgroundAbundance = dataInputStream.readFloat(); // Background Abundance
			IBaselineElement baselineElement = new BaselineElement(retentionTime, backgroundAbundance);
			baselineElements.add(baselineElement);
		}
		/*
		 * Set the Baseline
		 */
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		for(int index = 0; index < (scans - 1); index++) {
			/*
			 * Retention times and background abundances.
			 * Set the baseline.
			 */
			IBaselineElement baselineElement = baselineElements.get(index);
			IBaselineElement baselineElementNext = baselineElements.get(index + 1);
			int startRetentionTime = baselineElement.getRetentionTime();
			float startBackgroundAbundance = baselineElement.getBackgroundAbundance();
			int stopRetentionTime = baselineElementNext.getRetentionTime();
			float stopBackgroundAbundance = baselineElementNext.getBackgroundAbundance();
			/*
			 * Add the baseline in validate == false modus, cause we know, that the baseline
			 * has been saved without overlapping segments.
			 */
			baselineModel.addBaseline(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance, false);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readPeaks(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		int numberOfPeaks = dataInputStream.readInt(); // Number of Peaks
		for(int i = 1; i <= numberOfPeaks; i++) {
			try {
				IChromatogramPeakMSD peak = readPeak(dataInputStream, chromatogram);
				chromatogram.addPeak(peak);
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private IChromatogramPeakMSD readPeak(DataInputStream dataInputStream, IChromatogramMSD chromatogram) throws IOException, IllegalArgumentException, PeakException {

		IIonTransitionSettings ionTransitionSettings = chromatogram.getIonTransitionSettings();
		//
		String detectorDescription = readString(dataInputStream); // Detector Description
		String quantifierDescription = readString(dataInputStream);
		boolean activeForAnalysis = dataInputStream.readBoolean();
		String integratorDescription = readString(dataInputStream); // Integrator Description
		String modelDescription = readString(dataInputStream); // Model Description
		PeakType peakType = PeakType.valueOf(readString(dataInputStream)); // Peak Type
		int suggestedNumberOfComponents = dataInputStream.readInt(); // Suggest Number Of Components
		readString(dataInputStream); // Keep this for backward compatibility 2020/09/11
		List<String> classifiers = IFileHelper.readStringCollection(dataInputStream);
		//
		float startBackgroundAbundance = dataInputStream.readFloat(); // Start Background Abundance
		float stopBackgroundAbundance = dataInputStream.readFloat(); // Stop Background Abundance
		//
		IPeakMassSpectrum peakMaximum = readPeakMassSpectrum(dataInputStream, ionTransitionSettings);
		//
		int numberOfRetentionTimes = dataInputStream.readInt(); // Number Retention Times
		IPeakIntensityValues intensityValues = new PeakIntensityValues(Float.MAX_VALUE);
		for(int i = 1; i <= numberOfRetentionTimes; i++) {
			int retentionTime = dataInputStream.readInt(); // Retention Time
			float relativeIntensity = dataInputStream.readFloat(); // Intensity
			intensityValues.addIntensityValue(retentionTime, relativeIntensity);
		}
		intensityValues.normalize();
		//
		IPeakModelMSD peakModel = new PeakModelMSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		ChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		peak.setDetectorDescription(detectorDescription);
		peak.setQuantifierDescription(quantifierDescription);
		peak.setActiveForAnalysis(activeForAnalysis);
		peak.setIntegratorDescription(integratorDescription);
		peak.setModelDescription(modelDescription);
		peak.setPeakType(peakType);
		peak.setSuggestedNumberOfComponents(suggestedNumberOfComponents);
		//
		for(String c : classifiers) {
			peak.addClassifier(c);
		}
		//
		List<IIntegrationEntry> integrationEntries = readIntegrationEntries(dataInputStream);
		peak.setIntegratedArea(integrationEntries, integratorDescription);
		/*
		 * Identification Results
		 */
		readPeakIdentificationTargets(dataInputStream, peak);
		/*
		 * Quantitation Results
		 */
		readPeakQuantitationEntries(dataInputStream, peak);
		/*
		 * Optimized Mass Spectrum
		 */
		boolean readOptimizedMassSpectrum = dataInputStream.readBoolean();
		if(readOptimizedMassSpectrum) {
			IScanMSD optimizedMassSpectrum = new ScanMSD();
			readNormalMassSpectrum(optimizedMassSpectrum, dataInputStream, ionTransitionSettings);
			peakMaximum.setOptimizedMassSpectrum(optimizedMassSpectrum);
		}
		//
		List<IInternalStandard> internalStandards = readInternalStandards(dataInputStream);
		peak.addInternalStandards(internalStandards);
		//
		return peak;
	}

	private List<IInternalStandard> readInternalStandards(DataInputStream dataInputStream) throws IOException {

		List<IInternalStandard> internalStandards = new ArrayList<IInternalStandard>();
		int numberOfInternalStandards = dataInputStream.readInt();
		for(int i = 1; i <= numberOfInternalStandards; i++) {
			String name = readString(dataInputStream);
			double concentration = dataInputStream.readDouble();
			String concentrationUnit = readString(dataInputStream);
			double responseFactor = dataInputStream.readDouble();
			String chemicalClass = readString(dataInputStream);
			IInternalStandard internalStandard = new InternalStandard(name, concentration, concentrationUnit, responseFactor);
			internalStandard.setChemicalClass(chemicalClass);
			internalStandards.add(internalStandard);
		}
		return internalStandards;
	}

	private List<IIntegrationEntry> readIntegrationEntries(DataInputStream dataInputStream) throws IOException {

		List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
		int numberOfIntegrationEntries = dataInputStream.readInt(); // Number Integration Entries
		for(int i = 1; i <= numberOfIntegrationEntries; i++) {
			double ion = dataInputStream.readDouble(); // m/z
			double integratedArea = dataInputStream.readDouble(); // Integrated Area
			IIntegrationEntry integrationEntry = new IntegrationEntry(ion, integratedArea);
			integrationEntries.add(integrationEntry);
		}
		return integrationEntries;
	}

	private void readPeakIdentificationTargets(DataInputStream dataInputStream, IPeakMSD peak) throws IOException {

		int numberOfPeakTargets = dataInputStream.readInt(); // Number Peak Targets
		for(int i = 1; i <= numberOfPeakTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			boolean manuallyVerified = dataInputStream.readBoolean();
			//
			int retentionTime = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat();
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String referenceIdentifier = readString(dataInputStream);
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String database = readString(dataInputStream);
			String contributor = readString(dataInputStream);
			String name = readString(dataInputStream); // Name
			Set<String> synonyms = new HashSet<String>(); // Synonyms
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream); // Formula
			String smiles = readString(dataInputStream); // SMILES
			String inChI = readString(dataInputStream); // InChI
			double molWeight = dataInputStream.readDouble(); // Mol Weight
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float matchFactorDirect = dataInputStream.readFloat(); // Match Factor Direct
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float reverseMatchFactorDirect = dataInputStream.readFloat(); // Reverse Match Factor Direct
			float probability = dataInputStream.readFloat(); // Probability
			boolean isMatch = dataInputStream.readBoolean();
			//
			IPeakLibraryInformation libraryInformation = new PeakLibraryInformation();
			libraryInformation.setRetentionTime(retentionTime);
			libraryInformation.setRetentionIndex(retentionIndex);
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setReferenceIdentifier(referenceIdentifier);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setDatabase(database);
			libraryInformation.setContributor(contributor);
			libraryInformation.setName(name);
			libraryInformation.setSynonyms(synonyms);
			libraryInformation.setFormula(formula);
			libraryInformation.setSmiles(smiles);
			libraryInformation.setInChI(inChI);
			libraryInformation.setMolWeight(molWeight);
			//
			IComparisonResult comparisonResult = new PeakComparisonResult(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
			comparisonResult.setMatch(isMatch);
			//
			try {
				IIdentificationTarget identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				identificationEntry.setManuallyVerified(manuallyVerified);
				peak.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	@Override
	public String readString(DataInputStream dataInputStream) throws IOException {

		return IFileHelper.readString(dataInputStream);
	}

	private void readMassSpectrumIdentificationTargets(DataInputStream dataInputStream, IScanMSD massSpectrum) throws IOException {

		int numberOfMassSpectrumTargets = dataInputStream.readInt(); // Number Mass Spectrum Targets
		for(int i = 1; i <= numberOfMassSpectrumTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			boolean manuallyVerified = dataInputStream.readBoolean();
			//
			int retentionTime = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat();
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String referenceIdentifier = readString(dataInputStream);
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String database = readString(dataInputStream);
			String contributor = readString(dataInputStream);
			String name = readString(dataInputStream); // Name
			Set<String> synonyms = new HashSet<String>(); // Synonyms
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream); // Formula
			String smiles = readString(dataInputStream); // SMILES
			String inChI = readString(dataInputStream); // InChI
			double molWeight = dataInputStream.readDouble(); // Mol Weight
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float matchFactorDirect = dataInputStream.readFloat(); // Match Factor Direct
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float reverseMatchFactorDirect = dataInputStream.readFloat(); // Reverse Match Factor Direct
			float probability = dataInputStream.readFloat(); // Probability
			boolean isMatch = dataInputStream.readBoolean();
			//
			ILibraryInformation libraryInformation = new LibraryInformation();
			libraryInformation.setRetentionTime(retentionTime);
			libraryInformation.setRetentionIndex(retentionIndex);
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setReferenceIdentifier(referenceIdentifier);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setDatabase(database);
			libraryInformation.setContributor(contributor);
			libraryInformation.setName(name);
			libraryInformation.setSynonyms(synonyms);
			libraryInformation.setFormula(formula);
			libraryInformation.setSmiles(smiles);
			libraryInformation.setInChI(inChI);
			libraryInformation.setMolWeight(molWeight);
			//
			IComparisonResult comparisonResult = new ComparisonResult(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
			comparisonResult.setMatch(isMatch);
			//
			try {
				IIdentificationTarget identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				identificationEntry.setManuallyVerified(manuallyVerified);
				massSpectrum.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private void readPeakQuantitationEntries(DataInputStream dataInputStream, IPeakMSD peak) throws IOException {

		int numberOfQuantitationEntries = dataInputStream.readInt(); // Number Quantitation Entries
		for(int i = 1; i <= numberOfQuantitationEntries; i++) {
			//
			String name = readString(dataInputStream); // Name
			String chemicalClass = readString(dataInputStream); // Chemical Class
			double concentration = dataInputStream.readDouble(); // Concentration
			String concentrationUnit = readString(dataInputStream); // Concentration Unit
			double area = dataInputStream.readDouble(); // Area
			String calibrationMethod = readString(dataInputStream); // Calibration Method
			boolean usedCrossZero = dataInputStream.readBoolean(); // Used Cross Zero
			String description = readString(dataInputStream); // Description
			/*
			 * Legacy support
			 */
			double signal = ISignal.TOTAL_INTENSITY;
			boolean isSignal = dataInputStream.readBoolean();
			if(isSignal) {
				signal = dataInputStream.readDouble();
			}
			//
			IQuantitationEntry quantitationEntry = new QuantitationEntry(name, concentration, concentrationUnit, area);
			quantitationEntry.setSignal(signal);
			quantitationEntry.setChemicalClass(chemicalClass);
			quantitationEntry.setCalibrationMethod(calibrationMethod);
			quantitationEntry.setUsedCrossZero(usedCrossZero);
			quantitationEntry.setDescription(description);
			//
			peak.addQuantitationEntry(quantitationEntry);
		}
	}

	private void readArea(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		String integratorDescription = readString(dataInputStream); // Chromatogram Integrator Description
		List<IIntegrationEntry> chromatogramIntegrationEntries = readIntegrationEntries(dataInputStream);
		readString(dataInputStream); // Background Integrator Description
		List<IIntegrationEntry> backgroundIntegrationEntries = readIntegrationEntries(dataInputStream);
		//
		chromatogram.setIntegratedArea(chromatogramIntegrationEntries, backgroundIntegrationEntries, integratorDescription);
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readIdentification(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		int numberOfTargets = dataInputStream.readInt(); // Number of Targets
		for(int i = 1; i <= numberOfTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			boolean manuallyVerified = dataInputStream.readBoolean();
			//
			int retentionTime = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat();
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String referenceIdentifier = readString(dataInputStream);
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String database = readString(dataInputStream);
			String contributor = readString(dataInputStream);
			String name = readString(dataInputStream); // Name
			Set<String> synonyms = new HashSet<String>(); // Synonyms
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream); // Formula
			String smiles = readString(dataInputStream); // SMILES
			String inChI = readString(dataInputStream); // InChI
			double molWeight = dataInputStream.readDouble(); // Mol Weight
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float matchFactorDirect = dataInputStream.readFloat(); // Match Factor Direct
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float reverseMatchFactorDirect = dataInputStream.readFloat(); // Reverse Match Factor Direct
			float probability = dataInputStream.readFloat(); // Probability
			boolean isMatch = dataInputStream.readBoolean();
			//
			IChromatogramLibraryInformation libraryInformation = new ChromatogramLibraryInformation();
			libraryInformation.setRetentionTime(retentionTime);
			libraryInformation.setRetentionIndex(retentionIndex);
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setReferenceIdentifier(referenceIdentifier);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setDatabase(database);
			libraryInformation.setContributor(contributor);
			libraryInformation.setName(name);
			libraryInformation.setSynonyms(synonyms);
			libraryInformation.setFormula(formula);
			libraryInformation.setSmiles(smiles);
			libraryInformation.setInChI(inChI);
			libraryInformation.setMolWeight(molWeight);
			//
			IComparisonResult comparisonResult = new ChromatogramComparisonResult(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
			comparisonResult.setMatch(isMatch);
			//
			try {
				IIdentificationTarget identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				identificationEntry.setManuallyVerified(manuallyVerified);
				chromatogram.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readHistory(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		IEditHistory editHistory = chromatogram.getEditHistory();
		int numberOfEntries = dataInputStream.readInt(); // Number of entries
		for(int i = 1; i <= numberOfEntries; i++) {
			long time = dataInputStream.readLong(); // Date
			String description = readString(dataInputStream); // Description
			//
			Date date = new Date(time);
			IEditInformation editInformation = new EditInformation(date, description);
			editHistory.add(editInformation);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readMiscellaneous(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		int numberOfEntries = dataInputStream.readInt();
		for(int i = 0; i < numberOfEntries; i++) {
			String key = readString(dataInputStream);
			String value = readString(dataInputStream);
			chromatogram.putHeaderData(key, value);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private IPeakMassSpectrum readPeakMassSpectrum(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException {

		short massSpectrometer = dataInputStream.readShort(); // Mass Spectrometer
		short massSpectrumType = dataInputStream.readShort(); // Mass Spectrum Type
		double precursorIon = dataInputStream.readDouble(); // Precursor Ion (0 if MS1 or none has been selected)
		//
		IPeakMassSpectrum massSpectrum = new PeakMassSpectrum();
		massSpectrum.setMassSpectrometer(massSpectrometer);
		massSpectrum.setMassSpectrumType(massSpectrumType);
		massSpectrum.setPrecursorIon(precursorIon);
		//
		readNormalMassSpectrum(massSpectrum, dataInputStream, ionTransitionSettings);
		//
		return massSpectrum;
	}

	private void readNormalMassSpectrum(IScanMSD massSpectrum, DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException {

		int retentionTime = dataInputStream.readInt(); // Retention Time
		int relativeRetentionTime = dataInputStream.readInt();
		int retentionTimeColumn1 = dataInputStream.readInt();
		int retentionTimeColumn2 = dataInputStream.readInt();
		float retentionIndex = dataInputStream.readFloat(); // Retention Index
		if(dataInputStream.readBoolean()) {
			int size = dataInputStream.readInt();
			for(int i = 0; i < size; i++) {
				RetentionIndexType retentionIndexType = RetentionIndexType.valueOf(readString(dataInputStream));
				float retentionIndexAdditional = dataInputStream.readFloat();
				massSpectrum.setRetentionIndex(retentionIndexType, retentionIndexAdditional);
			}
		}
		int timeSegmentId = dataInputStream.readInt(); // Time Segment Id
		int cycleNumber = dataInputStream.readInt(); // Cycle Number
		massSpectrum.setRetentionTime(retentionTime);
		massSpectrum.setRelativeRetentionTime(relativeRetentionTime);
		massSpectrum.setRetentionTimeColumn1(retentionTimeColumn1);
		massSpectrum.setRetentionTimeColumn2(retentionTimeColumn2);
		massSpectrum.setRetentionIndex(retentionIndex);
		massSpectrum.setTimeSegmentId(timeSegmentId);
		massSpectrum.setCycleNumber(cycleNumber);
		//
		int numberOfIons = dataInputStream.readInt(); // Number of ions
		for(int i = 1; i <= numberOfIons; i++) {
			/*
			 * Read Ions
			 */
			try {
				IVendorIon ion = readIon(dataInputStream, ionTransitionSettings);
				massSpectrum.addIon(ion);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			} catch(IonTransitionIsNullException e) {
				logger.warn(e);
			}
		}
		/*
		 * Identification Results
		 */
		readMassSpectrumIdentificationTargets(dataInputStream, massSpectrum);
	}

	private IVendorIon readIon(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException, AbundanceLimitExceededException, IonLimitExceededException, IonTransitionIsNullException {

		IVendorIon ion;
		//
		double mz = dataInputStream.readDouble(); // m/z
		float abundance = dataInputStream.readFloat(); // Abundance
		/*
		 * Ion Transition
		 */
		int transition = dataInputStream.readInt();
		if(transition == 0) {
			ion = new VendorIon(mz, abundance);
		} else {
			/*
			 * parent m/z start, ...
			 */
			String compoundName = readString(dataInputStream); // compound name
			double filter1FirstIon = dataInputStream.readDouble(); // parent m/z start
			double filter1LastIon = dataInputStream.readDouble(); // parent m/z stop
			double filter3FirstIon = dataInputStream.readDouble(); // daughter m/z start
			double filter3LastIon = dataInputStream.readDouble(); // daughter m/z stop
			double collisionEnergy = dataInputStream.readDouble(); // collision energy
			double filter1Resolution = dataInputStream.readDouble(); // q1 resolution
			double filter3Resolution = dataInputStream.readDouble(); // q3 resolution
			int transitionGroup = dataInputStream.readInt(); // transition group
			int dwell = dataInputStream.readInt(); // dwell
			//
			IIonTransition ionTransition = ionTransitionSettings.getIonTransition(compoundName, filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
			ionTransition.setDwell(dwell);
			ion = new VendorIon(mz, abundance, ionTransition);
		}
		return ion;
	}

	private void readSeparationColumn(DataInputStream dataInputStream, boolean closeStream, IChromatogramMSD chromatogram) throws IOException {

		int numberOfEntries = dataInputStream.readInt();
		ISeparationColumnIndices separationColumnIndices = chromatogram.getSeparationColumnIndices();
		for(int i = 0; i < numberOfEntries; i++) {
			String name = readString(dataInputStream);
			int retentionTime = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat();
			IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
			separationColumnIndices.put(retentionIndexEntry);
		}
		//
		String name = readString(dataInputStream);
		String length = readString(dataInputStream);
		String diameter = readString(dataInputStream);
		String phase = readString(dataInputStream);
		ISeparationColumn separationColumn = separationColumnIndices.getSeparationColumn();
		separationColumn.copyFrom(SeparationColumnFactory.getSeparationColumn(name, length, diameter, phase));
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private int readChromatogramReferenceInfo(DataInputStream dataInputStream, boolean closeStream) throws IOException {

		int size = dataInputStream.readInt();
		if(closeStream) {
			dataInputStream.close();
		}
		return size;
	}

	private void readReferencedChromatograms(Object object, String directoryPrefix, IChromatogramMSD chromatogram, int size, boolean closeStream, IProgressMonitor monitor) throws IOException {

		for(int i = 0; i < size; i++) {
			//
			String directory = directoryPrefix + IFormat.DIR_CHROMATOGRAM_REFERENCE + IFormat.CHROMATOGRAM_REFERENCE_SEPARATOR + i + IFormat.DIR_SEPARATOR;
			DataInputStream dataInputStream = getDataInputStream(object, directory + IFormat.FILE_CHROMATOGRAM_TYPE);
			String dataType = readString(dataInputStream);
			//
			if(closeStream) {
				dataInputStream.close();
			}
			//
			parseChromatogram(object, dataType, directory, chromatogram, closeStream, monitor);
		}
	}

	private void parseChromatogram(Object object, String dataType, String directoryPrefix, IChromatogramMSD chromatogram, boolean closeStream, IProgressMonitor monitor) throws IOException {

		String directory = directoryPrefix + IFormat.DIR_CHROMATOGRAM_REFERENCE + IFormat.DIR_SEPARATOR;
		if(object instanceof ZipFile) {
			/*
			 * Chromatogram
			 */
			ZipFile zipFile = (ZipFile)object;
			if(dataType.equals(IFormat.DATA_TYPE_MSD)) {
				ChromatogramReaderMSD chromatogramReaderMSD = new ChromatogramReaderMSD();
				IChromatogramMSD chromatogramMSD = chromatogramReaderMSD.read(zipFile, directory, monitor);
				chromatogram.addReferencedChromatogram(chromatogramMSD);
			} else if(dataType.equals(IFormat.DATA_TYPE_CSD)) {
				ChromatogramReaderCSD chromatogramReaderCSD = new ChromatogramReaderCSD();
				IChromatogramCSD chromatogramCSD = chromatogramReaderCSD.read(zipFile, directory, monitor);
				chromatogram.addReferencedChromatogram(chromatogramCSD);
			} else if(dataType.equals(IFormat.DATA_TYPE_WSD)) {
				ChromatogramReaderWSD chromatogramReaderWSD = new ChromatogramReaderWSD();
				IChromatogramWSD chromatogramWSD = chromatogramReaderWSD.read(zipFile, directory, monitor);
				chromatogram.addReferencedChromatogram(chromatogramWSD);
			}
		} else {
			/*
			 * Reading from a stream currently makes problems.
			 */
			// ZipInputStream zipInputStream = new ZipInputStream(getDataInputStream(object, directory, true));
			//
			// if(closeStream) {
			// zipInputStream.close();
			// }
		}
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream;
		//
		dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.CHROMATOGRAM_VERSION_1301)) {
			isValid = true;
		}
		//
		dataInputStream.close();
		//
		return isValid;
	}
}
