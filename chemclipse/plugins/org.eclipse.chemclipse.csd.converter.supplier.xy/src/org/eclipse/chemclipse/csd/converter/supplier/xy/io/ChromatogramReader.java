/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.xy.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.xy.model.IVendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.xy.model.IVendorScan;
import org.eclipse.chemclipse.csd.converter.supplier.xy.model.VendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.xy.model.VendorScan;
import org.eclipse.chemclipse.csd.converter.supplier.xy.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramCSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, monitor);
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, monitor);
	}

	private IChromatogramCSD readChromatogram(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		chromatogram.setFile(file);
		/*
		 * Read the chromatogram
		 */
		boolean importSuccessful = readChromatogram(new BufferedReader(new FileReader(file)), chromatogram);
		if(!importSuccessful) {
			readChromatogram(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-16")), chromatogram);
		}
		/*
		 * Calculate the scanInterval and scanDelay.
		 */
		int scanInterval = ((chromatogram.getStopRetentionTime() - chromatogram.getStartRetentionTime()) / chromatogram.getNumberOfScans()) + 1;
		int scanDelay = chromatogram.getStartRetentionTime() - scanInterval;
		scanDelay = (scanDelay < 0) ? 0 : scanDelay;
		chromatogram.setScanInterval(scanInterval);
		chromatogram.setScanDelay(scanDelay);
		//
		return chromatogram;
	}

	private boolean readChromatogram(BufferedReader bufferedReader, IVendorChromatogram chromatogram) {

		if(bufferedReader != null) {
			try {
				boolean autoDetectFormat = PreferenceSupplier.isAutoDetectFormat();
				String delimiterFormat = PreferenceSupplier.getDelimiterFormat();
				String retentionTimeFormat = PreferenceSupplier.getRetentionTimeFormat();
				//
				String line;
				while((line = bufferedReader.readLine()) != null) {
					/*
					 * Parse each line.
					 */
					String[] values = null;
					if(autoDetectFormat) {
						if(line.contains(PreferenceSupplier.TAB)) {
							values = line.split(PreferenceSupplier.TAB);
						} else if(line.contains(PreferenceSupplier.COMMA)) {
							values = line.split(PreferenceSupplier.COMMA);
						} else if(line.contains(PreferenceSupplier.SEMICOLON)) {
							values = line.split(PreferenceSupplier.SEMICOLON);
						} else {
							values = line.split(PreferenceSupplier.WHITE_SPACE);
						}
					} else {
						values = line.split(delimiterFormat);
					}
					//
					if(values != null && values.length >= 2) {
						try {
							String value = values[0].trim();
							int retentionTime = -1;
							//
							if(autoDetectFormat) {
								if(value.contains(".")) {
									/*
									 * Minutes
									 */
									double retentionTimeInMinutes = Double.parseDouble(value);
									retentionTime = (int)(retentionTimeInMinutes * IChromatogram.MINUTE_CORRELATION_FACTOR);
								} else {
									/*
									 * Milliseconds
									 */
									retentionTime = Integer.parseInt(value);
								}
							} else {
								if(retentionTimeFormat.equals(PreferenceSupplier.MINUTES)) {
									double retentionTimeInMinutes = Double.parseDouble(value);
									retentionTime = (int)(retentionTimeInMinutes * IChromatogram.MINUTE_CORRELATION_FACTOR);
								} else if(retentionTimeFormat.equals(PreferenceSupplier.SECONDS)) {
									double retentionTimeInSeconds = Double.parseDouble(value);
									retentionTime = (int)(retentionTimeInSeconds * IChromatogram.SECOND_CORRELATION_FACTOR);
								} else {
									retentionTime = Integer.parseInt(value);
								}
							}
							/*
							 * Add the scan
							 */
							if(retentionTime >= 0) {
								float totalSignal = Float.parseFloat(values[1]);
								IVendorScan scan = new VendorScan(retentionTime, totalSignal);
								chromatogram.addScan(scan);
							}
						} catch(NumberFormatException e) {
							// logger.warn(e);
						}
					}
				}
			} catch(IOException e) {
				logger.warn(e);
			} finally {
				try {
					bufferedReader.close();
				} catch(IOException e) {
					logger.warn(e);
				}
			}
		}
		//
		return (chromatogram.getNumberOfScans() > 0) ? true : false;
	}
}
