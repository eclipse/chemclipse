/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

/**
 * Shows the chromatogram selection by the ions without the excluded
 * ones stored in the chromatogram selection instance.<br/>
 * 
 * @author eselmeister
 */
public class SelectedPeakChromatogramUI extends AbstractChromatogramLineSeriesUI {

	public SelectedPeakChromatogramUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesIntensityScale());
	}

	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		/*
		 * Convert the chromatogram without the excluded ions.
		 */
		ILineSeries lineSeries;
		ILineSeries peakSeries;
		ILineSeries backgroundSeries;
		ISeries series;
		/*
		 * Chromatogram
		 */
		series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.GRAY);
		/*
		 * Show the selected peak if available
		 */
		IPeak peak = chromatogramSelection.getSelectedPeak();
		if(peak != null) {
			/*
			 * Peak
			 */
			series = SeriesConverter.convertSelectedPeak(peak, true, Sign.POSITIVE);
			peakSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			peakSeries.setXSeries(series.getXSeries());
			peakSeries.setYSeries(series.getYSeries());
			peakSeries.enableArea(true);
			peakSeries.setSymbolType(PlotSymbolType.NONE);
			peakSeries.setLineColor(Colors.RED);
			/*
			 * Background
			 */
			series = SeriesConverter.convertSelectedPeakBackground(peak, Sign.POSITIVE);
			backgroundSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			backgroundSeries.setXSeries(series.getXSeries());
			backgroundSeries.setYSeries(series.getYSeries());
			backgroundSeries.enableArea(true);
			backgroundSeries.setSymbolType(PlotSymbolType.NONE);
			backgroundSeries.setLineColor(Colors.BLACK);
		}
	}
	// ---------------------------------------------------------------ISeriesSetter
}
