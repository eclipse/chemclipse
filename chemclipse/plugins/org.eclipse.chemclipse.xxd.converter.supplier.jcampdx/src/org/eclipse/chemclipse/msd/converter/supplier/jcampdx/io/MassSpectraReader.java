/*******************************************************************************
 * Copyright (c) 2015, 2021 Lablicate GmbH.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.preferences.PreferenceSupplier;
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
	private static final String COMMENT_MARKER = "$$";
	private static final String HEADER_MARKER = "##";
	private static final String HEADER_TITLE = "##TITLE=";
	private static final String HEADER_VERSION = "##VERSION=";
	private static final String HEADER_PROGRAM_MASSFINDER_193 = "##PROGRAM=MassFinder 1.93";
	private static final String HEADER_PROGRAM_MASSFINDER_230 = "##PROGRAM=MassFinder 2.3";
	private static final String HEADER_PROGRAM_MASSFINDER_300 = "##PROGRAM=MassFinder3";
	private static final String RETENTION_TIME_MARKER = "##RETENTION_TIME=";
	private static final String RETENTION_INDEX_MARKER = "##$RETENTION INDEX=";
	private static final String RETENTION_INDEX = "##RI=";
	private static final String RETENTIONINDEX = "##RETENTIONINDEX=";
	private static final String CAS_REGISTRY_NO = "##CAS REGISTRY NO=";
	private static final String CAS_NAME = "##CAS NAME=";
	private static final String CAS = "##CAS=";
	private static final String COMMENT = "##COMMENT=";
	private static final String FORMULA = "##FORMULA=";
	private static final String MOL_WEIGHT = "##MW=";
	private static final String MOL_FORM = "##MOLFORM=";
	private static final String TIME_MARKER = "##TIME=";
	private static final String TITLE_MARKER = "##TITLE=";
	private static final String NAME_MARKER = "##NAME=";
	private static final String NAMES_MARKER = "##NAMES=";
	private static final String XYDATA_MARKER_SPACE_TYPE1 = "##XYDATA= (XY..XY)";
	private static final String XYDATA_MARKER_SPACE_TYPE2 = "##XYDATA=(XY..XY)";
	private static final String XYDATA_MARKER_SHORT = "##XYDATA=(X,Y)";
	private static final String XYDATA_MARKER_NOMINAL = "##XYDATA=(X Y)";
	private static final String PEAK_TABLE_MARKER_TYPE1 = "##PEAK TABLE=(XY..XY)";
	private static final String PEAK_TABLE_MARKER_TYPE2 = "##PEAK TABLE= (XY..XY)";
	//
	private static final Pattern ionPattern = Pattern.compile("(\\d+\\.?\\d{0,5})(.*?)(\\d+\\.?\\d{0,5})");

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		if(isValidFileFormat(file)) {
			boolean isNameMarkerAvailable = isNameMarkerAvailable(file);
			return extractMassSpectra(file, isNameMarkerAvailable, monitor);
		}
		return null;
	}

	private IMassSpectra extractMassSpectra(File file, boolean isNameMarkerAvailable, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		String referenceIdentifierMarker = PreferenceSupplier.getReferenceIdentifierMarker();
		String referenceIdentifierPrefix = PreferenceSupplier.getReferenceIdentifierPrefix();
		//
		IMassSpectra massSpectra = new MassSpectra();
		IVendorLibraryMassSpectrum massSpectrum = null;
		IVendorIon ion;
		//
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		boolean readIons = false;
		/*
		 * Parse each line
		 */
		while((line = bufferedReader.readLine()) != null) {
			/*
			 * Strip line comments
			 */
			if(line.contains(COMMENT_MARKER)) {
				line = line.substring(0, line.indexOf(COMMENT_MARKER));
			}
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
			if(addNewMassSpectrum(line, isNameMarkerAvailable)) {
				/*
				 * Try to get the identification.
				 */
				String name;
				if(isNameMarkerAvailable) {
					if(line.startsWith(NAME_MARKER)) {
						name = line.replace(NAME_MARKER, "").trim();
					} else {
						name = line.replace(NAMES_MARKER, "").trim();
					}
				} else {
					name = line.replace(TITLE_MARKER, "").trim();
				}
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
					int retentionTime = getRetentionTime(line);
					massSpectrum.setRetentionTime(retentionTime);
				} else if(line.startsWith(RETENTION_INDEX_MARKER)) {
					float retentionIndex = getRetentionIndex(line, RETENTION_INDEX_MARKER);
					massSpectrum.setRetentionIndex(retentionIndex);
				} else if(line.startsWith(RETENTION_INDEX)) {
					float retentionIndex = getRetentionIndex(line, RETENTION_INDEX);
					massSpectrum.setRetentionIndex(retentionIndex);
				} else if(line.startsWith(RETENTIONINDEX)) {
					float retentionIndex = getRetentionIndex(line, RETENTIONINDEX);
					massSpectrum.setRetentionIndex(retentionIndex);
				} else if(line.startsWith(CAS_REGISTRY_NO)) {
					String casNumber = line.replace(CAS_REGISTRY_NO, "").trim();
					massSpectrum.getLibraryInformation().setCasNumber(casNumber);
				} else if(line.startsWith(COMMENT)) {
					String comment = line.replace(COMMENT, "").trim();
					massSpectrum.getLibraryInformation().setComments(comment);
				} else if(line.startsWith(FORMULA)) {
					String formula = line.replace(FORMULA, "").trim();
					massSpectrum.getLibraryInformation().setFormula(formula);
				} else if(line.startsWith(CAS_NAME)) {
					String name = line.replace(CAS_NAME, "").trim();
					extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
				} else if(line.startsWith(CAS)) {
					String casNumber = line.replace(CAS, "").trim();
					massSpectrum.getLibraryInformation().setCasNumber(casNumber);
				} else if(line.startsWith(MOL_WEIGHT)) {
					double molWeight = getMolWeight(line);
					massSpectrum.getLibraryInformation().setMolWeight(molWeight);
				} else if(line.startsWith(MOL_FORM)) {
					String formula = line.replace(MOL_FORM, "").trim();
					massSpectrum.getLibraryInformation().setFormula(formula);
				} else if(isReadIons(line)) {
					/*
					 * Mark to read ions.
					 */
					readIons = true;
				} else if(!line.startsWith(HEADER_MARKER) && readIons) {
					/*
					 * Parse the ions.
					 */
					try {
						line = line.trim();
						Matcher ions = ionPattern.matcher(line.trim());
						while(ions.find()) {
							double mz = Double.parseDouble(ions.group(1));
							float abundance = Float.parseFloat(ions.group(3));
							if(abundance >= VendorIon.MIN_ABUNDANCE && abundance <= VendorIon.MAX_ABUNDANCE) {
								ion = new VendorIon(mz, abundance);
								massSpectrum.addIon(ion);
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

	private boolean isReadIons(String line) {

		return (line.startsWith(XYDATA_MARKER_SPACE_TYPE1) || line.startsWith(XYDATA_MARKER_SPACE_TYPE2) || line.startsWith(XYDATA_MARKER_SHORT) || line.startsWith(XYDATA_MARKER_NOMINAL) || line.startsWith(PEAK_TABLE_MARKER_TYPE1) || line.startsWith(PEAK_TABLE_MARKER_TYPE2));
	}

	private boolean addNewMassSpectrum(String line, boolean isNameMarkerAvailable) {

		if(isNameMarkerAvailable) {
			if(line.startsWith(NAME_MARKER) || line.startsWith(NAMES_MARKER)) {
				return true;
			}
		} else {
			if(line.startsWith(TITLE_MARKER)) {
				return true;
			}
		}
		return false;
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

	private float getRetentionIndex(String line, String marker) {

		float retentionIndex = 0;
		try {
			if(line.startsWith(marker)) {
				String value = line.replace(marker, "").trim();
				retentionIndex = (float)(Double.parseDouble(value));
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return retentionIndex;
	}

	private double getMolWeight(String line) {

		double molWeight = 0;
		try {
			if(line.startsWith(MOL_WEIGHT)) {
				String value = line.replace(MOL_WEIGHT, "").trim();
				molWeight = Double.parseDouble(value);
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return molWeight;
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
		if(line.startsWith(HEADER_TITLE) || line.startsWith(HEADER_VERSION) || line.startsWith(HEADER_PROGRAM_MASSFINDER_193) || line.startsWith(HEADER_PROGRAM_MASSFINDER_230) || line.startsWith(HEADER_PROGRAM_MASSFINDER_300)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isNameMarkerAvailable(File file) throws IOException {

		boolean nameMarkerAvailable = false;
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		/*
		 * Check the first column header.
		 */
		String line;
		exitloop:
		while((line = bufferedReader.readLine()) != null) {
			if(line.startsWith(NAME_MARKER) || line.startsWith(NAMES_MARKER)) {
				nameMarkerAvailable = true;
				break exitloop;
			}
		}
		//
		bufferedReader.close();
		fileReader.close();
		return nameMarkerAvailable;
	}
}
