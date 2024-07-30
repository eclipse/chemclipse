/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnPackaging;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.identifier.ColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.misc.CompoundInformation;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.misc.ConverterCID;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.misc.ConverterMOL;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.runtime.IProgressMonitor;

public class MSLReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(MSLReader.class);
	//
	private static final String CONVERTER_ID = "org.eclipse.chemclipse.msd.converter.supplier.amdis.massspectrum.msl";
	/**
	 * Pre-compile all patterns to be a little bit faster.
	 */
	private static final Pattern NAME = Pattern.compile("(NAME:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern COMMENTS = Pattern.compile("(COMMENT:|COMMENTS:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern CAS = Pattern.compile("(CAS(NO|#)?:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern SYNONYM = Pattern.compile("(Synon:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern DB_NAME = Pattern.compile("(DB(NO|#)?:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern CONTRIBUTOR = Pattern.compile("(CONTRIBUTOR:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern REFERENCE_IDENTIFIER = Pattern.compile("(REFID:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern SMILES = Pattern.compile("(SMILES:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern INCHI = Pattern.compile("(INCHI:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern INCHIKEY = Pattern.compile("(INCHIKEY:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern MW = Pattern.compile("(MW:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern EXACT_MASS = Pattern.compile("(EXACTMASS:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern FORMULA = Pattern.compile("(FORMULA:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern RETENTION_TIME = Pattern.compile("(RT:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern RELATIVE_RETENTION_TIME = Pattern.compile("(RRT:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern RETENTION_INDEX = Pattern.compile("(RI:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_INDEX = Pattern.compile("(COLUMNINDEX:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern DATA = Pattern.compile("(.*)(Num Peaks:)(\\s*)(\\d*)(.*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern SOURCE = Pattern.compile("(SOURCE:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern IONS = Pattern.compile("([+]?\\d+\\.?\\d*)(\\s+)([+-]?\\d+\\.?\\d*([eE][+-]?\\d+)?)"); // "(\\d+)(\\s+)(\\d+)" or "(\\d+)(\\s+)([+-]?\\d+\\.?\\d*([eE][+-]?\\d+)?)"
	//
	private static final String RETENTION_INDICES_DELIMITER = ", ";
	private static final String LINE_DELIMITER = "\r\n";

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws IOException {

		List<String> massSpectraData = getMassSpectraData(file);
		//
		IMassSpectra massSpectra = extractMassSpectra(massSpectraData, monitor);
		massSpectra.setConverterId(CONVERTER_ID);
		massSpectra.setName(file.getName());
		/*
		 * Compound Information (*.CID)
		 */
		if(PreferenceSupplier.isParseCompoundInformation()) {
			File fileCID = ConverterCID.getFileCID(file);
			if(fileCID != null) {
				List<CompoundInformation> compoundList = ConverterCID.convert(fileCID);
				ConverterCID.transfer(compoundList, massSpectra);
			}
		}
		/*
		 * MOL Information (*.MOL)
		 */
		if(PreferenceSupplier.isParseMolInformation()) {
			File fileMOL = ConverterMOL.getFileMOL(file);
			if(fileMOL != null) {
				Map<String, String> moleculeStructureMap = ConverterMOL.convert(fileMOL);
				ConverterMOL.transfer(moleculeStructureMap, massSpectra);
			}
		}
		//
		return massSpectra;
	}

	/**
	 * Extracts the mass spectrum from the given text.
	 * 
	 * @param massSpectrumData
	 * @return {@link IVendorLibraryMassSpectrum}
	 */
	protected IVendorLibraryMassSpectrum extractMassSpectrum(String massSpectrumData) {

		return extractMassSpectrum(massSpectrumData, "", "");
	}

	/**
	 * Extracts the mass spectrum from the given text.
	 * 
	 * @param massSpectrumData
	 * @param referenceIdentifierMarker
	 * @param referenceIdentifierPrefix
	 * @return {@link IVendorLibraryMassSpectrum}
	 */
	protected IVendorLibraryMassSpectrum extractMassSpectrum(String massSpectrumData, String referenceIdentifierMarker, String referenceIdentifierPrefix) {

		IVendorLibraryMassSpectrum massSpectrum = new VendorLibraryMassSpectrum();
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		/*
		 * Extract name and reference identifier.
		 * Additionally, add the reference identifier if it is stored as a pattern.
		 */
		extractNameAndReferenceIdentifier(massSpectrum, extractContentAsString(massSpectrumData, NAME, 2), referenceIdentifierMarker, referenceIdentifierPrefix);
		setSynonyms(extractContentAsStringList(massSpectrumData, SYNONYM, 2), libraryInformation);
		setCasNumbers(extractContentAsStringList(massSpectrumData, CAS, 3), libraryInformation);
		libraryInformation.setComments(extractContentAsString(massSpectrumData, COMMENTS, 2));
		libraryInformation.setReferenceIdentifier(extractContentAsString(massSpectrumData, REFERENCE_IDENTIFIER, 2) + libraryInformation.getReferenceIdentifier());
		libraryInformation.setFormula(extractContentAsString(massSpectrumData, FORMULA, 2));
		libraryInformation.setInChI(extractContentAsString(massSpectrumData, INCHI, 2));
		libraryInformation.setInChIKey(extractContentAsString(massSpectrumData, INCHIKEY, 2));
		libraryInformation.setSmiles(extractContentAsString(massSpectrumData, SMILES, 2));
		libraryInformation.setMolWeight(extractContentAsDouble(massSpectrumData, MW, 2));
		libraryInformation.setExactMass(extractContentAsDouble(massSpectrumData, EXACT_MASS, 2));
		libraryInformation.setDatabase(extractContentAsString(massSpectrumData, DB_NAME, 3));
		libraryInformation.setContributor(extractContentAsString(massSpectrumData, CONTRIBUTOR, 2));
		massSpectrum.setRetentionTime(extractRetentionTime(massSpectrumData, RETENTION_TIME, 2));
		massSpectrum.setRelativeRetentionTime(extractRetentionTime(massSpectrumData, RELATIVE_RETENTION_TIME, 2));
		extractRetentionIndices(massSpectrum, extractContentAsString(massSpectrumData, RETENTION_INDEX, 2), RETENTION_INDICES_DELIMITER);
		setColumnIndices(extractContentAsStringList(massSpectrumData, COLUMN_INDEX, 2), libraryInformation);
		massSpectrum.setSource(extractContentAsString(massSpectrumData, SOURCE, 2));
		/*
		 * Extracts all ions and stored them.
		 */
		extractIons(massSpectrum, massSpectrumData);
		//
		return massSpectrum;
	}

	private void setCasNumbers(List<String> casNumbers, ILibraryInformation libraryInformation) {

		for(String casNumber : casNumbers) {
			libraryInformation.addCasNumber(casNumber);
		}
	}

	private void setSynonyms(List<String> synonyms, ILibraryInformation libraryInformation) {

		for(String synonym : synonyms) {
			libraryInformation.getSynonyms().add(synonym);
		}
	}

	private void setColumnIndices(List<String> columnIndices, ILibraryInformation libraryInformation) {

		ValueParserSupport valueParserSupport = new ValueParserSupport();
		for(String columnIndex : columnIndices) {
			String[] values = columnIndex.split(AbstractWriter.TAB);
			if(values.length >= 3) {
				float retentionIndex = valueParserSupport.parseFloat(values, 0, 0.0f);
				String name = valueParserSupport.parseString(values, 1, "");
				if(retentionIndex > 0.0f && !name.isEmpty()) {
					/*
					 * Separation Column
					 */
					SeparationColumnType separationColumnType = getSeparationColumnType(valueParserSupport.parseString(values, 2, ""));
					SeparationColumnPackaging separationColumnPackaging = getSeparationColumnPackaging(valueParserSupport.parseString(values, 3, ""));
					String calculationType = valueParserSupport.parseString(values, 4, "");
					String length = valueParserSupport.parseString(values, 5, "");
					String diameter = valueParserSupport.parseString(values, 6, "");
					String phase = valueParserSupport.parseString(values, 7, "");
					String thickness = valueParserSupport.parseString(values, 8, "");
					//
					ISeparationColumn separationColumn = SeparationColumnFactory.getSeparationColumn(name, length, diameter, phase);
					separationColumn.setSeparationColumnType(separationColumnType);
					separationColumn.setSeparationColumnPackaging(separationColumnPackaging);
					separationColumn.setCalculationType(calculationType);
					separationColumn.setThickness(thickness);
					IColumnIndexMarker columnIndexMarker = new ColumnIndexMarker(separationColumn, retentionIndex);
					libraryInformation.add(columnIndexMarker);
				}
			}
		}
	}

	private SeparationColumnType getSeparationColumnType(String value) {

		try {
			return SeparationColumnType.valueOf(value);
		} catch(Exception e) {
			for(SeparationColumnType type : SeparationColumnType.values()) {
				if(type.label().equals(value)) {
					return type;
				}
			}
			return SeparationColumnType.DEFAULT;
		}
	}

	private SeparationColumnPackaging getSeparationColumnPackaging(String value) {

		try {
			return SeparationColumnPackaging.valueOf(value);
		} catch(Exception e) {
			for(SeparationColumnPackaging packaging : SeparationColumnPackaging.values()) {
				if(packaging.label().equals(value)) {
					return packaging;
				}
			}
			return SeparationColumnPackaging.CAPILLARY;
		}
	}

	/**
	 * Returns a list of mass spectra data.
	 * 
	 * @throws IOException
	 */
	private List<String> getMassSpectraData(File file) throws IOException {

		Charset charset = PreferenceSupplier.getCharsetImportMSL();
		List<String> massSpectraData = new ArrayList<>();
		//
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
			StringBuilder builder = new StringBuilder();
			String line;
			while((line = bufferedReader.readLine()) != null) {
				/*
				 * The mass spectra are divided by empty lines. If the builder has
				 * at least 1 char, then add a new potential mass spectrum to the
				 * mass spectra data list. Don't forget to build a new
				 * StringBuilder. In all other cases append the found lines to the
				 * StringBuilder.
				 */
				if(line.length() == 0) {
					addMassSpectrumData(builder, massSpectraData);
					builder = new StringBuilder();
				} else {
					builder.append(line);
					builder.append(LINE_DELIMITER);
				}
			}
			/*
			 * Don't forget to add the last mass spectrum.
			 */
			addMassSpectrumData(builder, massSpectraData);
		}
		//
		return massSpectraData;
	}

	/**
	 * Adds the content from the StringBuilder to the mass spectra data list, if
	 * the length is > 0.
	 * 
	 * @param builder
	 * @param massSpectraData
	 */
	private void addMassSpectrumData(StringBuilder builder, List<String> massSpectraData) {

		String massSpectrumData;
		if(builder.length() > 0) {
			massSpectrumData = builder.toString();
			massSpectraData.add(massSpectrumData);
		}
	}

	/**
	 * Returns a mass spectra object or null, if something has gone wrong.
	 * 
	 * @param massSpectraData
	 * @return IMassSpectra
	 */
	private IMassSpectra extractMassSpectra(List<String> massSpectraData, IProgressMonitor monitor) {

		IMassSpectra massSpectra = new MassSpectra();
		String referenceIdentifierMarker = org.eclipse.chemclipse.msd.converter.preferences.PreferenceSupplier.getReferenceIdentifierMarker();
		String referenceIdentifierPrefix = org.eclipse.chemclipse.msd.converter.preferences.PreferenceSupplier.getReferenceIdentifierPrefix();
		/*
		 * Iterates through the saved mass spectrum text data and converts it to
		 * a mass spectrum.
		 */
		monitor.beginTask("Extract mass spectra", massSpectraData.size());
		for(String massSpectrumData : massSpectraData) {
			if(monitor.isCanceled()) {
				return massSpectra;
			}
			addMassSpectrum(massSpectra, massSpectrumData, referenceIdentifierMarker, referenceIdentifierPrefix);
			monitor.worked(1);
		}
		return massSpectra;
	}

	/**
	 * Detect a mass spectrum and add it to the given mass spectra.
	 * 
	 * @param massSpectra
	 * @param massSpectrumData
	 */
	private void addMassSpectrum(IMassSpectra massSpectra, String massSpectrumData, String referenceIdentifierMarker, String referenceIdentifierPrefix) {

		/*
		 * Store the mass spectrum in mass spectra if there is at least 1 mass
		 * fragment.
		 */
		IVendorLibraryMassSpectrum massSpectrum = extractMassSpectrum(massSpectrumData, referenceIdentifierMarker, referenceIdentifierPrefix);
		if(!massSpectrum.isEmpty()) {
			massSpectra.addMassSpectrum(massSpectrum);
		}
	}

	/**
	 * Extracts all ion from the given mass spectrum data and stores
	 * them in the given mass spectrum.
	 * 
	 * @param massSpectrum
	 * @param massSpectrumData
	 */
	private void extractIons(IVendorLibraryMassSpectrum massSpectrum, String massSpectrumData) {

		String ionData = "";
		Matcher data = DATA.matcher(massSpectrumData);
		data.find();
		if(data.matches()) {
			ionData = data.group(5);
		}
		//
		IIon amdisIon = null;
		double ion;
		float abundance;
		Matcher ions = IONS.matcher(ionData);
		while(ions.find()) {
			/*
			 * Get the ion and abundance values.
			 */
			ion = Double.parseDouble(ions.group(1));
			abundance = Float.parseFloat(ions.group(3));
			/*
			 * Create the ion and store it in mass spectrum.
			 */
			if(abundance > 0) {
				amdisIon = new Ion(ion, abundance);
				massSpectrum.addIon(amdisIon);
			}
		}
	}

	/**
	 * Extracts the content from the given mass spectrum string defined by the
	 * given pattern.
	 * 
	 * @param massSpectrumData
	 * @return String
	 */
	private String extractContentAsString(String massSpectrumData, Pattern pattern, int group) {

		String content = "";
		Matcher matcher = pattern.matcher(massSpectrumData);
		if(matcher.find()) {
			content = matcher.group(group).trim();
		}
		return content.replace("\0", " ");
	}

	private List<String> extractContentAsStringList(String massSpectrumData, Pattern pattern, int group) {

		List<String> contentList = new ArrayList<>();
		Matcher matcher = pattern.matcher(massSpectrumData);
		while(matcher.find()) {
			contentList.add(matcher.group(group).trim().replace("\0", " "));
		}
		//
		return contentList;
	}

	private int extractRetentionTime(String massSpectrumData, Pattern pattern, int group) {

		return (int)(extractContentAsDouble(massSpectrumData, pattern, group) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
	}

	private double extractContentAsDouble(String massSpectrumData, Pattern pattern, int group) {

		double content = 0;
		try {
			Matcher matcher = pattern.matcher(massSpectrumData);
			if(matcher.find()) {
				content = Double.parseDouble(matcher.group(group).trim());
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		//
		return content;
	}
}
