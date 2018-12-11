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
package org.eclipse.chemclipse.swt.ui.components.chromatogram;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.IAxisTick;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

/**
 * Draws the chromatogram overview. It's a simple view and will not show any
 * axes or additional information.
 * 
 * @author eselmeister
 */
public class ChromatogramOverviewUI extends Chart {

	private DecimalFormat decimalFormat;

	public ChromatogramOverviewUI(Composite parent, int style) {
		super(parent, style);
		/*
		 * The overview shall be displayed without
		 * extra space for the intensity axis.
		 * That's why ValueFormat isn't used here.
		 */
		decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(0);
		decimalFormat.setMaximumIntegerDigits(0);
		initialize();
	}

	/**
	 * Set the overview of the given chromatogram.
	 * 
	 * @param chromatogramOverview
	 */
	public void showChromatogramOverview(IChromatogramOverview chromatogramOverview) {

		if(chromatogramOverview != null) {
			setChromatogramOverview(chromatogramOverview);
		}
	}

	// -------------------------------------------private methods
	/**
	 * Initializes the chart.
	 */
	private void initialize() {

		setBackground(Colors.WHITE);
		setBackgroundInPlotArea(Colors.WHITE);
		/*
		 * No title, no legend.
		 */
		getLegend().setVisible(false);
		getTitle().setVisible(false);
		/*
		 * Axes and line series
		 */
		IAxisTick xAxisTick = getAxisSet().getXAxis(0).getTick();
		IAxisTick yAxisTick = getAxisSet().getYAxis(0).getTick();
		//
		getAxisSet().getXAxis(0).getTitle().setVisible(false);
		xAxisTick.setVisible(false);
		xAxisTick.setForeground(Colors.BLACK);
		//
		getAxisSet().getYAxis(0).getTitle().setVisible(false);
		yAxisTick.setVisible(false);
		yAxisTick.setFormat(decimalFormat);
		yAxisTick.setForeground(Colors.BLACK);
		//
		getAxisSet().adjustRange();
	}

	/**
	 * Draws the chromatogram overview.
	 * 
	 * @param chromatogramOverview
	 */
	private void setChromatogramOverview(IChromatogramOverview chromatogramOverview) {

		deleteAllCurrentSeries();
		ISeries series = SeriesConverter.convertChromatogramOverview(chromatogramOverview, Sign.POSITIVE, false);
		ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId() + "+");
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.RED);
		IAxisSet axisSet = getAxisSet();
		/*
		 * Don't show the milliseconds axis.
		 */
		IAxis milliseconds = axisSet.getXAxis(0);
		milliseconds.getTick().setVisible(false);
		milliseconds.getTitle().setVisible(false);
		/*
		 * Don't show the abundance axis.
		 */
		IAxis abundance = axisSet.getYAxis(0);
		abundance.getTick().setVisible(false);
		abundance.getTick().setFormat(decimalFormat);
		abundance.getTitle().setVisible(false);
		//
		getAxisSet().adjustRange();
		getPlotArea().redraw();
	}

	/**
	 * Deletes all series.
	 */
	private void deleteAllCurrentSeries() {

		org.eclipse.swtchart.ISeries[] series = getSeriesSet().getSeries();
		List<String> ids = new ArrayList<String>();
		/*
		 * Get the ids.
		 */
		for(org.eclipse.swtchart.ISeries serie : series) {
			ids.add(serie.getId());
		}
		/*
		 * Remove all ids.
		 */
		for(String id : ids) {
			getSeriesSet().deleteSeries(id);
		}
	}
	// -------------------------------------------private methods
}
