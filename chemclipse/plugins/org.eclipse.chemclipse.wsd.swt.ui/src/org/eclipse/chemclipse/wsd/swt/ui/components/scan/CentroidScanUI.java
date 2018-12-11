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
package org.eclipse.chemclipse.wsd.swt.ui.components.scan;

import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.notifier.IChromatogramSelectionWSDUpdateNotifier;
import org.eclipse.chemclipse.wsd.swt.ui.converter.SeriesConverterWSD;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.ISeries.SeriesType;

public class CentroidScanUI extends AbstractExtendedScan implements IChromatogramSelectionWSDUpdateNotifier {

	public CentroidScanUI(Composite parent, int style) {
		super(parent, style);
	}

	// -------------------------------------------------------------set the
	// series
	@Override
	public void setViewSeries() {

		if(supplierScan != null) {
			ISeries series = SeriesConverterWSD.convertSupplierScan(supplierScan, Sign.POSITIVE);
			multipleLineSeries.add(series);
			IBarSeries barSeries = (IBarSeries)getSeriesSet().createSeries(SeriesType.BAR, series.getId());
			barSeries.setXSeries(series.getXSeries());
			barSeries.setYSeries(series.getYSeries());
			barSeries.setBarWidthStyle(BarWidthStyle.FIXED);
			barSeries.setBarWidth(1);
			barSeries.setBarColor(Colors.RED);
		}
	}

	// -------------------------------------------------------------set the
	// series
	// ------------------------------------------------------ISelectionUpdateNotifier
	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		/*
		 * Set the previous chromatogram selection as we would like to show the
		 * chromatogram selection as selected by the use before update.<br/> In
		 * master mode, it is sometimes neccessary to reload all values.<br/>
		 * After loading all values, setPrimaryRanges in setSeries(forceReload)
		 * will show the previous values if the actual instance is in master
		 * mode.<br/> Huuh, i'm not really a big friend of this GUI problems.
		 */
		this.chromatogramSelection = chromatogramSelection;
		this.supplierScan = chromatogramSelection.getSelectedScan();
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
