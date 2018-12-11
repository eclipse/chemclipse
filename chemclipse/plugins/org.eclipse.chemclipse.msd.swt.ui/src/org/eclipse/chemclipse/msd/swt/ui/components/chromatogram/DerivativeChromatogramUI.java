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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

public class DerivativeChromatogramUI extends AbstractViewMSDChromatogramUI {

	// 1 = 1st, 2 = 2nd, 3 = 3rd
	private int derivative;
	private int scale;

	public DerivativeChromatogramUI(Composite parent, int derivative, int scale, int style) {
		super(parent, style);
		this.derivative = derivative;
		this.scale = scale;
	}

	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)storedChromatogramSelection;
			/*
			 * Print line as derivative
			 */
			ISeries series;
			IMultipleSeries multipleSeries = SeriesConverter.convertDerivativeChromatogram(chromatogramSelection, derivative, scale, true);
			/*
			 * Chromatogram (TIC)
			 */
			series = multipleSeries.getMultipleSeries().get(0);
			setAdditionalIonSeries(series, Colors.BLACK);
			/*
			 * Derivative
			 */
			series = multipleSeries.getMultipleSeries().get(1);
			setAdditionalIonSeries(series, Colors.RED);
		}
	}

	// ---------------------------------------------------------------ISeriesSetter
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
}
