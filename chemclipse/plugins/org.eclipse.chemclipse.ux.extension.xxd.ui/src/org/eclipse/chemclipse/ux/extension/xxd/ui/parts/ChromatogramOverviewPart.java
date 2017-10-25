/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractChromatogramUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.IChromatogramUpdateSupport;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramOverviewPart extends AbstractChromatogramUpdateSupport implements IChromatogramUpdateSupport {

	private ChromatogramOverviewChart chromatogramOverviewChart;

	@Inject
	public ChromatogramOverviewPart(Composite parent, MPart part) {
		super(part);
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateChromatogram(getChromatogramOverview());
		chromatogramOverviewChart.setFocus();
	}

	private void initialize(Composite parent) {

		chromatogramOverviewChart = new ChromatogramOverviewChart(parent, SWT.NONE);
	}

	@Override
	public void updateChromatogram(IChromatogramOverview chromatogramOverview) {

		if(doUpdate()) {
			chromatogramOverviewChart.deleteSeries();
			if(chromatogramOverview != null) {
				/*
				 * Create series.
				 */
				List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
				ISeriesData seriesData = getSeriesData(chromatogramOverview);
				ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
				lineSeriesSettings.setEnableArea(false);
				ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
				lineSeriesSettingsHighlight.setLineWidth(2);
				lineSeriesDataList.add(lineSeriesData);
				chromatogramOverviewChart.addSeriesData(lineSeriesDataList);
			}
		}
	}

	private ISeriesData getSeriesData(IChromatogramOverview chromatogramOverview) {

		String seriesId = chromatogramOverview.getName();
		ITotalScanSignalExtractor totalSignalExtractor = new TotalScanSignalExtractor(chromatogramOverview);
		ITotalScanSignals totalScanSignals = totalSignalExtractor.getTotalScanSignals(false);
		int size = totalScanSignals.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		int i = 0;
		for(ITotalScanSignal totalScanSignal : totalScanSignals.getTotalScanSignals()) {
			xSeries[i] = totalScanSignal.getRetentionTime();
			ySeries[i] = totalScanSignal.getTotalSignal();
			i++;
		}
		//
		return new SeriesData(xSeries, ySeries, seriesId);
	}
}
