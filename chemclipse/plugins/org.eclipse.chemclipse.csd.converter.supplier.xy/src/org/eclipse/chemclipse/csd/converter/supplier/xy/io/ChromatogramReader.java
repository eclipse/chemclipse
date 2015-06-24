/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.xy.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.xy.model.IVendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.xy.model.IVendorScan;
import org.eclipse.chemclipse.csd.converter.supplier.xy.model.VendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.xy.model.VendorScan;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramCSDReader {

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, monitor);
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		return readChromatogram(file, monitor);
	}

	private IChromatogramCSD readChromatogram(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IVendorScan scan;
		IVendorChromatogram chromatogram = new VendorChromatogram();
		chromatogram.setFile(file);
		/*
		 * Read the chromatogram
		 */
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line;
		while((line = bufferedReader.readLine()) != null) {
			String[] values = line.split("\t");
			if(values.length == 2) {
				double retentionTimeInMinutes = Double.parseDouble(values[0]);
				int retentionTime = (int)(retentionTimeInMinutes * IChromatogram.MINUTE_CORRELATION_FACTOR);
				float totalSignal = Float.parseFloat(values[1]);
				scan = new VendorScan(retentionTime, totalSignal);
				chromatogram.addScan(scan);
			}
		}
		bufferedReader.close();
		/*
		 * Calculate the scanInterval and scanDelay.
		 */
		int scanInterval = chromatogram.getStopRetentionTime() / chromatogram.getNumberOfScans();
		int scanDelay = chromatogram.getStartRetentionTime() - scanInterval;
		scanDelay = (scanDelay < 0) ? 0 : scanDelay;
		chromatogram.setScanInterval(scanInterval);
		chromatogram.setScanDelay(scanDelay);
		//
		return chromatogram;
	}
}
