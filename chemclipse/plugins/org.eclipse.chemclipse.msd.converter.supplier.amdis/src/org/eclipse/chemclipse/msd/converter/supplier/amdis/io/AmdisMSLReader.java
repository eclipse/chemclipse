/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;

public class AmdisMSLReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(AmdisMSLReader.class);
	private static final int correctionFactor = 1000 * 60; // 60 seconds * 1000
															// milliseconds
	/**
	 * Pre-compile all patterns to be a little bit faster.
	 */
	private static final Pattern namePattern = Pattern.compile("(NAME:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern commentsPattern = Pattern.compile("(COMMENTS:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern casNumberPattern = Pattern.compile("(CASNO:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern retentionTimePattern = Pattern.compile("(RT:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern retentionIndexPattern = Pattern.compile("(RI:)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ionPattern = Pattern.compile("(\\d+)(\\s+)(\\d+)");

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		List<String> massSpectraData = getMassSpectraData(file);
		IMassSpectra massSpectra = extractMassSpectra(massSpectraData);
		massSpectra.setConverterId("org.eclipse.chemclipse.msd.converter.supplier.amdis.massspectrum.msl");
		return massSpectra;
	}

	// ---------------------------------------------------private methods
	/**
	 * Returns a list of mass spectral data.
	 * 
	 * @throws IOException
	 */
	private List<String> getMassSpectraData(File file) throws IOException {

		Charset charSet = Charset.forName("US-ASCII");
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, charSet);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		List<String> massSpectraData = new ArrayList<String>();
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
				builder.append("\r\n");
			}
		}
		/*
		 * Don't forget to add the last mass spectrum.
		 */
		addMassSpectrumData(builder, massSpectraData);
		/*
		 * Close the streams.
		 */
		bufferedReader.close();
		inputStreamReader.close();
		bufferedInputStream.close();
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
	private IMassSpectra extractMassSpectra(List<String> massSpectraData) {

		IMassSpectra massSpectra = new MassSpectra();
		String referenceIdentifierMarker = PreferenceSupplier.getReferenceIdentifierMarker();
		String referenceIdentifierPrefix = PreferenceSupplier.getReferenceIdentifierPrefix();
		/*
		 * Iterates through the saved mass spectrum text data and converts it to
		 * a mass spectrum.
		 */
		for(String massSpectrumData : massSpectraData) {
			addMassSpectrum(massSpectra, massSpectrumData, referenceIdentifierMarker, referenceIdentifierPrefix);
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

		IVendorLibraryMassSpectrum massSpectrum = new VendorLibraryMassSpectrum();
		/*
		 * Extract name and reference identifier.
		 */
		String name = extractContentAsString(massSpectrumData, namePattern);
		extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		String comments = extractContentAsString(massSpectrumData, commentsPattern);
		massSpectrum.getLibraryInformation().setComments(comments);
		String casNumber = extractContentAsString(massSpectrumData, casNumberPattern);
		massSpectrum.getLibraryInformation().setCasNumber(casNumber);
		int retentionTime = extractContentAsInt(massSpectrumData, retentionTimePattern);
		massSpectrum.setRetentionTime(retentionTime);
		float retentionIndex = extractContentAsFloat(massSpectrumData, retentionIndexPattern);
		massSpectrum.setRetentionIndex(retentionIndex);
		/*
		 * Extracts all ions and stored them.
		 */
		extractIons(massSpectrum, massSpectrumData);
		/*
		 * Store the mass spectrum in mass spectra if there is at least 1 mass
		 * fragment.
		 */
		if(massSpectrum.getNumberOfIons() > 0) {
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

		IIon amdisIon = null;
		double ion;
		float abundance;
		Matcher ions = ionPattern.matcher(massSpectrumData);
		while(ions.find()) {
			try {
				/*
				 * Get the ion and abundance values.
				 */
				ion = Double.parseDouble(ions.group(1));
				abundance = Float.parseFloat(ions.group(3));
				/*
				 * Create the ion and store it in mass spectrum.
				 */
				amdisIon = new Ion(ion, abundance);
				massSpectrum.addIon(amdisIon);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
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
	private String extractContentAsString(String massSpectrumData, Pattern pattern) {

		String content = "";
		Matcher matcher = pattern.matcher(massSpectrumData);
		if(matcher.find()) {
			content = matcher.group(2).trim();
		}
		return content;
	}

	/**
	 * Extracts the content from the given mass spectrum string defined by the
	 * given pattern.
	 * 
	 * @param massSpectrumData
	 * @return int
	 */
	private int extractContentAsInt(String massSpectrumData, Pattern pattern) {

		int content = 0;
		try {
			Matcher matcher = pattern.matcher(massSpectrumData);
			if(matcher.find()) {
				content = (int)(Float.parseFloat(matcher.group(2).trim()) * correctionFactor);
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		return content;
	}

	/**
	 * Extracts the content from the given mass spectrum string defined by the
	 * given pattern.
	 * 
	 * @param massSpectrumData
	 * @return String
	 */
	private float extractContentAsFloat(String massSpectrumData, Pattern pattern) {

		float content = 0.0f;
		try {
			Matcher matcher = pattern.matcher(massSpectrumData);
			if(matcher.find()) {
				content = (float)(Float.parseFloat(matcher.group(2)));
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		return content;
	}
	// ---------------------------------------------------private methods
}
