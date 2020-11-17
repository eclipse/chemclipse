/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IOverviewListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.OverviewSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.OverviewChartUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ChromatogramOverviewPart extends AbstractPart<OverviewChartUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;
	private final OverviewSupport overviewSupport = new OverviewSupport();

	@Inject
	public ChromatogramOverviewPart(Composite parent) {

		super(parent, TOPIC);
		overviewSupport.setOverviewListener(new IOverviewListener() {

			@Override
			public void update(Object object) {

				updateChart(object);
			}
		});
	}

	@Override
	protected OverviewChartUI createControl(Composite parent) {

		return new OverviewChartUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		return overviewSupport.process(objects, topic);
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return overviewSupport.isUpdateTopic(topic);
	}

	private void updateChart(Object object) {

		getControl().deleteSeries();
		if(object instanceof IChromatogramOverview) {
			/*
			 * Create series.
			 */
			IChromatogramOverview chromatogramOverview = (IChromatogramOverview)object;
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			ISeriesData seriesData = getSeriesData(chromatogramOverview);
			ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setEnableArea(false);
			ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
			lineSeriesSettingsHighlight.setLineWidth(2);
			lineSeriesDataList.add(lineSeriesData);
			getControl().addSeriesData(lineSeriesDataList);
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
