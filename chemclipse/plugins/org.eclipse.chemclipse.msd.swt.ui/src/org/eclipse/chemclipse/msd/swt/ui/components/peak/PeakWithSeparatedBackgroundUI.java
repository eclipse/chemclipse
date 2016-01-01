/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Shows the peak with the background.
 * 
 * @author eselmeister
 */
public class PeakWithSeparatedBackgroundUI extends AbstractViewPeakUI {

	public PeakWithSeparatedBackgroundUI(Composite parent, int style, IAxisTitles axisTitles) {
		super(parent, style, axisTitles);
	}

	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		ISeries series;
		ILineSeries lineSeries;
		series = SeriesConverterMSD.convertPeakBackground(peak, Sign.POSITIVE);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.BLACK);
		/*
		 * Convert the peak + the backgrount to sit above the background line.
		 */
		series = SeriesConverterMSD.convertPeak(peak, true, Sign.POSITIVE);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.RED);
	}
	// ---------------------------------------------------------------ISeriesSetter
}
