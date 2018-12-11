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
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.ISeries.SeriesType;

public class ScanMassSpectrumUI extends AbstractExtendedMassSpectrumUI implements IChromatogramSelectionMSDUpdateNotifier {

	public ScanMassSpectrumUI(Composite parent, int style, MassValueDisplayPrecision massSpectrumType) {
		super(parent, style, massSpectrumType);
	}

	// -------------------------------------------------------------set the
	// series
	@Override
	public void setViewSeries() {

		if(massSpectrum != null) {
			ISeries series = getSeries(massSpectrum);
			multipleLineSeries.add(series);
			barSeriesPositive = (IBarSeries)getSeriesSet().createSeries(SeriesType.BAR, series.getId());
			barSeriesPositive.setXSeries(series.getXSeries());
			barSeriesPositive.setYSeries(series.getYSeries());
			barSeriesPositive.setBarWidthStyle(BarWidthStyle.FIXED);
			barSeriesPositive.setBarWidth(1);
			Color massSpectrumColor = PreferenceSupplier.getMassSpectrumColor();
			if(massSpectrumColor == null) {
				massSpectrumColor = Colors.RED;
			}
			barSeriesPositive.setBarColor(massSpectrumColor);
		}
	}

	// -------------------------------------------------------------set the
	// series
	// ------------------------------------------------------ISelectionUpdateNotifier
	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Set the previous chromatogram selection as we would like to show the
		 * chromatogram selection as selected by the use before update.<br/> In
		 * master mode, it is sometimes neccessary to reload all values.<br/>
		 * After loading all values, setPrimaryRanges in setSeries(forceReload)
		 * will show the previous values if the actual instance is in master
		 * mode.<br/> Huuh, i'm not really a big friend of this GUI problems.
		 */
		this.chromatogramSelection = chromatogramSelection;
		this.massSpectrum = chromatogramSelection.getSelectedScan();
		/*
		 * If the current view is not a master, reload the data on each
		 * update.<br/> If the ui is in master mode and force reload is set,
		 * load the series.
		 */
		if(!master || (master && forceReload)) {
			setSeries(forceReload);
		}
	}
	// ------------------------------------------------------ISelectionUpdateNotifier
}
