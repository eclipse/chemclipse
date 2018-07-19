/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.io.ChromatogramReaderCSD;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.io.IChromatogramCSDZipReader;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.model.chromatogram.IVendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IIntegrationEntryCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.implementation.IntegrationEntryCSD;
import org.eclipse.chemclipse.csd.model.implementation.PeakModelCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IMethod;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.InternalStandard;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io.ChromatogramReaderMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io.ChromatogramReaderWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.BaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IBaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramReader_1300 extends AbstractChromatogramReader implements IChromatogramCSDZipReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader_1300.class);

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramCSD chromatogram = null;
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
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramOverview chromatogramOverview = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				chromatogramOverview = readFromZipFile(zipFile, "", file, monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return chromatogramOverview;
	}

	@Override
	public IChromatogramCSD read(ZipInputStream zipInputStream, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		return readZipData(zipInputStream, directoryPrefix, null, monitor);
	}

	@Override
	public IChromatogramCSD read(ZipFile zipFile, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		return readFromZipFile(zipFile, directoryPrefix, null, monitor);
	}

	private IChromatogramCSD readFromZipFile(ZipFile zipFile, String directoryPrefix, File file, IProgressMonitor monitor) throws IOException {

		return readZipData(zipFile, directoryPrefix, file, monitor);
	}

	/*
	 * Object = ZipFile or ZipInputStream
	 * @param object
	 * @param file
	 * @return
	 */
	private IChromatogramCSD readZipData(Object object, String directoryPrefix, File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = null;
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Read Chromatogram", 100);
		//
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
			subMonitor.worked(20);
			readMethod(getDataInputStream(object, directoryPrefix + IFormat.FILE_SYSTEM_SETTINGS_CSD), closeStream, chromatogram, monitor);
			subMonitor.worked(20);
			readScans(getDataInputStream(object, directoryPrefix + IFormat.FILE_SCANS_CSD), closeStream, chromatogram, monitor);
			readBaseline(getDataInputStream(object, directoryPrefix + IFormat.FILE_BASELINE_CSD), closeStream, chromatogram, monitor);
			subMonitor.worked(20);
			readPeaks(getDataInputStream(object, directoryPrefix + IFormat.FILE_PEAKS_CSD), closeStream, chromatogram, monitor);
			subMonitor.worked(20);
			readHistory(getDataInputStream(object, directoryPrefix + IFormat.FILE_HISTORY_CSD), closeStream, chromatogram, monitor);
			readMiscellaneous(getDataInputStream(object, directoryPrefix + IFormat.FILE_MISC_CSD), closeStream, chromatogram, monitor);
			readSeparationColumn(getDataInputStream(object, directoryPrefix + IFormat.FILE_SEPARATION_COLUMN_CSD), closeStream, chromatogram, monitor);
			subMonitor.worked(20);
			setAdditionalInformation(file, chromatogram, monitor);
			//
			try {
				/*
				 * Read the referenced chromatograms.
				 * Get the size could lead to an exception if no reference info is stored.
				 */
				int size = readChromatogramReferenceInfo(getDataInputStream(object, directoryPrefix + IFormat.FILE_REFERENCE_INFO), closeStream, monitor);
				readReferencedChromatograms(object, directoryPrefix, chromatogram, size, closeStream, monitor);
			} catch(IOException e) {
				logger.info(e);
			}
		} finally {
			SubMonitor.done(monitor);
		}
		//
		return chromatogram;
	}

	private void readMethod(DataInputStream dataInputStream, boolean closeStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

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

	private void readScans(DataInputStream dataInputStream, boolean closeStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

		/*
		 * Scans
		 */
		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; scan++) {
			// monitor.subTask(IConstants.IMPORT_SCAN + scan);
			//
			int retentionTime = dataInputStream.readInt();
			int relativeRetentionTime = dataInputStream.readInt();
			float totalSignal = dataInputStream.readFloat();
			IVendorScan scanFID = new VendorScan(retentionTime, totalSignal);
			scanFID.setRelativeRetentionTime(relativeRetentionTime);
			//
			int retentionTimeColumn1 = dataInputStream.readInt();
			int retentionTimeColumn2 = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat(); // Retention Index
			if(dataInputStream.readBoolean()) {
				int size = dataInputStream.readInt();
				for(int i = 0; i < size; i++) {
					RetentionIndexType retentionIndexType = RetentionIndexType.valueOf(readString(dataInputStream));
					float retentionIndexAdditional = dataInputStream.readFloat();
					scanFID.setRetentionIndex(retentionIndexType, retentionIndexAdditional);
				}
			}
			int timeSegmentId = dataInputStream.readInt();
			int cycleNumber = dataInputStream.readInt();
			//
			scanFID.setRetentionTimeColumn1(retentionTimeColumn1);
			scanFID.setRetentionTimeColumn2(retentionTimeColumn2);
			scanFID.setRetentionIndex(retentionIndex);
			scanFID.setTimeSegmentId(timeSegmentId);
			scanFID.setCycleNumber(cycleNumber);
			chromatogram.addScan(scanFID);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readBaseline(DataInputStream dataInputStream, boolean closeStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

		/*
		 * Get the Baseline
		 */
		int scans = dataInputStream.readInt(); // Number of Scans
		List<IBaselineElement> baselineElements = new ArrayList<IBaselineElement>();
		for(int scan = 1; scan <= scans; scan++) {
			// monitor.subTask(IConstants.IMPORT_BASELINE + scan);
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
			 */
			IBaselineElement baselineElement = baselineElements.get(index);
			IBaselineElement baselineElementNext = baselineElements.get(index + 1);
			int startRetentionTime = baselineElement.getRetentionTime();
			float startBackgroundAbundance = baselineElement.getBackgroundAbundance();
			int stopRetentionTime = baselineElementNext.getRetentionTime();
			float stopBackgroundAbundance = baselineElementNext.getBackgroundAbundance();
			/*
			 * Set the baseline.
			 */
			baselineModel.addBaseline(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance, false);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readPeaks(DataInputStream dataInputStream, boolean closeStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

		int numberOfPeaks = dataInputStream.readInt(); // Number of Peaks
		for(int i = 1; i <= numberOfPeaks; i++) {
			// monitor.subTask(IConstants.IMPORT_PEAK + i);
			try {
				IChromatogramPeakCSD peak = readPeak(dataInputStream, chromatogram, monitor);
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

	private IChromatogramPeakCSD readPeak(DataInputStream dataInputStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException, IllegalArgumentException, PeakException {

		String detectorDescription = readString(dataInputStream); // Detector Description
		String quantifierDescription = readString(dataInputStream);
		boolean activeForAnalysis = dataInputStream.readBoolean();
		String integratorDescription = readString(dataInputStream); // Integrator Description
		String modelDescription = readString(dataInputStream); // Model Description
		PeakType peakType = PeakType.valueOf(readString(dataInputStream)); // Peak Type
		int suggestedNumberOfComponents = dataInputStream.readInt(); // Suggest Number Of Components
		String classifier = readString(dataInputStream);
		//
		float startBackgroundAbundance = dataInputStream.readFloat(); // Start Background Abundance
		float stopBackgroundAbundance = dataInputStream.readFloat(); // Stop Background Abundance
		//
		int retentionTime = dataInputStream.readInt();
		int relativeRetentionTime = dataInputStream.readInt();
		float totalSignalScan = dataInputStream.readFloat();
		IVendorScan peakMaximum = new VendorScan(retentionTime, totalSignalScan);
		peakMaximum.setRelativeRetentionTime(relativeRetentionTime);
		int retentionTimeColumn1 = dataInputStream.readInt();
		int retentionTimeColumn2 = dataInputStream.readInt();
		float retentionIndexScan = dataInputStream.readFloat(); // Retention Index
		if(dataInputStream.readBoolean()) {
			int size = dataInputStream.readInt();
			for(int i = 0; i < size; i++) {
				RetentionIndexType retentionIndexType = RetentionIndexType.valueOf(readString(dataInputStream));
				float retentionIndexAdditional = dataInputStream.readFloat();
				peakMaximum.setRetentionIndex(retentionIndexType, retentionIndexAdditional);
			}
		}
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
		IPeakModelCSD peakModel = new PeakModelCSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		IChromatogramPeakCSD peak = new ChromatogramPeakCSD(peakModel, chromatogram);
		peak.setDetectorDescription(detectorDescription);
		peak.setQuantifierDescription(quantifierDescription);
		peak.setActiveForAnalysis(activeForAnalysis);
		peak.setIntegratorDescription(integratorDescription);
		peak.setModelDescription(modelDescription);
		peak.setPeakType(peakType);
		peak.setSuggestedNumberOfComponents(suggestedNumberOfComponents);
		peak.setClassifier(classifier);
		//
		List<IIntegrationEntry> integrationEntries = readIntegrationEntries(dataInputStream);
		peak.setIntegratedArea(integrationEntries, integratorDescription);
		//
		List<IInternalStandard> internalStandards = readInternalStandards(dataInputStream);
		peak.addInternalStandards(internalStandards);
		//
		return peak;
	}

	private void setAdditionalInformation(File file, IChromatogramCSD chromatogram, IProgressMonitor monitor) {

		chromatogram.setConverterId(IFormat.CONVERTER_ID);
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

	private List<IIntegrationEntry> readIntegrationEntries(DataInputStream dataInputStream) throws IOException {

		List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
		int numberOfIntegrationEntries = dataInputStream.readInt(); // Number Integration Entries
		for(int i = 1; i <= numberOfIntegrationEntries; i++) {
			double integratedArea = dataInputStream.readDouble(); // Integrated Area
			IIntegrationEntryCSD integrationEntry = new IntegrationEntryCSD(integratedArea);
			integrationEntries.add(integrationEntry);
		}
		return integrationEntries;
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

	private void readHistory(DataInputStream dataInputStream, boolean closeStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

		IEditHistory editHistory = chromatogram.getEditHistory();
		int numberOfEntries = dataInputStream.readInt(); // Number of entries
		SubMonitor subMonitor = SubMonitor.convert(monitor, numberOfEntries);
		for(int i = 1; i <= numberOfEntries; i++) {
			long time = dataInputStream.readLong(); // Date
			String description = readString(dataInputStream); // Description
			//
			Date date = new Date(time);
			IEditInformation editInformation = new EditInformation(date, description);
			editHistory.add(editInformation);
			subMonitor.worked(1);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readMiscellaneous(DataInputStream dataInputStream, boolean closeStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

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

	private void readSeparationColumn(DataInputStream dataInputStream, boolean closeStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

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
		ISeparationColumn separationColumn = separationColumnIndices.getSeparationColumn();
		separationColumn.setName(readString(dataInputStream));
		separationColumn.setLength(readString(dataInputStream));
		separationColumn.setDiameter(readString(dataInputStream));
		separationColumn.setPhase(readString(dataInputStream));
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private int readChromatogramReferenceInfo(DataInputStream dataInputStream, boolean closeStream, IProgressMonitor monitor) throws IOException {

		int size = dataInputStream.readInt();
		if(closeStream) {
			dataInputStream.close();
		}
		return size;
	}

	private void readReferencedChromatograms(Object object, String directoryPrefix, IChromatogramCSD chromatogram, int size, boolean closeStream, IProgressMonitor monitor) throws IOException {

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

	private void parseChromatogram(Object object, String dataType, String directoryPrefix, IChromatogramCSD chromatogram, boolean closeStream, IProgressMonitor monitor) throws IOException {

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
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.VERSION_1300)) {
			isValid = true;
		}
		//
		dataInputStream.close();
		//
		return isValid;
	}
}
