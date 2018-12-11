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
package org.eclipse.chemclipse.msd.swt.ui.components.chromatogram;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

/**
 * Shows the chromatogram selection by the selected ions stored in the
 * chromatogram selection instance.<br/>
 * The selected ions will be combined.
 * 
 * @author eselmeister
 */
public class SelectedIonCombinedChromatogramUI extends AbstractViewMSDChromatogramUI {

	public SelectedIonCombinedChromatogramUI(Composite parent, int style) {
		super(parent, style);
	}

	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
		if(storedChromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)storedChromatogramSelection;
			/*
			 * Convert the selected ions.
			 */
			IMarkedIons selectedIons = chromatogramSelection.getSelectedIons();
			if(selectedIons != null && selectedIons.size() > 0) {
				ILineSeries lineSeries;
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
				lineSeries.setLineColor(Colors.BLACK);
				/*
				 * Combined ion
				 */
				series = SeriesConverterMSD.convertChromatogramBySelectedIons(chromatogramSelection, selectedIons, Sign.POSITIVE);
				addSeries(series);
				lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				lineSeries.setXSeries(series.getXSeries());
				lineSeries.setYSeries(series.getYSeries());
				lineSeries.enableArea(true);
				lineSeries.setSymbolType(PlotSymbolType.NONE);
				lineSeries.setLineColor(Colors.RED);
			}
		}
	}
	// ---------------------------------------------------------------ISeriesSetter
}
