/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.converter;

import java.util.List;

import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.Series;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.logging.core.Logger;

/**
 * Converts chromatograms, mass spectra, peaks, baselines to valid series
 * instances to draw with e.g. SWTChart.
 * 
 * @author eselmeister
 */
public class SeriesConverterWSD {

	public static final Logger logger = Logger.getLogger(SeriesConverterWSD.class);

	/**
	 * Use only static methods.
	 */
	private SeriesConverterWSD() {
	}

	/**
	 * Converts a supplier scan.<br/>
	 * If there is none, null will be returned.
	 * 
	 * @return ISeries
	 */
	public static ISeries convertSupplierScan(IScanWSD supplierScan, Sign sign) {

		ISeries scanSeries = null;
		if(supplierScan != null) {
			/*
			 * Extend the range +- 1 ion value.
			 */
			List<IScanSignalWSD> scanSignals = supplierScan.getScanSignals();
			int size = scanSignals.size();
			/*
			 * Initialize with zero.
			 */
			double[] xSeries = new double[size];
			double[] ySeries = new double[size];
			int x = 0;
			int y = 0;
			double signal;
			/*
			 * Get the abundance for each ion and check if the values should be
			 * negative
			 */
			for(IScanSignalWSD scanSignal : scanSignals) {
				xSeries[x++] = scanSignal.getWavelength();
				signal = scanSignal.getAbundance();
				if(sign == Sign.NEGATIVE) {
					signal *= -1;
				}
				ySeries[y++] = signal;
			}
			scanSeries = new Series(xSeries, ySeries, "Supplier Scan");
		} else {
			/*
			 * If no mass spectrum is available or the mass spectrum is null.
			 */
			scanSeries = getZeroScanSeries();
		}
		return scanSeries;
	}

	private static ISeries getZeroScanSeries() {

		/*
		 * If no profile is available or the if the profile is null.
		 */
		double[] xSeries = {0, 0};
		double[] ySeries = {0, 0};
		return new Series(xSeries, ySeries, "no profile available");
	}
}
