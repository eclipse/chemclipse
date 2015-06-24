/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.components.scan;

import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.swt.ui.components.AbstractProfileScanUI;
import org.eclipse.chemclipse.wsd.swt.ui.converter.SeriesConverterWSD;

public class ProfileScanUI extends AbstractProfileScanUI {

	public ProfileScanUI(Composite parent, int style) {

		super(parent, style);
	}

	@Override
	public void setViewSeries() {

		IChromatogramSelectionWSD chromatogramSelection = getChromatogramSelection();
		IScanWSD supplierScan = chromatogramSelection.getSelectedScan();
		if(supplierScan != null) {
			ISeries series;
			ILineSeries lineSeries;
			//
			series = SeriesConverterWSD.convertSupplierScan(supplierScan, Sign.POSITIVE);
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
