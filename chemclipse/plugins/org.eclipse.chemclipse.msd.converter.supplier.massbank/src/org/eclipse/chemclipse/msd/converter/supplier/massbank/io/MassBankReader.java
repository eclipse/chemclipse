/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.massbank.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.massbank.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.converter.supplier.massbank.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;

public class MassBankReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(MassBankReader.class);

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IMassSpectra massSpectra = new MassSpectra();
		IScanMSD massSpectrum = readMassSpectrum(file, monitor);
		massSpectra.addMassSpectrum(massSpectrum);
		return massSpectra;
	}

	private IScanMSD readMassSpectrum(File file, IProgressMonitor monitor) throws IOException {

		IVendorLibraryMassSpectrum massSpectrum = new VendorLibraryMassSpectrum();
		Map<String, String> infoMap = massSpectrum.getInfoMap();
		//
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		boolean readPeakData = false;
		//
		while((line = bufferedReader.readLine()) != null) {
			/*
			 * PK$...
			 */
			if(line.startsWith(IVendorLibraryMassSpectrum.PEAK_MARKER)) {
				readPeakData = true;
			}
			/*
			 * Evaluate the rows
			 */
			if(readPeakData) {
				/*
				 * Peak Data
				 */
				if(line.startsWith(IVendorLibraryMassSpectrum.PEAK_MARKER)) {
					// do nothing
				} else if(line.startsWith(IVendorLibraryMassSpectrum.PEAK_MARKER_STOP)) {
					readPeakData = false;
				} else {
					String[] values = line.trim().split(IVendorLibraryMassSpectrum.DELIMITER_MZ);
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
							logger.warn(e);
						}
					}
				}
			} else {
				/*
				 * Other Data
				 */
				String[] values = line.split(IVendorLibraryMassSpectrum.DELIMITER_DESCRIPTION_DATA);
				if(values.length == 2) {
					infoMap.put(values[0].trim(), values[1].trim());
				}
			}
		}
		/*
		 * Set additional information.
		 */
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		libraryInformation.setComments(infoMap.get(IVendorLibraryMassSpectrum.COMMENT));
		libraryInformation.setFormula(infoMap.get(IVendorLibraryMassSpectrum.SMILES)); // TODO Extra Field for SMILES
		libraryInformation.setName(infoMap.get(IVendorLibraryMassSpectrum.NAME));
		//
		bufferedReader.close();
		return massSpectrum;
	}
}
