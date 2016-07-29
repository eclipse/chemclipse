/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.components.chromatogram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.swt.ui.converter.SeriesConverterWSD;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

public class SelectedWavelengthChromatogramUI extends AbstractViewWSDChromatogramUI {

	public SelectedWavelengthChromatogramUI(Composite parent, int style) {
		super(parent, style);
	}

	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionWSD) {
			IChromatogramSelectionWSD chromatogramSelection = (IChromatogramSelectionWSD)storedChromatogramSelection;
			/*
			 * Convert a selection.
			 */
			List<Integer> wavelengths = new ArrayList<Integer>(chromatogramSelection.getSelectedWavelengths().getWavelengths());
			IMultipleSeries multipleSeries = SeriesConverterWSD.convertChromatogram(chromatogramSelection, wavelengths, false, Sign.POSITIVE);
			int size = multipleSeries.getMultipleSeries().size();
			ISeries series;
			String colorSchemeOverlay = PreferenceSupplier.getColorSchemeOverlay();
			IColorScheme colorScheme = Colors.getColorScheme(colorSchemeOverlay);
			/*
			 * Set the series.
			 */
			for(int i = 0; i < size; i++) {
				series = multipleSeries.getMultipleSeries().get(i);
				setAdditionalIonSeries(series, colorScheme.getColor());
				colorScheme.incrementColor();
			}
		}
	}

	// ---------------------------------------------------------------ISeriesSetter
	// ------------------------------------------private methods
	/**
	 * Sets additional ion series.
	 * 
	 * @param series
	 * @param color
	 */
	private void setAdditionalIonSeries(ISeries series, Color color) {

		addSeries(series);
		ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(color);
	}
	// ------------------------------------------private methods
}
