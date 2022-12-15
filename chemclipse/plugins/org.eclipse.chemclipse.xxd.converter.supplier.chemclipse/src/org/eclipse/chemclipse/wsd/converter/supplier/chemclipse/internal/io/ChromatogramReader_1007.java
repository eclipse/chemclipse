/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.internal.io;

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
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IMethod;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io.IChromatogramWSDZipReader;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.IVendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorScanSignal;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.BaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IBaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramReader_1007 extends AbstractChromatogramReader implements IChromatogramWSDZipReader {

	@Override
	public IChromatogramWSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramWSD chromatogram = null;
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

	private IChromatogramOverview readOverviewFromZipFile(ZipFile zipFile, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, directoryPrefix + IFormat.FILE_TIC_WSD);
		//
		IVendorChromatogram chromatogram = new VendorChromatogram();
		readScansOverview(dataInputStream, chromatogram, monitor);
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
		//
		IVendorChromatogram chromatogram = null;
		/*
		 * Read the chromatographic information.
		 */
		// monitor.subTask(IConstants.IMPORT_CHROMATOGRAM);
		chromatogram = new VendorChromatogram();
		readMethod(getDataInputStream(object, directoryPrefix + IFormat.FILE_SYSTEM_SETTINGS_WSD), closeStream, chromatogram, monitor);
		readScans(getDataInputStream(object, directoryPrefix + IFormat.FILE_SCANS_WSD), closeStream, chromatogram, monitor);
		readBaselines(getDataInputStream(object, directoryPrefix + IFormat.FILE_BASELINE_WSD), closeStream, chromatogram, monitor);
		readHistory(getDataInputStream(object, directoryPrefix + IFormat.FILE_HISTORY_WSD), closeStream, chromatogram, monitor);
		readMiscellaneous(getDataInputStream(object, directoryPrefix + IFormat.FILE_MISC_WSD), closeStream, chromatogram, monitor);
		//
		setAdditionalInformation(file, chromatogram, monitor);
		//
		return chromatogram;
	}

	private void readScansOverview(DataInputStream dataInputStream, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; ++scan) {
			// monitor.subTask(IConstants.IMPORT_SCAN + scan);
			IScanWSD scanObject = new VendorScan();
			int scanSignals = dataInputStream.readInt();
			//
			for(int scanSignal = 0; scanSignal < scanSignals; ++scanSignal) {
				IScanSignalWSD scanSignalObject = new VendorScanSignal();
				int wavelength = dataInputStream.readInt();
				float abundance = dataInputStream.readFloat();
				//
				scanSignalObject.setWavelength(wavelength);
				scanSignalObject.setAbundance(abundance);
				//
				scanObject.addScanSignal(scanSignalObject);
			}
			//
			int retentionTime = dataInputStream.readInt();
			float retentionIndex = dataInputStream.readFloat();
			float totalSignal = dataInputStream.readFloat();
			int timeSegmentId = dataInputStream.readInt();
			int cycleNumber = dataInputStream.readInt();
			//
			scanObject.setRetentionTime(retentionTime);
			scanObject.setRetentionIndex(retentionIndex);
			scanObject.setTimeSegmentId(timeSegmentId);
			scanObject.setCycleNumber(cycleNumber);
			scanObject.adjustTotalSignal(totalSignal);
			chromatogram.addScan(scanObject);
		}
	}

	private void readMethod(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

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

	private void readScans(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; ++scan) {
			// monitor.subTask(IConstants.IMPORT_SCAN + scan);
			IScanWSD scanWSD = new VendorScan();
			int scanSignals = dataInputStream.readInt();
			//
			for(int scanSignal = 0; scanSignal < scanSignals; ++scanSignal) {
				IScanSignalWSD scanSignalObject = new VendorScanSignal();
				int wavelength = dataInputStream.readInt();
				float abundance = dataInputStream.readFloat();
				//
				scanSignalObject.setWavelength(wavelength);
				scanSignalObject.setAbundance(abundance);
				//
				scanWSD.addScanSignal(scanSignalObject);
			}
			//
			int retentionTime = dataInputStream.readInt();
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
			scanWSD.setRetentionTimeColumn1(retentionTimeColumn1);
			scanWSD.setRetentionTimeColumn2(retentionTimeColumn2);
			scanWSD.setRetentionIndex(retentionIndex);
			scanWSD.setTimeSegmentId(timeSegmentId);
			scanWSD.setCycleNumber(cycleNumber);
			scanWSD.adjustTotalSignal(totalSignal);
			chromatogram.addScan(scanWSD);
		}
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private void readBaselines(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		int scans = dataInputStream.readInt();
		List<IBaselineElement> baselineElements = new ArrayList<IBaselineElement>();
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

	private void readHistory(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

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

	private void readMiscellaneous(DataInputStream dataInputStream, boolean closeStream, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		long time = dataInputStream.readLong();
		String miscInfo = readString(dataInputStream);
		String miscInfoSeparated = readString(dataInputStream);
		String dataName = readString(dataInputStream);
		String operator = readString(dataInputStream);
		//
		Date date = new Date(time);
		chromatogram.setDate(date);
		chromatogram.setMiscInfo(miscInfo);
		chromatogram.setMiscInfoSeparated(miscInfoSeparated);
		chromatogram.setDataName(dataName);
		chromatogram.setOperator(operator);
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.CHROMATOGRAM_VERSION_1007)) {
			isValid = true;
		}
		//
		dataInputStream.close();
		//
		return isValid;
	}

	private void setAdditionalInformation(File file, IChromatogramWSD chromatogram, IProgressMonitor monitor) {

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
}