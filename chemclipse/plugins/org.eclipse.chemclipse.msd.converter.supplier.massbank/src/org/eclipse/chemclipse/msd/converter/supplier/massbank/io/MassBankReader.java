/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - allow reading from a stream instead of reading directly from a file, adding some extra information to the library, improve parsing
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.massbank.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.massbank.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.Polarity;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassBankReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final String TAG_DELIMITER = ":";
	private static final String SUB_TAG_DELIMITER = " ";
	//
	private static final String COMMENT = "COMMENT";
	private static final String LICENSE = "LICENSE";
	private static final String COPYRIGHT = "COPYRIGHT";
	private static final String AUTHORS = "AUTHORS";
	private static final String CHEMICAL_NAME = "CH$NAME";
	private static final String CHEMICAL_LINK = "CH$LINK";
	private static final String CHEMICAL_COMPOUND_CLASS = "CH$COMPOUND_CLASS";
	private static final String PEAK_LIST_MARKER = "PK$PEAK";
	private static final String CHEMICAL_FORMULA = "CH$FORMULA";
	private static final String CHEMICAL_EXACT_MASS = "CH$EXACT_MASS";
	private static final String CHEMICAL_SMILES = "CH$SMILES";
	private static final String CHEMICAL_INCHI = "CH$IUPAC";
	private static final String INSTRUMENT = "AC$INSTRUMENT";
	private static final String INSTRUMENT_TYPE = "AC$INSTRUMENT_TYPE";
	private static final String MASS_SPECTROMETRY = "AC$MASS_SPECTROMETRY";
	private static final String CHROMATOGRAPHY = "AC$CHROMATOGRAPHY";
	private static final String MASSSPECTRUM_FOCUSED_ION = "MS$FOCUSED_ION";
	private static final Logger logger = Logger.getLogger(MassBankReader.class);

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws IOException {

		IMassSpectra massSpectra = new MassSpectra();
		massSpectra.setConverterId("MassBank");
		try (FileInputStream inputStream = new FileInputStream(file)) {
			IScanMSD massSpectrum = readMassSpectrum(inputStream);
			if(!massSpectrum.isEmpty()) {
				massSpectra.addMassSpectrum(massSpectrum);
			}
		}
		return massSpectra;
	}

	public static IScanMSD readMassSpectrum(InputStream stream) throws IOException {

		VendorLibraryMassSpectrum massSpectrum = new VendorLibraryMassSpectrum();
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		//
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		String line;
		while((line = bufferedReader.readLine()) != null) {
			if(line.startsWith(PEAK_LIST_MARKER)) {
				line = parsePeakList(bufferedReader, massSpectrum);
				if(line == null) {
					continue;
				}
			}
			String[] values = line.split(TAG_DELIMITER, 2);
			if(values.length == 2) {
				String key = values[0].trim();
				String value = values[1].trim();
				switch(key) {
					case COPYRIGHT:
						addMisc(libraryInformation, "copyright: " + value);
						break;
					case LICENSE:
						addMisc(libraryInformation, "license: " + value);
						break;
					case COMMENT:
						addComment(libraryInformation, value);
						break;
					case CHEMICAL_FORMULA:
						libraryInformation.setFormula(value);
						break;
					case CHEMICAL_SMILES:
						libraryInformation.setSmiles(value);
						break;
					case CHEMICAL_NAME:
						libraryInformation.setName(value);
						break;
					case CHEMICAL_INCHI:
						libraryInformation.setInChI(value);
						break;
					case INSTRUMENT:
						massSpectrum.putProperty(IRegularLibraryMassSpectrum.PROPERTY_INSTRUMENT_NAME, value);
						break;
					case INSTRUMENT_TYPE:
						massSpectrum.putProperty(IRegularLibraryMassSpectrum.PROPERTY_INSTRUMENT_TYPE, value);
						break;
					case MASS_SPECTROMETRY:
						parseMassSpectrometrySubTag(value.trim(), massSpectrum);
						break;
					case CHROMATOGRAPHY:
						parseChromatographySubTag(value.trim(), massSpectrum);
						break;
					case CHEMICAL_LINK:
						parseLinkSubTag(value, massSpectrum);
						break;
					case MASSSPECTRUM_FOCUSED_ION:
						parseFocusedIonSubTag(value.trim(), massSpectrum);
						break;
					case CHEMICAL_COMPOUND_CLASS:
						libraryInformation.setCompoundClass(value);
						break;
					case AUTHORS:
						libraryInformation.setContributor(value);
						break;
					case CHEMICAL_EXACT_MASS:
						try {
							libraryInformation.setExactMass(Double.parseDouble(value));
						} catch(NumberFormatException e) {
							// ignore then...
						}
						break;
					default:
						break;
				}
			}
		}
		bufferedReader.close();
		return massSpectrum;
	}

	private static void addMisc(ILibraryInformation libraryInformation, String value) {

		String miscellaneous = libraryInformation.getMiscellaneous();
		if(miscellaneous != null && !miscellaneous.isEmpty()) {
			libraryInformation.setMiscellaneous(miscellaneous + ", " + value);
		} else {
			libraryInformation.setMiscellaneous(value);
		}
	}

	private static void addComment(ILibraryInformation libraryInformation, String value) {

		String comments = libraryInformation.getComments();
		if(comments != null && comments.length() > 0) {
			libraryInformation.setComments(comments + ", " + value);
		} else {
			libraryInformation.setComments(value);
		}
	}

	private static String parsePeakList(BufferedReader bufferedReader, VendorLibraryMassSpectrum massSpectrum) throws IOException {

		String line;
		while((line = bufferedReader.readLine()) != null) {
			if(line.startsWith("  ")) {
				String[] values = line.trim().split(" ");
				if(values.length == 3) {
					/*
					 * Parse the m/z and abundance.
					 */
					try {
						double mz = Double.parseDouble(values[0]);
						float abundance = Float.parseFloat(values[1]);
						IIon ion = new Ion(mz, abundance);
						massSpectrum.addIon(ion);
					} catch(Exception e) {
						logger.warn("Parsing peak line failed: " + e);
						continue;
					}
				}
			} else {
				break;
			}
		}
		return line;
	}

	private static void parseLinkSubTag(String subtag, VendorLibraryMassSpectrum massSpectrum) {

		String[] split = subtag.split(SUB_TAG_DELIMITER, 2);
		if(split.length == 2) {
			String tag = split[0].trim();
			String value = split[1].trim();
			if("CAS".equals(tag)) {
				massSpectrum.getLibraryInformation().setCasNumber(value);
			} else if("INCHIKEY".equals(tag)) {
				massSpectrum.getLibraryInformation().setInChI(value);
			}
		}
	}

	private static void parseFocusedIonSubTag(String subtag, VendorLibraryMassSpectrum massSpectrum) {

		String[] split = subtag.split(SUB_TAG_DELIMITER, 2);
		if(split.length == 2) {
			String tag = split[0].trim();
			String value = split[1].trim();
			if("PRECURSOR_M/Z".equals(tag)) {
				try {
					massSpectrum.setPrecursorIon(Double.parseDouble(value));
				} catch(RuntimeException e) {
					// can't use then...
				}
			} else if("PRECURSOR_TYPE".equals(tag)) {
				massSpectrum.setPrecursorType(value);
			} else if("BASE_PEAK".equals(tag)) {
				try {
					massSpectrum.setPrecursorBasePeak(Double.parseDouble(value));
				} catch(RuntimeException e) {
					// can't use then...
				}
			}
		}
	}

	private static void parseMassSpectrometrySubTag(String subtag, VendorLibraryMassSpectrum massSpectrum) {

		String[] split = subtag.split(SUB_TAG_DELIMITER, 2);
		if(split.length == 2) {
			String tag = split[0].trim();
			String value = split[1].trim();
			if("MS_TYPE".equals(tag)) {
				try {
					massSpectrum.setMassSpectrometer(Short.parseShort(value.substring(2)));
				} catch(RuntimeException e) {
					// can't use then...
				}
			} else if("ION_MODE".equals(tag)) {
				if(value.equals("POSITIVE")) {
					massSpectrum.setPolarity(Polarity.POSITIVE);
				} else if(value.substring(2).equals("NEGATIVE")) {
					massSpectrum.setPolarity(Polarity.NEGATIVE);
				}
			} else if("IONIZATION".equals(tag)) {
				massSpectrum.putProperty(IRegularLibraryMassSpectrum.PROPERTY_IONIZATION_MODE, value);
			} else if("COLLISION_ENERGY".equals(tag)) {
				massSpectrum.putProperty(IRegularLibraryMassSpectrum.PROPERTY_COLLISION_ENERGY, value);
			} else if("FRAGMENTATION_METHOD".equals(tag)) {
				massSpectrum.putProperty(IRegularLibraryMassSpectrum.PROPERTY_FRAGMENTATION_METHOD, value);
			}
		}
	}

	private static void parseChromatographySubTag(String subtag, VendorLibraryMassSpectrum massSpectrum) {

		String[] split = subtag.split(SUB_TAG_DELIMITER, 2);
		if(split.length == 2) {
			String tag = split[0].trim();
			String value = split[1].trim();
			if("COLUMN_NAME".equals(tag)) {
				massSpectrum.getChromatography().setColumnName(value);
			} else if("FLOW_GRADIENT".equals(tag)) {
				massSpectrum.getChromatography().setFlowGradient(value);
			} else if("FLOW_RATE".equals(tag)) {
				massSpectrum.getChromatography().setFlowRate(value);
			} else if("RETENTION_TIME".equals(tag)) {
				if(value.endsWith("s")) {
					double retentionTime = parseNumber(value) * IChromatogramOverview.SECOND_CORRELATION_FACTOR;
					massSpectrum.setRetentionTime((int)Math.round(retentionTime));
				} else if(value.endsWith("min")) {
					double retentionTime = parseNumber(value) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
					massSpectrum.setRetentionTime((int)Math.round(retentionTime));
				}
			} else if("SOLVENT".equals(tag)) {
				massSpectrum.getChromatography().setSolvent(value);
			}
		}
	}

	private static double parseNumber(String text) {

		String digits = text.replaceAll("[^0-9.]", "");
		return Double.parseDouble(digits);
	}
}