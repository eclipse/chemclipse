/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.jcampdx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model.IVendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model.IVendorScan;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model.VendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model.VendorScan;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.xxd.converter.supplier.jcampdx.support.IConstants;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramCSDReader {

	private static final String HEADER_TITLE = "##TITLE=";
	private static final String HEADER_PROGRAM = "##PROGRAM=";
	private static final String RETENTION_TIME_MARKER = "##RETENTION_TIME=";
	private static final String TIME_MARKER = "##TIME=";
	private static final String TIC_MARKER = "##TIC=";
	private static final String NAME_MARKER = "##NAME=";
	private static final String HIT_MARKER = "##HIT=";

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file, monitor);
		}
		return null;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file, monitor);
		}
		return null;
	}

	private IChromatogramCSD readChromatogram(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		//
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		/*
		 * Parse each line
		 */
		String name = "";
		while((line = bufferedReader.readLine()) != null) {
			/*
			 * Each scan starts with the marker:
			 * [##NAME=Acetoin (Butanon-2, 3-hydroxy-)]
			 * ##TIC=27243
			 * ##SCAN=1
			 * ##TIME=102.3358
			 * ##NPOINTS=0
			 * ##XYDATA=(X,Y)
			 * ...
			 * [##HIT=90]
			 */
			if(line.startsWith(NAME_MARKER)) {
				name = line.trim();
			} else if(line.startsWith(TIC_MARKER)) {
				String value = line.replace(TIC_MARKER, "").trim();
				float abundance = Float.parseFloat(value); // TIC
				bufferedReader.readLine(); // SCAN
				//
				if((line = bufferedReader.readLine()) != null) {
					if(line.startsWith(RETENTION_TIME_MARKER) || line.startsWith(TIME_MARKER)) {
						int retentionTime = getRetentionTime(line);
						IVendorScan scan = new VendorScan(abundance);
						scan.setRetentionTime(retentionTime);
						chromatogram.addScan(scan);
						/*
						 * Add the identification
						 */
						if(!name.equals("")) {
							/*
							 * Find the hit value and set it.
							 */
							boolean findHitMarker = true;
							while((line = bufferedReader.readLine()) != null && findHitMarker) {
								if(line.startsWith(HIT_MARKER)) {
									String hitValue = line.replace(HIT_MARKER, "").trim();
									Double.parseDouble(hitValue);
									findHitMarker = false;
								} else if(line.startsWith(NAME_MARKER) || line.startsWith(TIC_MARKER)) {
									findHitMarker = false;
								}
							}
						}
					}
				}
				/*
				 * Set name to ""
				 */
				name = "";
			}
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
		chromatogram.setConverterId(IConstants.CONVERTER_ID_CSD);
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
		int retentionTime = 0;
		if(line.startsWith(RETENTION_TIME_MARKER)) {
			String value = line.replace(RETENTION_TIME_MARKER, "").trim();
			retentionTime = (int)(Double.parseDouble(value) * 1000.0d);
		} else if(line.startsWith(TIME_MARKER)) {
			String value = line.replace(TIME_MARKER, "").trim();
			retentionTime = (int)(Double.parseDouble(value) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
		}
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
		if(line.startsWith(HEADER_TITLE) || line.startsWith(HEADER_PROGRAM)) {
			return true;
		} else {
			return false;
		}
	}
}
