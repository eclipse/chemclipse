/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
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
import java.util.List;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDReader;
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
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.BaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IBaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramReader_1004 extends AbstractChromatogramReader implements IChromatogramCSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader_1004.class);

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, monitor);
	}

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, monitor);
	}

	private IChromatogramCSD readChromatogram(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IVendorChromatogram chromatogram = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				/*
				 * Read the chromatographic information.
				 */
				monitor.subTask(IConstants.IMPORT_CHROMATOGRAM);
				chromatogram = new VendorChromatogram();
				readScans(zipFile, chromatogram, monitor);
				readBaseline(zipFile, chromatogram, monitor);
				readPeaks(zipFile, chromatogram, monitor);
				//
				setAdditionalInformation(file, chromatogram, monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return chromatogram;
	}

	private void readScans(ZipFile zipFile, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_SCANS_FID);
		/*
		 * Scans
		 */
		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; scan++) {
			monitor.subTask(IConstants.IMPORT_SCAN + scan);
			//
			int retentionTime = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat();
			float totalSignal = dataInputStream.readFloat();
			int timeSegmentId = dataInputStream.readInt();
			int cycleNumber = dataInputStream.readInt();
			//
			IVendorScan scanFID = new VendorScan(retentionTime, totalSignal);
			scanFID.setRetentionIndex(retentionIndex);
			scanFID.setTimeSegmentId(timeSegmentId);
			scanFID.setCycleNumber(cycleNumber);
			chromatogram.addScan(scanFID);
		}
		//
		dataInputStream.close();
	}

	private void readBaseline(ZipFile zipFile, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_BASELINE_FID);
		/*
		 * Get the Baseline
		 */
		int scans = dataInputStream.readInt(); // Number of Scans
		List<IBaselineElement> baselineElements = new ArrayList<IBaselineElement>();
		for(int scan = 1; scan <= scans; scan++) {
			monitor.subTask(IConstants.IMPORT_BASELINE + scan);
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
		dataInputStream.close();
	}

	private void readPeaks(ZipFile zipFile, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_PEAKS_FID);
		//
		int numberOfPeaks = dataInputStream.readInt(); // Number of Peaks
		for(int i = 1; i <= numberOfPeaks; i++) {
			monitor.subTask(IConstants.IMPORT_PEAK + i);
			try {
				IChromatogramPeakCSD peak = readPeak(dataInputStream, chromatogram, monitor);
				chromatogram.addPeak(peak);
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
		dataInputStream.close();
	}

	private IChromatogramPeakCSD readPeak(DataInputStream dataInputStream, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException, IllegalArgumentException, PeakException {

		String detectorDescription = readString(dataInputStream); // Detector Description
		String integratorDescription = readString(dataInputStream); // Integrator Description
		String modelDescription = readString(dataInputStream); // Model Description
		PeakType peakType = PeakType.valueOf(readString(dataInputStream)); // Peak Type
		int suggestedNumberOfComponents = dataInputStream.readInt(); // Suggest Number Of Components
		//
		float startBackgroundAbundance = dataInputStream.readFloat(); // Start Background Abundance
		float stopBackgroundAbundance = dataInputStream.readFloat(); // Stop Background Abundance
		//
		int retentionTimeScan = dataInputStream.readInt();
		float retentionIndexScan = dataInputStream.readFloat();
		float totalSignalScan = dataInputStream.readFloat();
		int timeSegmentId = dataInputStream.readInt();
		int cycleNumber = dataInputStream.readInt();
		//
		IVendorScan peakMaximum = new VendorScan(retentionTimeScan, totalSignalScan);
		peakMaximum.setRetentionIndex(retentionIndexScan);
		peakMaximum.setTimeSegmentId(timeSegmentId);
		peakMaximum.setCycleNumber(cycleNumber);
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
		IPeakModelCSD peakModel = new PeakModelCSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		IChromatogramPeakCSD peak = new ChromatogramPeakCSD(peakModel, chromatogram);
		peak.setDetectorDescription(detectorDescription);
		peak.setIntegratorDescription(integratorDescription);
		peak.setModelDescription(modelDescription);
		peak.setPeakType(peakType);
		peak.setSuggestedNumberOfComponents(suggestedNumberOfComponents);
		//
		List<IIntegrationEntry> integrationEntries = readIntegrationEntries(dataInputStream);
		peak.setIntegratedArea(integrationEntries, integratorDescription);
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

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.VERSION_1004)) {
			isValid = true;
		}
		//
		dataInputStream.close();
		//
		return isValid;
	}
}
