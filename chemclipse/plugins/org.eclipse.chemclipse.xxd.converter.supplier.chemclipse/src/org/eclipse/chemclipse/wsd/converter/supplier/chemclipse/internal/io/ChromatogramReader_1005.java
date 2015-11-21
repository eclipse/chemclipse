/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.wsd.converter.io.IChromatogramWSDReader;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.IVendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorScanSignal;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.BaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IBaselineElement;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramReader_1005 extends AbstractChromatogramReader implements IChromatogramWSDReader {

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramOverview chromatogramOverview = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				chromatogramOverview = readOverviewFromZipFile(zipFile, monitor);
			}
		} finally {
			zipFile.close();
		}
		return chromatogramOverview;
	}

	private IChromatogramOverview readOverviewFromZipFile(ZipFile zipFile, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_TIC_WSD);
		//
		IVendorChromatogram chromatogram = new VendorChromatogram();
		readScansOverview(dataInputStream, chromatogram, monitor);
		//
		dataInputStream.close();
		//
		return chromatogram;
	}

	private void readScansOverview(DataInputStream dataInputStream, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		IVendorScan _vendorScan;
		int scans = dataInputStream.readInt();
		//
		for(int scan = 1; scan <= scans; scan++) {
			//
			_vendorScan = new VendorScan();
			//
			int retentionTime = dataInputStream.readInt();
			float totalSignal = dataInputStream.readFloat();
			//
			_vendorScan.setRetentionTime(retentionTime);
			_vendorScan.adjustTotalSignal(totalSignal);
			//
			chromatogram.addScan(_vendorScan);
		}
	}

	@Override
	public IChromatogramWSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramWSD chromatogram = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				monitor.subTask(IConstants.IMPORT_CHROMATOGRAM);
				chromatogram = readFromZipFile(zipFile, file, monitor);
			}
		} finally {
			zipFile.close();
		}
		return chromatogram;
	}

	private IChromatogramWSD readFromZipFile(ZipFile zipFile, File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		/*
		 * Read chromatographic information... :-)
		 */
		readScans(zipFile, chromatogram, monitor);
		readBaselines(zipFile, chromatogram, monitor);
		readHistory(zipFile, chromatogram, monitor);
		readMiscellaneous(zipFile, chromatogram, monitor);
		//
		return chromatogram;
	}

	private void readScans(ZipFile zipFile, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_SCANS_WSD);
		//
		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; ++scan) {
			monitor.subTask(IConstants.IMPORT_SCAN + scan);
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
		//
		dataInputStream.close();
	}

	private void readBaselines(ZipFile zipFile, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_BASELINE_WSD);
		/*
		 * Baselines
		 */
		int scans = dataInputStream.readInt();
		List<IBaselineElement> baselineElements = new ArrayList<IBaselineElement>();
		for(int scan = 1; scan <= scans; ++scan) {
			monitor.subTask(IConstants.IMPORT_SCAN + scan);
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
			monitor.subTask(IConstants.IMPORT_BASELINE + index);
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
		dataInputStream.close();
	}

	private void readHistory(ZipFile zipFile, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_HISTORY_WSD);
		//
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
		dataInputStream.close();
	}

	private void readMiscellaneous(ZipFile zipFile, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_MISC_WSD);
		//
		long time = dataInputStream.readLong();
		String miscInfo = readString(dataInputStream);
		String operator = readString(dataInputStream);
		//
		Date date = new Date(time);
		chromatogram.setDate(date);
		chromatogram.setMiscInfo(miscInfo);
		chromatogram.setOperator(operator);
		//
		dataInputStream.close();
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.VERSION_1005)) {
			isValid = true;
		}
		//
		dataInputStream.close();
		//
		return isValid;
	}
}
