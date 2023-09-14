/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractMassSpectraWriter extends AbstractWriter implements IMassSpectraWriter {

	private static final int MAX_SPECTRA_CHUNK = 65535;

	@Override
	public void write(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		try (FileWriter fileWriter = new FileWriter(file, append)) {
			writeMassSpectrum(fileWriter, massSpectrum, monitor);
			fileWriter.flush();
		}
	}

	@Override
	public void write(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		if(massSpectra.size() > 65535 && PreferenceSupplier.isSplitLibrary()) {
			/*
			 * Split the export file to several files.
			 */
			file.delete();
			List<IMassSpectra> splittedMassSpectra = getSplittedMassSpectra(massSpectra);
			int counter = 1;
			for(IMassSpectra massSpectraChunk : splittedMassSpectra) {
				String filePath = file.getAbsolutePath();
				String fileExtension = filePath.substring(filePath.lastIndexOf("."), filePath.length());
				filePath = filePath.replace(fileExtension, "-" + counter + fileExtension);
				FileWriter fileWriter = new FileWriter(new File(filePath), append);
				writeMassSpectra(fileWriter, massSpectraChunk, monitor);
				fileWriter.close();
				counter++;
			}
		} else {
			/*
			 * <= 65535 mass spectra
			 */
			FileWriter fileWriter = new FileWriter(file, append);
			writeMassSpectra(fileWriter, massSpectra, monitor);
			fileWriter.close();
		}
	}

	private List<IMassSpectra> getSplittedMassSpectra(IMassSpectra massSpectra) {

		IMassSpectra massSpectraChunk;
		List<IMassSpectra> splittedMassSpectra = new ArrayList<>();
		//
		massSpectraChunk = new MassSpectra();
		int counter = 1;
		/*
		 * Split
		 */
		for(int i = 1; i <= massSpectra.size(); i++) {
			IScanMSD massSpectrum = massSpectra.getMassSpectrum(i);
			if(counter <= MAX_SPECTRA_CHUNK) {
				massSpectraChunk.addMassSpectrum(massSpectrum);
				counter++;
			} else {
				splittedMassSpectra.add(massSpectraChunk);
				massSpectraChunk = new MassSpectra();
				massSpectraChunk.addMassSpectrum(massSpectrum);
				counter = 1;
			}
		}
		splittedMassSpectra.add(massSpectraChunk);
		//
		return splittedMassSpectra;
	}

	/**
	 * Writes the mass spectra with the given file writer.
	 * 
	 * @throws IOException
	 */
	private void writeMassSpectra(FileWriter fileWriter, IMassSpectra massSpectra, IProgressMonitor monitor) throws IOException {

		/*
		 * Get all mass spectra, test to null and append them with the given
		 * file writer.
		 */
		for(int i = 1; i <= massSpectra.size(); i++) {
			IScanMSD massSpectrum = massSpectra.getMassSpectrum(i);
			/*
			 * There must be at least one ion.
			 */
			if(massSpectrum != null && !massSpectrum.isEmpty()) {
				writeMassSpectrum(fileWriter, massSpectrum, monitor);
			}
		}
	}
}