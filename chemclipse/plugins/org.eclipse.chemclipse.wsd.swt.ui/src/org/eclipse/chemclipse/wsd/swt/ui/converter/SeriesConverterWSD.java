/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.Series;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

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

	public static IMultipleSeries convertChromatogram(IChromatogramSelectionWSD chromatogramSelection, List<Double> wavelengths, boolean combine, Sign sign) {

		IMultipleSeries wavelengthSeries = new MultipleSeries();
		if(chromatogramSelection != null) {
			/*
			 * Extract the wavelength
			 */
			IChromatogramWSD chromatogram = chromatogramSelection.getChromatogramWSD();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			int scans = stopScan - startScan + 1;
			//
			for(Double wavelength : wavelengths) {
				/*
				 * Series per Wavelength
				 */
				double[] xSeries = new double[scans];
				double[] ySeries = new double[scans];
				int x = 0;
				int y = 0;
				//
				for(int i = startScan; i <= stopScan; i++) {
					IScan scan = chromatogram.getScan(i);
					if(scan instanceof IScanWSD) {
						IScanWSD scanWSD = (IScanWSD)scan;
						List<IScanSignalWSD> scanSignals = scanWSD.getScanSignals();
						//
						double retentionTime = scanWSD.getRetentionTime();
						double abundance = getIntensityWavelength(scanSignals, wavelength);
						xSeries[x++] = retentionTime;
						ySeries[y++] = abundance;
					}
				}
				ISeries series = new Series(xSeries, ySeries, "nm: " + wavelength);
				wavelengthSeries.add(series);
			}
		}
		return wavelengthSeries;
	}

	private static double getIntensityWavelength(List<IScanSignalWSD> scanSignals, double wavelength) {

		for(IScanSignalWSD scanSignal : scanSignals) {
			if(scanSignal.getWavelength() == wavelength) {
				return scanSignal.getAbundance();
			}
		}
		return 0;
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
