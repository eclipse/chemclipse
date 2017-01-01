/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.converter;

import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.swt.ui.support.PlotSeries;
import org.eclipse.chemclipse.logging.core.Logger;

public class Converter {

	private static final Logger logger = Logger.getLogger(Converter.class);

	private Converter() {
	}

	/**
	 * Returns an {@link PlotSeries} instance extracted from the values given by {@link IChromatogramOverview}.<br/>
	 * If the given chromatogramOverview is null, null will be returned.
	 * 
	 * @param chromatogramOverview
	 * @return PlotSeries
	 */
	public static PlotSeries convertChromatogram(IChromatogramOverview chromatogramOverview) {

		PlotSeries chromatogramSeries = null;
		if(chromatogramOverview != null) {
			/*
			 * Get the signals.
			 */
			try {
				ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogramOverview);
				ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals();
				int scans = signals.size();
				double[] xSeries = new double[scans];
				double[] ySeries = new double[scans];
				int x = 0;
				int y = 0;
				/*
				 * Retrieve the chromatogram x and y signals.
				 */
				for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
					xSeries[x++] = signal.getRetentionTime();
					ySeries[y++] = signal.getTotalSignal();
				}
				chromatogramSeries = new PlotSeries(xSeries, ySeries);
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
			}
		}
		return chromatogramSeries;
	}

	/**
	 * Returns an {@link PlotSeries} instance extracted from the values given by
	 * chromatogram respectively {@link IBaselineModel}.<br/>
	 * If the given baselineModel is null, null will be returned.
	 * 
	 * @param chromatogram
	 * @return {@link PlotSeries}
	 */
	public static PlotSeries convertBaseline(IChromatogram chromatogram) {

		PlotSeries baselineSeries = null;
		if(chromatogram != null) {
			IBaselineModel baselineModel = chromatogram.getBaselineModel();
			try {
				ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
				ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals();
				int scans = signals.size();
				double[] xSeries = new double[scans];
				double[] ySeries = new double[scans];
				int x = 0;
				int y = 0;
				int retentionTime;
				/*
				 * Retrieve the baseline x and y signals.
				 */
				for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
					retentionTime = signal.getRetentionTime();
					xSeries[x++] = retentionTime;
					ySeries[y++] = baselineModel.getBackgroundAbundance(retentionTime);
				}
				baselineSeries = new PlotSeries(xSeries, ySeries);
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
			}
		}
		return baselineSeries;
	}
}
