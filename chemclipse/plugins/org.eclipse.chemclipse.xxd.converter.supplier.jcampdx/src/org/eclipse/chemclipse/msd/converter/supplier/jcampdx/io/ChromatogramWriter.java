/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;

public class ChromatogramWriter extends AbstractChromatogramMSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		PrintWriter printWriter = new PrintWriter(file);
		//
		writeHeader(chromatogram, printWriter, monitor);
		writeScans(chromatogram, printWriter, monitor);
		//
		printWriter.flush();
		printWriter.close();
	}

	private void writeHeader(IChromatogramMSD chromatogram, PrintWriter printWriter, IProgressMonitor monitor) {

		printWriter.println("##TITLE= ");
		printWriter.println("##JCAMP-DX= ");
		printWriter.println("##SAMPLE_DESCRIPTION= " + chromatogram.getMiscInfo());
		printWriter.println("##DATE= " + chromatogram.getDate().toString());
		printWriter.println("##TIME= " + chromatogram.getDate().toString());
		printWriter.println("##SPECTROMETER_SYSTEM= ");
		printWriter.println("##EXPERIMENT_NAME= ");
		printWriter.println("##INLET= ");
		printWriter.println("##IONIZATION_MODE= EI+");
		printWriter.println("##ELECTRON_ENERGY= 70.000000");
		printWriter.println("##RESOLUTION= ");
		printWriter.println("##ACCELERATING_VOLTAGE= 8000.000000");
		printWriter.println("##CALIBRATION_FILE= ");
		printWriter.println("##REFERENCE_FILE= ");
		printWriter.println("##MASS_RANGE= ");
		printWriter.println("##SCAN_LAW= Exponential");
		printWriter.println("##SCAN_RATE_UNITS= seconds/decade");
		printWriter.println("##SCAN_RATE= " + chromatogram.getScanInterval() / 1000.0d);
		printWriter.println("##SCAN_DELAY_UNITS= seconds");
		printWriter.println("##SCAN_DELAY= " + chromatogram.getScanDelay() / 1000.0d);
		printWriter.println("##XUNITS= Daltons");
		printWriter.println("##DATA_FORMAT= Centroid");
	}

	private void writeScans(IChromatogramMSD chromatogram, PrintWriter printWriter, IProgressMonitor monitor) {

		for(IScan scan : chromatogram.getScans()) {
			monitor.subTask("Export Scan " + scan.getScanNumber());
			if(scan instanceof IVendorMassSpectrum) {
				/*
				 * Export each scan.
				 */
				IVendorMassSpectrum scanMassSpectrum = (IVendorMassSpectrum)scan;
				printWriter.println("##SCAN_NUMBER= " + scanMassSpectrum.getScanNumber());
				printWriter.println("##RETENTION_TIME= " + scanMassSpectrum.getRetentionTime() / 1000.0d); // milliseconds -> seconds
				printWriter.println("##TIC= " + (int)scanMassSpectrum.getTotalSignal());
				printWriter.println("##NPOINTS= " + scanMassSpectrum.getNumberOfIons());
				printWriter.println("##XYDATA= (XY..XY)");
				for(IIon ion : scanMassSpectrum.getIons()) {
					printWriter.println(" " + ion.getIon() + ", " + (int)ion.getAbundance());
				}
			}
		}
	}
}
