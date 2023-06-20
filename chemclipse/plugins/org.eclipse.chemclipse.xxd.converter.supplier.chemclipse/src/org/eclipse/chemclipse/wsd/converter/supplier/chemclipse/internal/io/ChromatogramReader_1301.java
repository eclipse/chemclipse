/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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
package org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.internal.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IMethod;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.core.PeakType;
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
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io.ChromatogramReaderWSD;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io.IChromatogramWSDZipReader;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.IVendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorScanSignal;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakModelWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.PeakModelWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.BaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IBaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramReader_1301 extends AbstractChromatogramReader implements IChromatogramWSDZipReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader_1301.class);

	@Override
	public IChromatogramWSD read(File file, IProgressMonitor monitor) throws IOException {

		IChromatogramWSD chromatogram = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				chromatogram = readFromZipFile(zipFile, "", file, monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return chromatogram;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws IOException {

		IChromatogramOverview chromatogramOverview = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				chromatogramOverview = readOverviewFromZipFile(zipFile, "");
			}
		} finally {
			zipFile.close();
		}
		//
		return chromatogramOverview;
	}

	@Override
	public IChromatogramWSD read(ZipInputStream zipInputStream, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		return readZipData(zipInputStream, directoryPrefix, null, monitor);
	}

	@Override
	public IChromatogramWSD read(ZipFile zipFile, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		return readFromZipFile(zipFile, directoryPrefix, null, monitor);
	}

	private IChromatogramWSD readFromZipFile(ZipFile zipFile, String directoryPrefix, File file, IProgressMonitor monitor) throws IOException {

		return readZipData(zipFile, directoryPrefix, file, monitor);
	}

	private IChromatogramOverview readOverviewFromZipFile(ZipFile zipFile, String directoryPrefix) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, directoryPrefix + IFormat.FILE_TIC_WSD);
		//
		IVendorChromatogram chromatogram = new VendorChromatogram();
		readScansOverview(dataInputStream, chromatogram);
		//
		dataInputStream.close();
		//
		return chromatogram;
	}

	/*
	 * Object = ZipFile or ZipInputStream
	 * @param object
	 * @param file
	 * @return
	 */
	private IChromatogramWSD readZipData(Object object, String directoryPrefix, File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = null;
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Read Chromatogram", 100);
		try {
			boolean closeStream;
			if(object instanceof ZipFile) {
				/*
				 * ZipFile
				 */
				closeStream = true;
			} else if(object instanceof ZipInputStream) {
				/*
				 * ZipInputStream
				 */
				closeStream = false;
			} else {
				return null;
			}
			/*
			 * Read the chromatographic information.
			 */
			chromatogram = new VendorChromatogram();
			readMethod(getDataInputStream(object, directoryPrefix + IFormat.FILE_SYSTEM_SETTINGS_WSD), closeStream, chromatogram);
			readScans(getDataInputStream(object, directoryPrefix + IFormat.FILE_SCANS_WSD), closeStream, chromatogram);
			readBaseline(getDataInputStream(object, directoryPrefix + IFormat.FILE_BASELINE_WSD), closeStream, chromatogram);
			subMonitor.worked(20);
			readPeaks(getDataInputStream(object, directoryPrefix + IFormat.FILE_PEAKS_WSD), closeStream, chromatogram);
			readArea(getDataInputStream(object, directoryPrefix + IFormat.FILE_AREA_WSD), closeStream, chromatogram);
			subMonitor.worked(20);
			readIdentification(getDataInputStream(object, directoryPrefix + IFormat.FILE_IDENTIFICATION_WSD), closeStream, chromatogram);
			readHistory(getDataInputStream(object, directoryPrefix + IFormat.FILE_HISTORY_WSD), closeStream, chromatogram);
			subMonitor.worked(20);
			readMiscellaneous(getDataInputStream(object, directoryPrefix + IFormat.FILE_MISC_WSD), chromatogram);
			readSeparationColumn(getDataInputStream(object, directoryPrefix + IFormat.FILE_SEPARATION_COLUMN_WSD), closeStream, chromatogram);
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

	private void readScansOverview(DataInputStream dataInputStream, IChromatogramWSD chromatogram) throws IOException {

		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; ++scan) {
			IScanWSD scanObject = new VendorScan();
			float totalSignal = dataInputStream.readFloat();
			int retentionTime = dataInputStream.readInt();
			scanObject.setRetentionTime(retentionTime);
			//
			IScanSignalWSD scanSignalObject = new VendorScanSignal();
			scanSignalObject.setWavelength(IScanSignalWSD.TOTAL_INTENSITY);
			scanSignalObject.setAbundance(totalSignal);
			scanObject.addScanSignal(scanSignalObject);
			chromatogram.addScan(scanObject);
		}
	}

	private void readMethod(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram) throws IOException {

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

	private void readScans(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram) throws IOException {

		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; ++scan) {
			IScanWSD scanWSD = new VendorScan();
			int scanSignals = dataInputStream.readInt();
			//
			for(int scanSignal = 0; scanSignal < scanSignals; ++scanSignal) {
				IScanSignalWSD scanSignalObject = new VendorScanSignal();
				float wavelength = (float)dataInputStream.readDouble();
				float abundance = dataInputStream.readFloat();
				//
				scanSignalObject.setWavelength(wavelength);
				scanSignalObject.setAbundance(abundance);
				//
				scanWSD.addScanSignal(scanSignalObject);
			}
			//
			int retentionTime = dataInputStream.readInt();
			int relativeRetentionTime = dataInputStream.readInt();
			int retentionTimeColumn1 = dataInputStream.readInt();
			int retentionTimeColumn2 = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat(); // Retention Index
			if(dataInputStream.readBoolean()) {
				int size = dataInputStream.readInt();
				for(int i = 0; i < size; i++) {
					SeparationColumnType separationColumnType = SeparationColumnFactory.getSeparationColumnType(readString(dataInputStream));
					float retentionIndexAdditional = dataInputStream.readFloat();
					scanWSD.setRetentionIndex(separationColumnType, retentionIndexAdditional);
				}
			}
			float totalSignal = dataInputStream.readFloat();
			int timeSegmentId = dataInputStream.readInt();
			int cycleNumber = dataInputStream.readInt();
			//
			scanWSD.setRetentionTime(retentionTime);
			scanWSD.setRelativeRetentionTime(relativeRetentionTime);
			scanWSD.setRetentionTimeColumn1(retentionTimeColumn1);
			scanWSD.setRetentionTimeColumn2(retentionTimeColumn2);
			scanWSD.setRetentionIndex(retentionIndex);
			scanWSD.setTimeSegmentId(timeSegmentId);
			scanWSD.setCycleNumber(cycleNumber);
			scanWSD.adjustTotalSignal(totalSignal);
			/*
			 * Identification Results
			 */
			readScanIdentificationTargets(dataInputStream, scanWSD);
			//
			chromatogram.addScan(scanWSD);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readBaseline(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram) throws IOException {

		int scans = dataInputStream.readInt();
		List<IBaselineElement> baselineElements = new ArrayList<>();
		for(int scan = 1; scan <= scans; ++scan) {
			// monitor.subTask(IConstants.IMPORT_SCAN + scan);
			int retentionTime = dataInputStream.readInt();
			float backgroundAbundance = dataInputStream.readFloat();
			IBaselineElement baselineElement = new BaselineElement(retentionTime, backgroundAbundance);
			baselineElements.add(baselineElement);
		}
		//
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		for(int index = 0; index < (scans - 1); ++index) {
			/*
			 * Retention times and background abundances.
			 * Set the baseline.
			 */
			// monitor.subTask(IConstants.IMPORT_BASELINE + index);
			IBaselineElement baselineElement = baselineElements.get(index);
			IBaselineElement baselineElementNext = baselineElements.get(index + 1);
			int startRetentionTime = baselineElement.getRetentionTime();
			float startBackgroundAbundance = baselineElement.getBackgroundAbundance();
			int stopRetentionTime = baselineElementNext.getRetentionTime();
			float stopBackgroundAbundance = baselineElementNext.getBackgroundAbundance();
			/*
			 * Found all baseline & next baseline elements.
			 */
			baselineModel.addBaseline(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance, false);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readPeaks(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram) throws IOException {

		int numberOfPeaks = dataInputStream.readInt(); // Number of Peaks
		for(int i = 1; i <= numberOfPeaks; i++) {
			try {
				IChromatogramPeakWSD peak = readPeak(dataInputStream, chromatogram);
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

	private IChromatogramPeakWSD readPeak(DataInputStream dataInputStream, IChromatogramWSD chromatogram) throws IOException, IllegalArgumentException, PeakException {

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
		int retentionTime = dataInputStream.readInt();
		int relativeRetentionTime = dataInputStream.readInt();
		float totalSignalScan = dataInputStream.readFloat();
		IVendorScan peakMaximum = new VendorScan();
		peakMaximum.setRetentionTime(retentionTime);
		peakMaximum.adjustTotalSignal(totalSignalScan);
		peakMaximum.setRelativeRetentionTime(relativeRetentionTime);
		int retentionTimeColumn1 = dataInputStream.readInt();
		int retentionTimeColumn2 = dataInputStream.readInt();
		float retentionIndexScan = dataInputStream.readFloat(); // Retention Index
		if(dataInputStream.readBoolean()) {
			int size = dataInputStream.readInt();
			for(int i = 0; i < size; i++) {
				SeparationColumnType separationColumnType = SeparationColumnFactory.getSeparationColumnType(readString(dataInputStream));
				float retentionIndexAdditional = dataInputStream.readFloat();
				peakMaximum.setRetentionIndex(separationColumnType, retentionIndexAdditional);
			}
		}
		//
		int timeSegmentId = dataInputStream.readInt();
		int cycleNumber = dataInputStream.readInt();
		//
		peakMaximum.setRetentionIndex(retentionIndexScan);
		peakMaximum.setRetentionTimeColumn1(retentionTimeColumn1);
		peakMaximum.setRetentionTimeColumn2(retentionTimeColumn2);
		peakMaximum.setTimeSegmentId(timeSegmentId);
		peakMaximum.setCycleNumber(cycleNumber);
		//
		int numberOfRetentionTimes = dataInputStream.readInt(); // Number Retention Times
		IPeakIntensityValues intensityValues = new PeakIntensityValues(Float.MAX_VALUE);
		for(int i = 1; i <= numberOfRetentionTimes; i++) {
			int retentionTimePeak = dataInputStream.readInt(); // Retention Time
			float relativeIntensity = dataInputStream.readFloat(); // Intensity
			intensityValues.addIntensityValue(retentionTimePeak, relativeIntensity);
		}
		intensityValues.normalize();
		//
		IPeakModelWSD peakModel = new PeakModelWSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		ChromatogramPeakWSD peak = new ChromatogramPeakWSD(peakModel, chromatogram);
		peak.setDetectorDescription(detectorDescription);
		peak.setQuantifierDescription(quantifierDescription);
		peak.setActiveForAnalysis(activeForAnalysis);
		peak.setIntegratorDescription(integratorDescription);
		peak.setModelDescription(modelDescription);
		peak.setPeakType(peakType);
		peak.setSuggestedNumberOfComponents(suggestedNumberOfComponents);
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
		//
		List<IInternalStandard> internalStandards = readInternalStandards(dataInputStream);
		peak.addInternalStandards(internalStandards);
		//
		return peak;
	}

	@Override
	public String readString(DataInputStream dataInputStream) throws IOException {

		return IFileHelper.readString(dataInputStream);
	}

	private void readArea(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram) throws IOException {

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

	private void readPeakIdentificationTargets(DataInputStream dataInputStream, IPeakWSD peak) throws IOException {

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
			Set<String> synonyms = new HashSet<>(); // Synonyms
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
				identificationEntry.setVerified(manuallyVerified);
				peak.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private List<IInternalStandard> readInternalStandards(DataInputStream dataInputStream) throws IOException {

		List<IInternalStandard> internalStandards = new ArrayList<>();
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

	private void readPeakQuantitationEntries(DataInputStream dataInputStream, IPeakWSD peak) throws IOException {

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

	private List<IIntegrationEntry> readIntegrationEntries(DataInputStream dataInputStream) throws IOException {

		List<IIntegrationEntry> integrationEntries = new ArrayList<>();
		int numberOfIntegrationEntries = dataInputStream.readInt(); // Number Integration Entries
		for(int i = 1; i <= numberOfIntegrationEntries; i++) {
			double integratedArea = dataInputStream.readDouble(); // Integrated Area
			IIntegrationEntry integrationEntry = new IntegrationEntry(integratedArea);
			integrationEntries.add(integrationEntry);
		}
		return integrationEntries;
	}

	private void readIdentification(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram) throws IOException {

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
			Set<String> synonyms = new HashSet<>(); // Synonyms
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
				identificationEntry.setVerified(manuallyVerified);
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

	private void readHistory(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram) throws IOException {

		IEditHistory editHistory = chromatogram.getEditHistory();
		int numEntries = dataInputStream.readInt();
		for(int i = 1; i <= numEntries; ++i) {
			long time = dataInputStream.readLong();
			String description = readString(dataInputStream);
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

	private void readMiscellaneous(DataInputStream dataInputStream, IChromatogramWSD chromatogram) throws IOException {

		int numberOfEntries = dataInputStream.readInt();
		for(int i = 0; i < numberOfEntries; i++) {
			String key = readString(dataInputStream);
			String value = readString(dataInputStream);
			chromatogram.putHeaderData(key, value);
		}
		//
		dataInputStream.close();
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.CHROMATOGRAM_VERSION_1301)) {
			isValid = true;
		}
		//
		dataInputStream.close();
		//
		return isValid;
	}

	private void readSeparationColumn(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram) throws IOException {

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

	private void readReferencedChromatograms(Object object, String directoryPrefix, IChromatogramWSD chromatogram, int size, boolean closeStream, IProgressMonitor monitor) throws IOException {

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

	private void parseChromatogram(Object object, String dataType, String directoryPrefix, IChromatogramWSD chromatogram, boolean closeStream, IProgressMonitor monitor) throws IOException {

		String directory = directoryPrefix + IFormat.DIR_CHROMATOGRAM_REFERENCE + IFormat.DIR_SEPARATOR;
		if(object instanceof ZipFile zipFile) {
			/*
			 * Chromatogram
			 */
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

	private void setAdditionalInformation(File file, IChromatogramWSD chromatogram) {

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

	private void readScanIdentificationTargets(DataInputStream dataInputStream, IScanWSD scanWSD) throws IOException {

		int numberOfTargets = dataInputStream.readInt();
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
			Set<String> synonyms = new HashSet<>(); // Synonyms
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
				identificationEntry.setVerified(manuallyVerified);
				scanWSD.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}
}