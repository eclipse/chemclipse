/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.arw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.arw.io.support.ChromatogramArrayReader;
import org.eclipse.chemclipse.csd.converter.supplier.arw.io.support.IChromatogramArrayReader;
import org.eclipse.chemclipse.csd.converter.supplier.arw.model.IVendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.arw.model.IVendorScan;
import org.eclipse.chemclipse.csd.converter.supplier.arw.model.VendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.arw.model.VendorScan;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramCSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);
	private static final Pattern scanPattern = Pattern.compile("(.*)(\t)(.*)");

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, monitor);
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, monitor);
	}

	private IChromatogramCSD readChromatogram(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		chromatogram.setFile(file);
		chromatogram.setConverterId("");
		//
		IChromatogramArrayReader in = new ChromatogramArrayReader(file);
		String input = in.readBytesAsString(in.getLength());
		Matcher matcher = scanPattern.matcher(input);
		while(matcher.find()) {
			String retentionTimeInMinutes = matcher.group(1).replace(",", ".");
			String abundance = matcher.group(3).replace(",", ".");
			try {
				int retentionTime = (int)(Double.valueOf(retentionTimeInMinutes) * IChromatogram.MINUTE_CORRELATION_FACTOR);
				float totalSignal = Float.valueOf(abundance);
				IVendorScan scan = new VendorScan();
				scan.setRetentionTime(retentionTime);
				scan.setTotalSignal(totalSignal);
				chromatogram.addScan(scan);
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
		/*
		 * Set scan delay and interval
		 */
		calculateScanIntervalAndDelay(chromatogram, monitor);
		//
		return chromatogram;
	}

	private void calculateScanIntervalAndDelay(IChromatogramCSD chromatogram, IProgressMonitor monitor) {

		/*
		 * Calculate the scanInterval and scanDelay.
		 */
		int scanInterval = chromatogram.getStopRetentionTime() / chromatogram.getNumberOfScans();
		int scanDelay = chromatogram.getStartRetentionTime() - scanInterval;
		scanDelay = (scanDelay < 0) ? 0 : scanDelay;
		chromatogram.setScanInterval(scanInterval);
		chromatogram.setScanDelay(scanDelay);
	}
}
