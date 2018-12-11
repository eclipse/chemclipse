/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.swt;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.swt.ui.converter.SeriesConverterCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.components.peaks.IIncludeBackground;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

public abstract class AbstractChromatogramViewPeakUI extends AbstractChromatogramLineSeriesUI implements IIncludeBackground {

	private IPeak peak;
	private boolean includeBackground = false;

	public AbstractChromatogramViewPeakUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesIntensityScale());
	}

	/**
	 * Sets the peak to show.
	 */
	protected void setPeak(IPeak peak) {

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

		if(peak instanceof IChromatogramPeakMSD) {
			convertMSD((IChromatogramPeakMSD)peak);
		} else if(peak instanceof IChromatogramPeakCSD) {
			convertCSD((IChromatogramPeakCSD)peak);
		}
	}

	// ---------------------------------------------------------------ISeriesSetter
	private void convertMSD(IChromatogramPeakMSD peak) {

		ISeries series;
		ILineSeries lineSeries;
		Sign sign = Sign.POSITIVE;
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

	private void convertCSD(IChromatogramPeakCSD peak) {

		ISeries series;
		ILineSeries lineSeries;
		Sign sign = Sign.POSITIVE;
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
}
