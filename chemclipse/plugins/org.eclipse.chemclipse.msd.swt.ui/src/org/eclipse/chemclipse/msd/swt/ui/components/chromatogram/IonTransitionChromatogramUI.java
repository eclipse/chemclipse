/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.chromatogram;

import java.util.Set;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

/**
 * Shows the chromatogram selection by the selected ion transitions stored in the
 * chromatogram selection instance.<br/>
 * The selected ion transitions will be drawn separately.
 * 
 */
public class IonTransitionChromatogramUI extends AbstractViewMSDChromatogramUI {

	public IonTransitionChromatogramUI(Composite parent, int style) {
		super(parent, style);
	}

	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)storedChromatogramSelection;
			/*
			 * There must be at least 1 selected ion.
			 */
			Set<IIonTransition> ionTransitions = chromatogramSelection.getMarkedIonTransitions().getSelectedIonTransitions();
			if(ionTransitions != null && ionTransitions.size() > 0) {
				/*
				 * Convert the ion transition selection.
				 */
				IMultipleSeries multipleSeries = SeriesConverterMSD.convertIonTranstionChromatogram(chromatogramSelection, ionTransitions);
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
