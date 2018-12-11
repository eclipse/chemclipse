/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.chromatogram;

import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.swt.ui.support.Sign;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

public class MultipleChromatogramOffsetSelectedIonsUI extends AbstractViewMSDChromatogramUI {

	private IOffset offset;

	public MultipleChromatogramOffsetSelectedIonsUI(Composite parent, int style, IOffset offset) {
		super(parent, style);
	}

	/**
	 * Sets the offset.
	 * 
	 * @param offset
	 */
	public void setOffset(IOffset offset) {

		/*
		 * If the offset is null, replace it by a valid instance.
		 */
		if(offset != null) {
			this.offset = offset;
		} else {
			this.offset = new Offset(0.0d, 0.0d);
		}
	}

	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		/*
		 * Convert a selection.
		 */
		boolean showChromatogramArea = PreferenceSupplier.showChromatogramArea();
		IMultipleSeries multipleSeries = SeriesConverterMSD.convertChromatogramsUseSelectedIons(getChromatogramSelections(), Sign.POSITIVE, offset);
		int size = multipleSeries.getMultipleSeries().size();
		ISeries series;
		String colorSchemeOverlay = PreferenceSupplier.getColorSchemeOverlay();
		IColorScheme colorScheme = Colors.getColorScheme(colorSchemeOverlay);
		/*
		 * Set the series.
		 */
		for(int i = 0; i < size; i++) {
			series = multipleSeries.getMultipleSeries().get(i);
			setAdditionalChromatogramSeries(series, colorScheme.getColor(), i, showChromatogramArea);
			colorScheme.incrementColor();
		}
	}

	// ---------------------------------------------------------------ISeriesSetter
	// ------------------------------------------private methods
	/**
	 * Sets additional chromatogram series.
	 * 
	 * @param series
	 * @param color
	 */
	private void setAdditionalChromatogramSeries(ISeries series, Color color, int i, boolean showChromatogramArea) {

		addSeries(series);
		ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(showChromatogramArea);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(color);
	}
	// ------------------------------------------private methods
}
