/*******************************************************************************
 * Copyright (c) 2016, 2018 Matthias Mailänder.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.io.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Writes a simple peaklist into a text file.
 */
public class MassSpectrumWriter implements IMassSpectraWriter {

	private static final Logger logger = Logger.getLogger(MassSpectrumWriter.class);
	/*
	 * Don't modify this instance.
	 */
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	@Override
	public void writeMassSpectrum(FileWriter fileWriter, IScanMSD massSpectrum, IProgressMonitor monitor) throws IOException {

		MassSpectra massSpectra = new MassSpectra();
		massSpectra.addMassSpectrum(massSpectrum);
		writeMassSpectrumToCsv(massSpectra, fileWriter);
	}

	@Override
	public void write(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		MassSpectra massSpectra = new MassSpectra();
		massSpectra.addMassSpectrum(massSpectrum);
		writeMassSpectrumToCsv(massSpectra, fileWriter);
	}

	@Override
	public void write(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		writeMassSpectrumToCsv(massSpectra, fileWriter);
	}

	private void writeMassSpectrumToCsv(IMassSpectra massSpectra, FileWriter fileWriter) throws IOException {

		if(massSpectra != null) {
			CSVPrinter csvFilePrinter = null;
			try {
				csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.EXCEL);
				//
				for(IScanMSD massSpectrum : massSpectra.getList()) {
					/*
					 * Try to make a deep copy and normalize.
					 */
					IScanMSD massSpectrumExport;
					try {
						massSpectrumExport = massSpectrum.makeDeepCopy();
						massSpectrumExport.normalize(1000.0f);
					} catch(CloneNotSupportedException e) {
						logger.warn(e);
						massSpectrumExport = massSpectrum;
					}
					//
					csvFilePrinter.printRecord("m/z", "intensity");
					List<IIon> ions = massSpectrum.getIons();
					for(IIon ion : ions) {
						String mz = decimalFormat.format(ion.getIon());
						String intensity = decimalFormat.format(ion.getAbundance());
						csvFilePrinter.printRecord(mz, intensity);
					}
					csvFilePrinter.printRecord("");
				}
			} finally {
				if(csvFilePrinter != null) {
					csvFilePrinter.close();
				}
			}
		}
	}
}
