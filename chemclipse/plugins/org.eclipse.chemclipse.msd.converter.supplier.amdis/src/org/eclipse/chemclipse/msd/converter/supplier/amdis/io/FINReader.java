/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;

public class FINReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final String MARKER_START = "|CA";
	private static final String MARKER_NAME = "NA#";
	private static final String MARKER_SPECTRUM = "LIBRARY SPECTRUM";
	private static final String NAME = "NAME:";
	private static final String UNKNOWN = "Unknown";

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IMassSpectra massSpectra = new MassSpectra();
		massSpectra.setConverterId("");
		massSpectra.setName(file.getName());
		parse(massSpectra, file, monitor);
		return massSpectra;
	}

	private void parse(IMassSpectra massSpectra, File file, IProgressMonitor monitor) throws FileNotFoundException, IOException {

		/*
		 * ...
		 * |CA141-78-6|FOC4H8O2|NA# 60 Ethylacetate @P255
		 * |CO|LI59|FN82|FM78|Fm95|FR98|EI0.00|LT0|LO0|CC0|MO6: 185 276 195 284 59 237|WF-0.2|RC5.9|FP0.0|PU0.2|TF-0.0|CS0.0|AP-2.0|FC-0.0
		 * LIBRARY SPECTRUM
		 * NAME: Library Entry #59
		 * NUM PEAKS: 33
		 * ( 24 1) ( 25 10) ( 26 69) ( 27 180) ( 28 77)
		 * ( 29 334) ( 30 23) ( 31 22) ( 32 1) ( 38 1)
		 * ( 39 1) ( 40 3) ( 41 21) ( 42 192) ( 43 1000)
		 * ( 44 54) ( 45 200) ( 46 4) ( 47 1) ( 55 1)
		 * ( 58 1) ( 59 2) ( 60 20) ( 61 157) ( 62 4)
		 * ( 63 1) ( 70 136) ( 71 6) ( 73 47) ( 74 2)
		 * ( 88 46) ( 89 11) ( 90 1)
		 * EXTRACTED SPECTRUM
		 * ...
		 */
		if(file != null && file.exists()) {
			Charset charset = PreferenceSupplier.getCharsetImportFIN();
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
				/*
				 * Settings and builder.
				 */
				MSLReader mslReader = new MSLReader();
				boolean append = false;
				String component = null;
				StringBuilder builder = new StringBuilder();
				//
				String line;
				while((line = bufferedReader.readLine()) != null) {
					if(line.startsWith(MARKER_START)) {
						String[] values = line.split("\\|");
						if(values.length == 4) {
							String item = values[3].trim();
							String[] items = item.split(" ");
							if(item.startsWith(MARKER_NAME)) {
								component = (items.length >= 3) ? items[2].trim() : UNKNOWN;
							} else {
								component = (items.length >= 4) ? items[3].trim() : UNKNOWN;
							}
						}
					} else {
						if(line.startsWith(MARKER_SPECTRUM)) {
							append = true;
						} else {
							if(line.isEmpty()) {
								if(append) {
									/*
									 * Extract library mass spectrum.
									 */
									String massSpectrumData = builder.toString();
									IVendorLibraryMassSpectrum massSpectrum = mslReader.extractMassSpectrum(massSpectrumData);
									if(massSpectrum != null && massSpectrum.hasIons()) {
										massSpectra.addMassSpectrum(massSpectrum);
									}
									/*
									 * Reset for the next mass spectrum.
									 */
									builder.setLength(0);
								}
								append = false;
							} else {
								if(append) {
									if(line.startsWith(NAME)) {
										builder.append(NAME);
										builder.append(" ");
										builder.append(component);
										builder.append("\n");
									} else {
										builder.append(line);
										builder.append("\n");
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
