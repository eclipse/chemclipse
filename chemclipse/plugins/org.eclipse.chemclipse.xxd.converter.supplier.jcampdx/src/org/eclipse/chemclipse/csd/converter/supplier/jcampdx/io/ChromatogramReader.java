/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
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
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model.IVendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model.IVendorScan;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model.VendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model.VendorScan;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramCSDReader {

	public static final String CONVERTER_ID_CSD = "org.eclipse.chemclipse.csd.converter.supplier.jcampdx";
	//
	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);
	//
	private static final String HEADER_TITLE = "##TITLE=";
	private static final String HEADER_PROGRAM = "##PROGRAM=";
	private static final String RETENTION_TIME_MARKER = "##RETENTION_TIME=";
	private static final String TIME_MARKER = "##TIME=";
	private static final String TIC_MARKER = "##TIC=";
	private static final String NAME_MARKER = "##NAME=";
	private static final String HIT_MARKER = "##HIT=";
	// private static final String RRT_MARKER = "##RRT=";
	// private static final String SCAN = "##SCAN";
	//
	private static final String XYDATA_MARKER_SPACE = "##XYDATA= (XY..XY)";
	private static final String XYDATA_MARKER_SHORT = "##XYDATA=(X,Y)";
	private static final String HEADER_MARKER = "##";

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file);
		}
		return null;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws IOException {

		return read(file, monitor);
	}

	private IChromatogramCSD readChromatogram(File file) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		//
		try (FileReader fileReader = new FileReader(file)) {
			try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
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
						name = line.trim().replace(NAME_MARKER, "");
					} else if(line.startsWith(TIC_MARKER)) {
						float abundance = 0;
						try {
							String value = line.replace(TIC_MARKER, "").trim();
							abundance = Float.parseFloat(value); // TIC
						} catch(NumberFormatException e) {
							logger.warn(e);
						}
						/*
						 * Read until
						 * RETENTION_TIME_MARKER
						 * or
						 * TIME_MARKER
						 * is reached.
						 */
						boolean searchForRetentionTime = true;
						while(searchForRetentionTime) {
							if((line = bufferedReader.readLine()) != null) {
								if(isRetentionTimeMarker(line)) {
									searchForRetentionTime = false;
								}
							}
						}
						//
						if(line != null) {
							int retentionTime = getRetentionTime(line);
							if(retentionTime >= 0 && abundance > 0) {
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
									float matchFactor = 100.0f;
									while((line = bufferedReader.readLine()) != null && findHitMarker) {
										if(line.startsWith(HIT_MARKER)) {
											try {
												String hitValue = line.replace(HIT_MARKER, "").trim();
												matchFactor = Float.parseFloat(hitValue);
												findHitMarker = false;
											} catch(NumberFormatException e) {
												logger.warn(e);
											}
										} else if(line.startsWith(NAME_MARKER) || line.startsWith(TIC_MARKER)) {
											findHitMarker = false;
										}
									}
									/*
									 * Add the target.
									 */
									try {
										ILibraryInformation libraryInformation = new LibraryInformation();
										libraryInformation.setName(name);
										IComparisonResult comparisonResult = new ComparisonResult(matchFactor, matchFactor, 0.0f, 0.0f);
										IIdentificationTarget scanTargetCSD = new IdentificationTarget(libraryInformation, comparisonResult);
										scan.getTargets().add(scanTargetCSD);
									} catch(ReferenceMustNotBeNullException e) {
										logger.warn(e);
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
				chromatogram.setConverterId(CONVERTER_ID_CSD);
			}
		}
		//
		return chromatogram;
	}

	private boolean isRetentionTimeMarker(String line) {

		return line.startsWith(RETENTION_TIME_MARKER) || line.startsWith(TIME_MARKER);
	}

	private int getRetentionTime(String line) {

		/*
		 * The retention time is stored in seconds scale.
		 * Milliseconds = seconds * 1000.0d
		 */
		int retentionTime = -1;
		try {
			if(line.startsWith(RETENTION_TIME_MARKER)) {
				String value = line.replace(RETENTION_TIME_MARKER, "").trim();
				retentionTime = (int)(Double.parseDouble(value) * 1000.0d);
			} else if(line.startsWith(TIME_MARKER)) {
				String value = line.replace(TIME_MARKER, "").trim();
				retentionTime = (int)(Double.parseDouble(value) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return retentionTime;
	}

	private boolean isValidFileFormat(File file) throws IOException {

		boolean isValidFormat = true;
		try (FileReader fileReader = new FileReader(file)) {
			try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
				String line = bufferedReader.readLine();
				/*
				 * Check the first column header.
				 */
				if(line.startsWith(HEADER_TITLE) || line.startsWith(HEADER_PROGRAM)) {
					boolean readIons = false;
					exitloop:
					while((line = bufferedReader.readLine()) != null) {
						if(line.startsWith(XYDATA_MARKER_SPACE) || line.startsWith(XYDATA_MARKER_SHORT)) {
							readIons = true;
						} else if(!line.startsWith(HEADER_MARKER) && readIons) {
							isValidFormat = false;
							break exitloop;
						}
					}
				}
			}
		}
		return isValidFormat;
	}
}
