/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.jcampdx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.xxd.converter.supplier.jcampdx.support.IConstants;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectraReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(MassSpectraReader.class);
	private static final String HEADER_MARKER = "##";
	private static final String HEADER_TITLE = "##TITLE=";
	private static final String HEADER_VERSION = "##VERSION=";
	private static final String RETENTION_TIME_MARKER = "##RETENTION_TIME=";
	private static final String TIME_MARKER = "##TIME=";
	private static final String NAME_MARKER = "##NAME=";
	private static final String XYDATA_MARKER_SPACE = "##XYDATA= (XY..XY)";
	private static final String XYDATA_MARKER_SHORT = "##XYDATA=(X,Y)";
	private static final String ION_DELIMITER_COMMA = ",";
	private static final String ION_DELIMITER_WHITESPACE = " ";

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		if(isValidFileFormat(file)) {
			return extractMassSpectra(file, monitor);
		}
		return null;
	}

	private IMassSpectra extractMassSpectra(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IMassSpectra massSpectra = new MassSpectra();
		IVendorLibraryMassSpectrum massSpectrum = null;
		IVendorIon ion;
		//
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		int retentionTime = 0;
		boolean readIons = false;
		boolean readIonsSpace = false;
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
			if(line.startsWith(NAME_MARKER)) {
				/*
				 * Try to get the identification.
				 */
				String name = line.replace(NAME_MARKER, "").trim();
				/*
				 * Store an existing scan.
				 */
				if(massSpectrum != null && massSpectrum.getIons().size() > 0) {
					massSpectra.addMassSpectrum(massSpectrum);
				}
				/*
				 * Create a new scan.
				 */
				massSpectrum = new VendorLibraryMassSpectrum();
				ILibraryInformation libraryInformation = new LibraryInformation();
				libraryInformation.setName(name);
				libraryInformation.setComments("JCAMP-DX");
				massSpectrum.setLibraryInformation(libraryInformation);
				//
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
				if(line.startsWith(RETENTION_TIME_MARKER) || line.startsWith(TIME_MARKER)) {
					retentionTime = getRetentionTime(line);
					massSpectrum.setRetentionTime(retentionTime);
				} else if(line.startsWith(XYDATA_MARKER_SPACE) || line.startsWith(XYDATA_MARKER_SHORT)) {
					/*
					 * Mark to read ions.
					 */
					readIons = true;
					if(line.startsWith(XYDATA_MARKER_SPACE)) {
						readIonsSpace = true;
					} else {
						readIonsSpace = false;
					}
				} else if(!line.startsWith(HEADER_MARKER) && readIons) {
					/*
					 * Parse the ions.
					 */
					try {
						line = line.trim();
						if(readIonsSpace) {
							String[] values = line.split(ION_DELIMITER_COMMA);
							if(values.length == 2) {
								double mz = Double.parseDouble(values[0].trim());
								float abundance = Float.parseFloat(values[1].trim());
								if(abundance >= VendorIon.MIN_ABUNDANCE && abundance <= VendorIon.MAX_ABUNDANCE) {
									ion = new VendorIon(mz, abundance);
									massSpectrum.addIon(ion);
								}
							}
						} else {
							String[] values = line.split(ION_DELIMITER_WHITESPACE);
							if(values.length == 2) {
								double mz = Double.parseDouble(values[0].trim());
								float abundance = Float.parseFloat(values[1].trim());
								if(abundance >= VendorIon.MIN_ABUNDANCE && abundance <= VendorIon.MAX_ABUNDANCE) {
									ion = new VendorIon(mz, abundance);
									massSpectrum.addIon(ion);
								}
							}
						}
					} catch(AbundanceLimitExceededException e) {
						logger.warn(e);
					} catch(IonLimitExceededException e) {
						logger.warn(e);
					} catch(NumberFormatException e) {
						logger.warn(e);
					}
				}
			}
		}
		/*
		 * Add the last scan.
		 */
		if(massSpectrum != null && massSpectrum.getIons().size() > 0) {
			massSpectra.addMassSpectrum(massSpectrum);
		}
		//
		massSpectra.setName(file.getName());
		massSpectra.setConverterId(IConstants.CONVERTER_ID_MSD_LIBRARY);
		/*
		 * Close the streams
		 */
		bufferedReader.close();
		fileReader.close();
		//
		return massSpectra;
	}

	private int getRetentionTime(String line) {

		/*
		 * The retention time is stored in seconds scale.
		 * Milliseconds = seconds * 1000.0d
		 */
		int retentionTime = 0;
		try {
			if(line.startsWith(RETENTION_TIME_MARKER)) {
				String value = line.replace(RETENTION_TIME_MARKER, "").trim();
				retentionTime = (int)(Double.parseDouble(value) * 1000.0d);
			} else if(line.startsWith(TIME_MARKER)) {
				String value = line.replace(TIME_MARKER, "").trim();
				retentionTime = (int)(Double.parseDouble(value) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
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
		if(line.startsWith(HEADER_TITLE) || line.startsWith(HEADER_VERSION)) {
			return true;
		} else {
			return false;
		}
	}
}
