/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - rework for new datamodel and processor support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.chemclipse.nmr.model.core.FIDMeasurement;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection.ChangeType;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartNMR;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedNMRScanUI implements Observer {

	private static final String SERIES_ID = "NMR";
	private ChartNMR chartNMR;
	private IDataNMRSelection dataNMRSelection;

	public ExtendedNMRScanUI(Composite parent) {
		chartNMR = new ChartNMR(parent, SWT.NONE);
		IChartSettings chartSettings = chartNMR.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(false);
		chartSettings.setShowRangeSelectorInitially(false);
		chartNMR.applySettings(chartSettings);
	}

	public void update(IDataNMRSelection scanNMR) {

		if(dataNMRSelection != null) {
			dataNMRSelection.removeObserver(this);
		}
		this.dataNMRSelection = scanNMR;
		updateScan();
		scanNMR.addObserver(this);
	}

	private void updateScan() {

		chartNMR.deleteSeries();
		if(dataNMRSelection != null) {
			IComplexSignalMeasurement<?> measurement = getCurrentMeasurement();
			boolean rawData = measurement instanceof FIDMeasurement;
			chartNMR.modifyChart(rawData);
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			ILineSeriesData lineSeriesData;
			if(measurement == null) {
				lineSeriesData = new LineSeriesData(new SeriesData(new double[0], new double[0], SERIES_ID));
			} else {
				lineSeriesData = new LineSeriesData(ChartNMR.createSignalSeries(SERIES_ID, measurement.getSignals(), !rawData));
			}
			if(Boolean.getBoolean("editor.nmr.debug.seriesdata")) {
				ISeriesData data = lineSeriesData.getSeriesData();
				double[] xSeries = data.getXSeries();
				double[] ySeries = data.getYSeries();
				for(int i = 0; i < xSeries.length; i++) {
					System.out.println("[" + i + "] time = " + xSeries[i] + ", signal = " + ySeries[i]);
				}
			}
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setEnableArea(!rawData);
			lineSeriesSettings.setLineColor(Colors.RED);
			lineSeriesDataList.add(lineSeriesData);
			chartNMR.addSeriesData(lineSeriesDataList);
		}
	}

	private IComplexSignalMeasurement<?> getCurrentMeasurement() {

		return dataNMRSelection.getMeasurement();
	}

	public Control getControl() {

		return chartNMR;
	}

	@Override
	public void update(Observable o, Object arg) {

		if(arg == ChangeType.SELECTION_CHANGED) {
			Display.getDefault().asyncExec(this::updateScan);
		}
	}
}
