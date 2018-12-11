/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.components.peaks.IIncludeBackground;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesMassScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

/**
 * Use this class as a base to show peaks.
 * 
 * @author eselmeister
 */
public abstract class AbstractChromatogramViewPeakUI extends AbstractChromatogramLineSeriesUI implements IIncludeBackground {

	private IChromatogramPeakMSD peak;
	private boolean includeBackground = false;

	public AbstractChromatogramViewPeakUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesMassScale());
	}

	/**
	 * Sets the peak to show.
	 */
	protected void setPeak(IChromatogramPeakMSD peak) {

		this.peak = peak;
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
		series = SeriesConverter.convertPeak(peak, includeBackground, sign);
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
		series = SeriesConverterMSD.convertIncreasingInflectionPoints(peak, includeBackground, sign);
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
		series = SeriesConverterMSD.convertDecreasingInflectionPoints(peak, includeBackground, sign);
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
		series = SeriesConverterMSD.convertPeakPerpendicular(peak, includeBackground, sign);
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
		series = SeriesConverterMSD.convertPeakWidthByInflectionPoints(peak, includeBackground, 0.5f, sign);
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
		series = SeriesConverterMSD.convertPeakWidthByInflectionPoints(peak, includeBackground, 0.0f, sign);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(false);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.DARK_RED);
		if(includeBackground) {
			series = SeriesConverter.convertPeakBackground(peak, sign);
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
