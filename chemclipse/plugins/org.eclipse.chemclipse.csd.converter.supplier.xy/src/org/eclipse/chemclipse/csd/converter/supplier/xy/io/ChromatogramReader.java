/*******************************************************************************
 * Copyright (c) 2012, 2022 Lablicate GmbH.
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

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
	//
	private static final String TAB = DelimiterFormat.TAB.value();
	private static final String COMMA = ",";
	private static final String SEMICOLON = ";";
	private static final String WHITE_SPACE = " ";

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
		boolean importSuccessful = readChromatogram(file, Charset.defaultCharset().name(), chromatogram);
		if(!importSuccessful) {
			readChromatogram(file, "UTF-16", chromatogram);
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

	private boolean readChromatogram(File file, String charsetName, IVendorChromatogram chromatogram) {

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			try {
				boolean autoDetectFormat = PreferenceSupplier.isAutoDetectFormat();
				DelimiterFormat delimiterFormat = PreferenceSupplier.getDelimiterFormat();
				RetentionTimeFormat retentionTimeFormat = PreferenceSupplier.getRetentionTimeFormat();
				//
				String line;
				while((line = bufferedReader.readLine()) != null) {
					/*
					 * Parse each line.
					 */
					String[] values = null;
					if(autoDetectFormat) {
						if(line.contains(TAB)) {
							values = line.split(TAB);
						} else if(line.contains(COMMA)) {
							values = line.split(COMMA);
						} else if(line.contains(SEMICOLON)) {
							values = line.split(SEMICOLON);
						} else {
							values = line.split(WHITE_SPACE);
						}
					} else {
						values = line.split(delimiterFormat.value());
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
								switch(retentionTimeFormat) {
									case MINUTES:
										double retentionTimeInMinutes = Double.parseDouble(value);
										retentionTime = (int)(retentionTimeInMinutes * IChromatogram.MINUTE_CORRELATION_FACTOR);
										break;
									case SECONDS:
										double retentionTimeInSeconds = Double.parseDouble(value);
										retentionTime = (int)(retentionTimeInSeconds * IChromatogram.SECOND_CORRELATION_FACTOR);
										break;
									default:
										retentionTime = Integer.parseInt(value);
										break;
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
			} catch(Exception e) {
				logger.warn(e);
			}
		} catch(UnsupportedEncodingException e1) {
			logger.warn(e1);
		} catch(FileNotFoundException e1) {
			logger.warn(e1);
		} catch(IOException e1) {
			logger.warn(e1);
		}
		//
		return (chromatogram.getNumberOfScans() > 0) ? true : false;
	}
}