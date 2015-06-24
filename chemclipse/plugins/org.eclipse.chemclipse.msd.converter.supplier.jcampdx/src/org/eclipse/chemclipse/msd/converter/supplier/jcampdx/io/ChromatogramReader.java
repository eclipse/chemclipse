/*******************************************************************************
 * Copyright (c) 2013, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.jcampdx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.internal.converter.IConstants;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.VendorScan;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.logging.core.Logger;

public class ChromatogramReader extends AbstractChromatogramMSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);
	private static final String HEADER = "##TITLE=";
	private static final String RETENTION_TIME_MARKER = "##RETENTION_TIME=";
	private static final String TIC_MARKER = "##TIC=";
	private static final String SCAN_MARKER = "##SCAN_NUMBER=";
	private static final String XYDATA_MARKER = "##XYDATA";
	private static final String ION_MARKER = " ";
	private static final String ION_DELIMITER = ",";

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file, monitor);
		}
		return null;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		if(isValidFileFormat(file)) {
			return readChromatogramOverview(file, monitor);
		}
		return null;
	}

	private IChromatogramMSD readChromatogram(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		IVendorScan massSpectrum = null;
		IVendorIon ion;
		//
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		int retentionTime = 0;
		boolean readIons = false;
		/*
		 * Parse each line
		 */
		while((line = bufferedReader.readLine()) != null) {
			/*
			 * Each scan starts with the marker:
			 * ##SCAN_NUMBER= 1
			 * ##RETENTION_TIME= 1.78
			 * ##NPOINTS= 3
			 * ##XYDATA= (XY..XY)
			 * 40.01681, 4352
			 * 41.07158, 221
			 * 44.05768, 36
			 * ...
			 */
			if(line.startsWith(SCAN_MARKER)) {
				/*
				 * Store an existing scan.
				 */
				if(massSpectrum != null) {
					chromatogram.addScan(massSpectrum);
				}
				/*
				 * Create a new scan.
				 */
				massSpectrum = new VendorScan();
				readIons = false;
				/*
				 * Read the next line.
				 */
				continue;
			}
			/*
			 * Read the scan data.
			 * The SCAN_MARKER section has been accessed.
			 */
			if(massSpectrum != null) {
				/*
				 * Parse the scan data
				 */
				if(line.startsWith(RETENTION_TIME_MARKER)) {
					retentionTime = getRetentionTime(line);
					massSpectrum.setRetentionTime(retentionTime);
				} else if(line.startsWith(XYDATA_MARKER)) {
					/*
					 * Mark to read ions.
					 */
					readIons = true;
				} else if(line.startsWith(ION_MARKER) && readIons) {
					/*
					 * Parse the ions.
					 */
					line = line.trim();
					String[] values = line.split(ION_DELIMITER);
					if(values.length == 2) {
						double mz = Double.parseDouble(values[0].trim());
						float abundance = Float.parseFloat(values[1].trim());
						try {
							ion = new VendorIon(mz, abundance);
							massSpectrum.addIon(ion);
						} catch(AbundanceLimitExceededException e) {
							logger.warn(e);
						} catch(IonLimitExceededException e) {
							logger.warn(e);
						}
					}
				}
			}
		}
		/*
		 * Add the last scan.
		 */
		if(massSpectrum != null) {
			chromatogram.addScan(massSpectrum);
		}
		/*
		 * Set the scan delay and interval
		 * file and converter id.
		 */
		int scanDelay = chromatogram.getScan(1).getRetentionTime();
		chromatogram.setScanDelay(scanDelay);
		int scanInterval = chromatogram.getStartRetentionTime() / chromatogram.getNumberOfScans();
		chromatogram.setScanInterval(scanInterval);
		//
		chromatogram.setFile(file);
		chromatogram.setConverterId(IConstants.CONVERTER_ID);
		/*
		 * Close the streams
		 */
		bufferedReader.close();
		fileReader.close();
		//
		return chromatogram;
	}

	private IChromatogramMSD readChromatogramOverview(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		//
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		boolean retentionTimeIsAvailable = false;
		int retentionTime = 0;
		/*
		 * Parse each line
		 */
		while((line = bufferedReader.readLine()) != null) {
			/*
			 * Extract the TIC values
			 * ##RETENTION_TIME= 2.90
			 * ##TIC= 3875
			 */
			if(line.startsWith(RETENTION_TIME_MARKER)) {
				retentionTime = getRetentionTime(line);
				retentionTimeIsAvailable = true;
			} else {
				/*
				 * Set a tic value only if a retention time is available.
				 */
				if(line.startsWith(TIC_MARKER) && retentionTimeIsAvailable) {
					String value = line.replace(TIC_MARKER, "").trim();
					float abundance = Float.parseFloat(value);
					try {
						IVendorIon ion = new VendorIon(AbstractIon.TIC_ION, abundance);
						IVendorScan massSpectrum = new VendorScan();
						/*
						 * Set retentionTimeIsAvailable = false
						 * to wait for the next retention
						 * time event and to avoid setting
						 * a wrong retention time for the
						 * current scan.
						 */
						massSpectrum.setRetentionTime(retentionTime);
						retentionTimeIsAvailable = false;
						//
						massSpectrum.addIon(ion);
						chromatogram.addScan(massSpectrum);
					} catch(AbundanceLimitExceededException e) {
						logger.warn(e);
					} catch(IonLimitExceededException e) {
						logger.warn(e);
					}
				}
			}
		}
		/*
		 * Close the streams
		 */
		bufferedReader.close();
		fileReader.close();
		//
		return chromatogram;
	}

	private int getRetentionTime(String line) {

		/*
		 * The retention time is stored in seconds scale.
		 * Milliseconds = seconds * 1000.0d
		 */
		String value = line.replace(RETENTION_TIME_MARKER, "").trim();
		int retentionTime = (int)(Double.parseDouble(value) * 1000.0d);
		return retentionTime;
	}

	private boolean isValidFileFormat(File file) throws IOException {

		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		/*
		 * Check the first column header.
		 */
		String line = bufferedReader.readLine();
		//
		bufferedReader.close();
		fileReader.close();
		//
		return line.startsWith(HEADER);
	}
}
