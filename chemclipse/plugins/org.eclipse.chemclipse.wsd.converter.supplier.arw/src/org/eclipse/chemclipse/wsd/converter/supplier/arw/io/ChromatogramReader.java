/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.arw.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.wsd.converter.io.AbstractChromatogramWSDReader;
import org.eclipse.chemclipse.wsd.converter.supplier.arw.model.IVendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.arw.model.VendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.arw.model.VendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.arw.model.VendorScanSignal;
import org.eclipse.chemclipse.wsd.converter.supplier.arw.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.interpolation.ScanRasterizer;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramWSDReader {

	private static final double INVALID_WAVELENGTH = -1.0d;
	private static final String DELIMITER = "\t";
	private static final String WAVELENGTH = "Wavelength";
	private static final String TIME = "Time";

	@Override
	public IChromatogramWSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, false, monitor);
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, true, monitor);
	}

	private IChromatogramWSD readChromatogram(File file, boolean overview, IProgressMonitor monitor) throws IOException {

		/*
		 * It's a simple text format:
		 * ---
		 * Wavelength 190.2906 190.8967 191.5017 192.1068 ...
		 * Time
		 * 0 0 0 0 0 ...
		 * 0.0008333334 -0.0016962 -0.0009046 -0.000166 -0.000495 ...
		 * ...
		 */
		IVendorChromatogram chromatogram = new VendorChromatogram();
		chromatogram.setFile(file);
		chromatogram.setConverterId("");
		/*
		 * Parse via a buffered reader.
		 */
		List<Double> wavelengths = new ArrayList<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				if(line.startsWith(WAVELENGTH)) {
					/*
					 * Wavelengths
					 */
					String[] values = line.split(DELIMITER);
					for(int i = 1; i < values.length; i++) {
						wavelengths.add(extractWavelength(values[i]));
					}
				} else if(line.startsWith(TIME)) {
					/*
					 * Time
					 * skip the next line, it seems to be 0.
					 */
					bufferedReader.readLine();
				} else {
					/*
					 * Scans
					 */
					if(wavelengths.size() > 0) {
						String[] values = line.split(DELIMITER);
						if(values.length >= 1) {
							try {
								IScanWSD scan = new VendorScan();
								int retentionTime = (int)(Double.parseDouble(values[0]) * IChromatogram.MINUTE_CORRELATION_FACTOR);
								scan.setRetentionTime(retentionTime);
								/*
								 * Wavelengths
								 */
								int index = 1;
								for(double wavelength : wavelengths) {
									if(wavelength != INVALID_WAVELENGTH) {
										float intensity = extractIntensity(values[index]);
										if(intensity != 0.0f) {
											IScanSignalWSD scanSignal = new VendorScanSignal();
											scanSignal.setWavelength(wavelength);
											scanSignal.setAbundance(intensity);
											scan.addScanSignal(scanSignal);
										}
									}
									index++;
								}
								/*
								 * Add the scan.
								 */
								chromatogram.addScan(scan);
							} catch(NumberFormatException e) {
								/*
								 * Don't add the scan.
								 */
							}
						}
					}
				}
			}
		}
		/*
		 * Set scan delay and interval
		 */
		if(PreferenceSupplier.isNormalizeScans()) {
			int steps = PreferenceSupplier.getNormalizationSteps();
			ScanRasterizer.normalize(chromatogram, steps);
		}
		calculateScanIntervalAndDelay(chromatogram, monitor);
		return chromatogram;
	}

	private void calculateScanIntervalAndDelay(IChromatogramWSD chromatogram, IProgressMonitor monitor) {

		/*
		 * Calculate the scanInterval and scanDelay.
		 */
		int numberOfScans = chromatogram.getNumberOfScans();
		if(numberOfScans > 0) {
			int scanInterval = (chromatogram.getStopRetentionTime() - chromatogram.getStartRetentionTime() + 1) / chromatogram.getNumberOfScans();
			int scanDelay = chromatogram.getStartRetentionTime();
			chromatogram.setScanInterval(scanInterval);
			chromatogram.setScanDelay(scanDelay);
		}
	}

	private float extractIntensity(String value) {

		try {
			return Float.parseFloat(value);
		} catch(NumberFormatException e) {
			return 0.0f;
		}
	}

	private double extractWavelength(String value) {

		try {
			return Double.parseDouble(value);
		} catch(NumberFormatException e) {
			return INVALID_WAVELENGTH;
		}
	}
}
