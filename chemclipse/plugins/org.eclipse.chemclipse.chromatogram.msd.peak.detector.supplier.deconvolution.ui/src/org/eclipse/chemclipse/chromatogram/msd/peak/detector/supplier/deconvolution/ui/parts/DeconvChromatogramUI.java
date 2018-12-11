/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.ui.parts;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.DeconvHelper;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArraysViewDeconv;
import org.eclipse.chemclipse.msd.swt.ui.components.chromatogram.AbstractViewMSDChromatogramUI;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;
import org.eclipse.swtchart.LineStyle;

public class DeconvChromatogramUI extends AbstractViewMSDChromatogramUI {

	private ISeries series;

	public DeconvChromatogramUI(Composite parent, int style) {
		super(parent, style);
	}

	public void setAdditionalDisplayData(ISeries series) {

		this.series = series;
	}

	public void setView(IArraysViewDeconv arraysViewDeconv) {

		Color color;
		boolean dashedLine = false;
		boolean barMode = false;
		boolean areaUnderLine = false;
		IMultipleSeries multipleSeries = new MultipleSeries();
		/*
		 * Converter
		 */
		multipleSeries = DeconvConverterUI.setViewSeries(arraysViewDeconv);
		int sizeSeries = multipleSeries.getMultipleSeries().size();
		for(int i = 0; i < sizeSeries; i++) {
			series = multipleSeries.getMultipleSeries().get(i);
			switch(series.getId()) {
				case DeconvHelper.PROPERTY_DECONVIEW_ORIGINALCHROMA:
					color = Colors.GRAY;// Colors.RED;
					areaUnderLine = true;
					break;
				case DeconvHelper.PROPERTY_DECONVIEW_BASELINE:
					color = Colors.DARK_GRAY;
					break;
				case DeconvHelper.PROPTERY_DECONVIEW_NOISE:
					color = Colors.BLUE;
					break;
				case DeconvHelper.PROPTERY_DECONVIEW_SMOOTHED:
					color = Colors.BLACK;
					break;
				case DeconvHelper.PROPTERY_DECONVIEW_FIRSTDERIV:
					color = Colors.RED;
					break;
				case DeconvHelper.PROPTERY_DECONVIEW_SECONDDERIV:
					color = Colors.DARK_GREEN;
					break;
				case DeconvHelper.PROPTERY_DECONVIEW_THIRDDERIV:
					color = Colors.GREEN;
					break;
				case DeconvHelper.PROPERTY_DECONVIEW_PEAKRANGES:
					color = Colors.MAGENTA;
					barMode = true;
					break;
				case DeconvHelper.PROPERTY_DECONVIEW_PEAKRANGES_ENDPOINTS:
					color = Colors.GREEN;
					barMode = true;
					break;
				default:
					color = Colors.DARK_GRAY;
					break;
			}
			setAdditionalIonseries(series, color, dashedLine, areaUnderLine, barMode);
			dashedLine = areaUnderLine = barMode = false;
		}
	}

	/**
	 * 
	 * @param series
	 * @param color
	 */
	private void setAdditionalIonseries(ISeries series, Color color, boolean dashedLine, boolean areaUnderLine, boolean barMode) {

		addSeries(series);
		ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		if(barMode) {
			IBarSeries barSeries = (IBarSeries)getSeriesSet().createSeries(SeriesType.BAR, series.getId());
			barSeries.setXSeries(series.getXSeries());
			barSeries.setYSeries(series.getYSeries());
			barSeries.setBarColor(color);
			barSeries.setBarWidthStyle(BarWidthStyle.FIXED);
			barSeries.setBarWidth(1);
		} else {
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			if(areaUnderLine) {
				lineSeries.enableArea(areaUnderLine);
			}
			if(dashedLine) {
				lineSeries.setLineStyle(LineStyle.DASH);
			} else {
				lineSeries.setSymbolType(PlotSymbolType.NONE);
			}
			lineSeries.setLineColor(color);
		}
	}

	@Override
	public void setViewSeries() {

	}
}
