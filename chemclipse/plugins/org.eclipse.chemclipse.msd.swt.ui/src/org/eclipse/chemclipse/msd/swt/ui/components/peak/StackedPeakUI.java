/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.AbstractStackedPeakLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

public class StackedPeakUI extends AbstractStackedPeakLineSeriesUI {

	public StackedPeakUI(Composite parent, int style, IAxisTitles axisTitles) {
		super(parent, style, axisTitles);
	}

	@Override
	public void setViewSeries() {

		String colorSchemeOverlay = PreferenceSupplier.getColorSchemeOverlay();
		IColorScheme colorScheme = Colors.getColorScheme(colorSchemeOverlay);
		int counter = 1;
		for(IPeakMSD peak : peakListMSD) {
			/*
			 * Display all peaks
			 */
			ISeries series = SeriesConverter.convertPeak(peak, false, Sign.POSITIVE);
			addSeries(series);
			ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, "Peak [" + counter + "]");
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			lineSeries.enableArea(true);
			lineSeries.setSymbolType(PlotSymbolType.NONE);
			lineSeries.setLineColor(colorScheme.getColor());
			colorScheme.incrementColor();
			counter++;
		}
	}
}
