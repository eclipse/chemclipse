/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class ConverterCID {

	private static final Logger logger = Logger.getLogger(ConverterCID.class);
	//
	private static final String MARKER_ENTRY = "|";
	//
	private static final String MARKER_CAS = "C";
	private static final String MARKER_FORMULA = "F";
	private static final String MARKER_RETENTION_TIME = "RT";
	private static final String MARKER_RETENTION_INDEX = "RI";
	private static final String MARKER_RETENTION_WINDOW = "RW";
	private static final String MARKER_SIGNAL_TO_NOISE = "RSN";
	private static final String MARKER_CHEMICAL_CLASS = "S";
	private static final String MARKER_REFERENCE_CONCENTRATION = "RC";
	private static final String MARKER_RESPONSE_FACTOR = "RF";
	private static final String MARKER_MIN_MATCH_FACTOR = "MM";
	private static final String MARKER_NUMBER = "N";

	public static List<CompoundInformation> convert(File file) {

		List<CompoundInformation> compoundList = new ArrayList<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			/*
			 * Parse each line.
			 */
			String line = null;
			CompoundInformation compoundInformation = null;
			int row = 0;
			//
			while((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if(line.startsWith(MARKER_ENTRY)) {
					/*
					 * Compound Header
					 */
					row = 0;
					compoundInformation = extract(line);
					if(compoundInformation != null && !compoundInformation.getCasNumber().isEmpty()) {
						compoundList.add(compoundInformation);
					}
				} else if(compoundInformation != null) {
					/*
					 * Compound Content
					 */
					if(row == 0) {
						compoundInformation.setName(line);
					} else {
						compoundInformation.getSynonyms().add(line);
					}
					row++;
				}
			}
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		return compoundList;
	}

	public static void transfer(List<CompoundInformation> compoundList, IMassSpectra massSpectra) {

		for(IScanMSD massSpectrum : massSpectra.getList()) {
			if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
				IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
				CompoundInformation compoundInformation = getCompoundInformation(libraryMassSpectrum, compoundList);
				if(compoundInformation != null) {
					/*
					 * Mass Spectrum
					 */
					int retentionTime = getRetentionTime(compoundInformation);
					if(retentionTime != 0) {
						libraryMassSpectrum.setRetentionTime(retentionTime);
					}
					//
					float retentionIndex = getRetentionIndex(compoundInformation);
					if(retentionIndex != 0.0f) {
						libraryMassSpectrum.setRetentionIndex(retentionIndex);
					}
					/*
					 * Library Information
					 */
					ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
					libraryInformation.setFormula(compoundInformation.getFormula());
					libraryInformation.getSynonyms().addAll(compoundInformation.getSynonyms());
					libraryInformation.setReferenceIdentifier(compoundInformation.getChemicalClass());
				}
			}
		}
	}

	/**
	 * Converts e.g. the file
	 * ---
	 * /home/.../library.msl
	 * to
	 * /home/.../library.cid
	 * ---
	 * If the file doesn't exist, null is returned.
	 * 
	 * @param file
	 * @return File
	 */
	public static File getFileCID(File file) {

		File fileCID = null;
		if(file.isFile()) {
			String path = file.getParentFile().getAbsolutePath();
			String fileBaseName = FilenameUtils.getBaseName(file.getName());
			//
			fileCID = new File(path + File.separator + fileBaseName + ".CID");
			if(!fileCID.exists()) {
				fileCID = new File(path + File.separator + fileBaseName + ".cid");
				if(!fileCID.exists()) {
					fileCID = null;
				}
			}
		}
		//
		return fileCID;
	}

	private static CompoundInformation extract(String line) {

		CompoundInformation compoundInformation = new CompoundInformation();
		String[] values = line.split("\\" + MARKER_ENTRY); // Escape RegEx
		//
		for(String value : values) {
			value = value.trim();
			if(value.startsWith(MARKER_CAS)) {
				compoundInformation.setCasNumber(getValue(value, 1));
			} else if(value.startsWith(MARKER_FORMULA)) {
				compoundInformation.setFormula(getValue(value, 1));
			} else if(value.startsWith(MARKER_RETENTION_TIME)) {
				compoundInformation.setRetentionTime(getValue(value, 2));
			} else if(value.startsWith(MARKER_RETENTION_INDEX)) {
				compoundInformation.setRetentionIndex(getValue(value, 2));
			} else if(value.startsWith(MARKER_RETENTION_WINDOW)) {
				compoundInformation.setRetentionWindow(getValue(value, 2));
			} else if(value.startsWith(MARKER_SIGNAL_TO_NOISE)) {
				compoundInformation.setSignalToNoise(getValue(value, 3));
			} else if(value.startsWith(MARKER_CHEMICAL_CLASS)) {
				compoundInformation.setChemicalClass(getValue(value, 1));
			} else if(value.startsWith(MARKER_REFERENCE_CONCENTRATION)) {
				compoundInformation.setReferenceConcentration(getValue(value, 2));
			} else if(value.startsWith(MARKER_RESPONSE_FACTOR)) {
				double compensationFactor = 0.0d;
				try {
					double responseFactor = Double.parseDouble(getValue(value, 2));
					if(responseFactor > 0.0d) {
						compensationFactor = 1.0d / responseFactor;
					}
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
				compoundInformation.setCompensationFactor(Double.toString(compensationFactor));
			} else if(value.startsWith(MARKER_MIN_MATCH_FACTOR)) {
				compoundInformation.setMinMatchFactor(getValue(value, 2));
			} else if(value.startsWith(MARKER_NUMBER)) {
				compoundInformation.setNumber(getValue(value, 1));
			} else {
				if(!value.isEmpty()) {
					compoundInformation.getMiscellaneous().add(value);
				}
			}
		}
		//
		return compoundInformation;
	}

	private static String getValue(String value, int lengthMarker) {

		return value.substring(lengthMarker, value.length());
	}

	private static CompoundInformation getCompoundInformation(IRegularLibraryMassSpectrum libraryMassSpectrum, List<CompoundInformation> compoundList) {

		for(CompoundInformation compoundInformation : compoundList) {
			if(libraryMassSpectrum.getLibraryInformation().getCasNumber().equals(compoundInformation.getCasNumber())) {
				return compoundInformation;
			}
		}
		//
		return null;
	}

	private static int getRetentionTime(CompoundInformation compoundInformation) {

		String retentionTime = compoundInformation.getRetentionTime();
		if(retentionTime != null && !retentionTime.isEmpty()) {
			try {
				return (int)(Double.parseDouble(compoundInformation.getRetentionTime()) * IChromatogram.MINUTE_CORRELATION_FACTOR);
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
		//
		return 0;
	}

	private static float getRetentionIndex(CompoundInformation compoundInformation) {

		String retentionIndex = compoundInformation.getRetentionIndex();
		if(retentionIndex != null && !retentionIndex.isEmpty()) {
			try {
				return Float.parseFloat(compoundInformation.getRetentionIndex());
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
		//
		return 0.0f;
	}
}