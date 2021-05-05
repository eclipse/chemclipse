/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.sirius.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.sirius.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;

public class SiriusReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final String COMPOUND = ">compound";
	private static final String FORMULA = ">formula";
	private static final String PARENTMASS = ">parentmass";
	private static final String IONIZATION = ">ionization";
	private static final String COLLISION = ">collision";
	private static final String MS1PEAKS = ">ms1peaks";
	private static final String INCHI = "#inchi";
	private static final String INCHIKEY = "#inchikey";
	private static final String CASNUMBER = "#CAS_Number";
	private static final String PUBMED = "#Pubmed_ID";
	private static final String SMILES = "#Smiles";
	private static final String PRECURSORMZ = "#Precursor_MZ";
	private static final String EXACTMASS = "#ExactMass";
	private static final String MS1 = ">ms1";
	private static final Logger logger = Logger.getLogger(SiriusReader.class);

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws IOException {

		if(file.getName().toLowerCase().endsWith(".zip")) {
			if(monitor != null) {
				monitor.beginTask("Reading mass spectras from " + file.getName(), IProgressMonitor.UNKNOWN);
			}
			IMassSpectra massSpectra = new MassSpectra();
			massSpectra.setConverterId("SIRIUS");
			try (ZipFile zipFile = new ZipFile(file)) {
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while(entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if(entry.isDirectory()) {
						continue;
					}
					if(entry.getName().toLowerCase().endsWith(".ms")) {
						IScanMSD spectrum = readMassSpectrum(zipFile.getInputStream(entry), null);
						if(spectrum.getNumberOfIons() > 0) {
							massSpectra.addMassSpectrum(spectrum);
						}
					}
				}
			}
			return massSpectra;
		} else {
			IMassSpectra massSpectra = new MassSpectra();
			try (FileInputStream inputStream = new FileInputStream(file)) {
				IScanMSD spectrum = readMassSpectrum(inputStream, monitor);
				if(spectrum.getNumberOfIons() > 0) {
					massSpectra.addMassSpectrum(spectrum);
				}
			}
			return massSpectra;
		}
	}

	public static IScanMSD readMassSpectrum(InputStream stream, IProgressMonitor monitor) throws IOException {

		VendorLibraryMassSpectrum massSpectrum = new VendorLibraryMassSpectrum();
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		//
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		String line;
		while((line = bufferedReader.readLine()) != null) {
			if(line.startsWith(COLLISION) || line.startsWith(MS1)) {
				line = parsePeakList(bufferedReader, massSpectrum);
				if(line == null) {
					continue;
				}
			}
			if(line.startsWith(COMPOUND)) {
				String value = line.split(COMPOUND)[1].trim();
				libraryInformation.setName(value);
			}
			if(line.startsWith(FORMULA)) {
				String value = line.split(FORMULA)[1].trim();
				libraryInformation.setFormula(value);
			}
			if(line.startsWith(PARENTMASS)) {
				String value = line.split(PARENTMASS)[1].trim();
				Double parentMass = Double.parseDouble(value);
				massSpectrum.setPrecursorBasepeak(parentMass);
			}
			if(line.startsWith(PRECURSORMZ)) {
				String value = line.split(PRECURSORMZ)[1].trim();
				Double precursor = Double.parseDouble(value);
				massSpectrum.setPrecursorBasepeak(precursor);
			}
			if(line.startsWith(INCHI)) {
				String value = line.split(INCHI)[1].trim();
				if(!value.equals("N/A"))
					libraryInformation.setInChI(value);
			}
			if(line.startsWith(CASNUMBER)) {
				String value = line.split(CASNUMBER)[1].trim();
				if(!value.equals("N/A"))
					libraryInformation.setCasNumber(value);
			}
			if(line.startsWith(SMILES)) {
				String value = line.split(SMILES)[1].trim();
				if(!value.equals("N/A"))
					libraryInformation.setSmiles(value);
			}
			if(line.startsWith(EXACTMASS)) {
				String value = line.split(EXACTMASS)[1].trim();
				Double mass = Double.parseDouble(value);
				libraryInformation.setMolWeight(mass);
			}
		}
		bufferedReader.close();
		return massSpectrum;
	}

	private static String parsePeakList(BufferedReader bufferedReader, VendorLibraryMassSpectrum massSpectrum) throws IOException {

		String line;
		while((line = bufferedReader.readLine()) != null) {
			if(!line.isEmpty() && Character.isDigit(line.charAt(0))) {
				String[] values = line.trim().split(" ");
				if(values.length == 2) {
					try {
						double mz = Double.parseDouble(values[0]);
						float abundance = Float.parseFloat(values[1]);
						IIon ion = new Ion(mz, abundance);
						massSpectrum.addIon(ion);
					} catch(Exception e) {
						logger.warn("Parsing peak list failed: " + e);
						continue;
					}
				}
			} else {
				break;
			}
		}
		return line;
	}
}
