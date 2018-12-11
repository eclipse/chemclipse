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
package org.eclipse.chemclipse.csd.swt.ui.components.peak;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

import org.eclipse.chemclipse.csd.swt.ui.components.AbstractPeakLineSeriesUI;
import org.eclipse.chemclipse.csd.swt.ui.converter.SeriesConverterCSD;
import org.eclipse.chemclipse.swt.ui.components.peaks.IIncludeBackground;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;

public abstract class AbstractViewPeakUI extends AbstractPeakLineSeriesUI implements IIncludeBackground {

	private boolean includeBackground = false;

	public AbstractViewPeakUI(Composite parent, int style) {
		super(parent, style);
	}

	// ---------------------------------------------------------------IIncludeBackground
	@Override
	public boolean isIncludeBackground() {

		return includeBackground;
	}

	@Override
	public void setIncludeBackground(boolean includeBackground) {

		this.includeBackground = includeBackground;
	}

	// ---------------------------------------------------------------IIncludeBackground
	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		ISeries series;
		ILineSeries lineSeries;
		Sign sign = Sign.POSITIVE;
		// TODO schauen, ob das mit background klappt??!!
		/*
		 * Convert the peak.
		 */
		series = SeriesConverterCSD.convertPeak(peak, includeBackground, sign);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.RED);
		/*
		 * Increasing tangent
		 */
		series = SeriesConverterCSD.convertIncreasingInflectionPoints(peak, includeBackground, sign);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(false);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.BLACK);
		/*
		 * Decreasing tangent
		 */
		series = SeriesConverterCSD.convertDecreasingInflectionPoints(peak, includeBackground, sign);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(false);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.BLACK);
		// if(!includeBackground) {
		/*
		 * Perpendicular
		 */
		series = SeriesConverterCSD.convertPeakPerpendicular(peak, includeBackground, sign);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(false);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.DARK_RED);
		// }
		/*
		 * Width 50%
		 */
		series = SeriesConverterCSD.convertPeakWidthByInflectionPoints(peak, includeBackground, 0.5f, sign);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(false);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.DARK_RED);
		/*
		 * Width 0%
		 */
		series = SeriesConverterCSD.convertPeakWidthByInflectionPoints(peak, includeBackground, 0.0f, sign);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(false);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.DARK_RED);
		if(includeBackground) {
			series = SeriesConverterCSD.convertPeakBackground(peak, sign);
			addSeries(series);
			lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			lineSeries.enableArea(true);
			lineSeries.setSymbolType(PlotSymbolType.NONE);
			lineSeries.setLineColor(Colors.BLACK);
		}
	}
	// ---------------------------------------------------------------ISeriesSetter
}
