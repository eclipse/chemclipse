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
package org.eclipse.chemclipse.msd.swt.ui.components.chromatogram;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

/**
 * Shows the chromatogram selection by the given compound ions and the TIC.<br/>
 * The selected ions will be drawn separately.
 * 
 * This class may be used to display hydrocarbon compounds or others.
 */
public class OrganicCompoundChromatogramUI extends AbstractViewMSDChromatogramUI {

	private IMarkedIons compoundIons;

	public OrganicCompoundChromatogramUI(Composite parent, int style) {
		super(parent, style);
		this.compoundIons = new MarkedIons();
	}

	public void setMarkedIons(IMarkedIons compoundIons) {

		if(compoundIons == null) {
			this.compoundIons = new MarkedIons();
		} else {
			this.compoundIons = compoundIons;
		}
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
			if(compoundIons.size() > 0) {
				/*
				 * Convert a selection.
				 */
				IMultipleSeries multipleSeries = SeriesConverterMSD.convertChromatogram(chromatogramSelection, compoundIons, false, Sign.POSITIVE);
				int size = multipleSeries.getMultipleSeries().size();
				ISeries series;
				String colorSchemeOverlay = PreferenceSupplier.getColorSchemeOverlay();
				IColorScheme colorScheme = Colors.getColorScheme(colorSchemeOverlay);
				/*
				 * Chromatogram (TIC)
				 */
				series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
				setAdditionalIonSeries(series, colorScheme.getColor());
				colorScheme.incrementColor();
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
