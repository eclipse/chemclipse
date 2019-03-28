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
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.nmr.model.core.FIDMeasurement;
import org.eclipse.chemclipse.nmr.model.core.SpectrumMeasurement;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartNMR;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedNMRScanUI {

	private static final Image IMAGE_FREQUENCY = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_NMR, IApplicationImage.SIZE_16x16);
	private static final Image IMAGE_FID = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_FID, IApplicationImage.SIZE_16x16);
	private ChartNMR chartNMR;
	private IDataNMRSelection dataNMRSelection;
	//
	// private CLabel labelDataInfo;
	private ToolItem toggleRawMode;
	//

	public ExtendedNMRScanUI(Composite parent) {
		initialize(parent);
	}

	public void update(IDataNMRSelection scanNMR) {

		this.dataNMRSelection = scanNMR;
		// if(scanNMR != null) {
		// showRawData = (scanNMR.getMeasurmentNMR().getScanMNR().getNumberOfFourierPoints() > 0) ? false : true;
		// }
		chartNMR.modifyChart(toggleRawMode.getSelection());
		updateScan();
	}

	private void updateScan() {

		chartNMR.deleteSeries();
		// toggleRawMode.setText(toggleRawMode.getSelection() ? " FID " : " Frequency ");
		toggleRawMode.setImage(toggleRawMode.getSelection() ? IMAGE_FID : IMAGE_FREQUENCY);
		//
		if(dataNMRSelection != null) {
			/*
			 * Get the data.
			 */
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			ILineSeriesData lineSeriesData = getLineSeriesData(dataNMRSelection, "NMR", toggleRawMode.getSelection());
			//
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineColor(Colors.RED);
			if(toggleRawMode.getSelection()) {
				lineSeriesSettings.setEnableArea(false);
			} else {
				lineSeriesSettings.setEnableArea(true);
			}
			//
			lineSeriesDataList.add(lineSeriesData);
			chartNMR.addSeriesData(lineSeriesDataList);
		}
	}

	private ILineSeriesData getLineSeriesData(IDataNMRSelection dataNMRSelection, String id, boolean raw) {

		if(raw) {
			FIDMeasurement fid = getFIDMeasurement(dataNMRSelection);
			if(fid != null) {
				return new LineSeriesData(createSignalSeries(id, fid.getSignals()));
			}
		} else {
			SpectrumMeasurement nmr = getNMRMeasurement(dataNMRSelection);
			if(nmr != null) {
				return new LineSeriesData(createSignalSeries(id, nmr.getSignals()));
			}
		}
		return new LineSeriesData(new SeriesData(new double[0], new double[0], id));
	}

	private static SpectrumMeasurement getNMRMeasurement(IDataNMRSelection selection) {

		IMeasurement measurement = selection.getMeasurement();
		if(measurement instanceof SpectrumMeasurement) {
			return (SpectrumMeasurement)measurement;
		}
		return Adapters.adapt(measurement, SpectrumMeasurement.class);
	}

	private static FIDMeasurement getFIDMeasurement(IDataNMRSelection selection) {

		IMeasurement measurement = selection.getMeasurement();
		if(measurement instanceof FIDMeasurement) {
			return (FIDMeasurement)measurement;
		}
		return Adapters.adapt(measurement, FIDMeasurement.class);
	}

	public ISeriesData createSignalSeries(String id, Collection<? extends ISignal> signals) {

		int size = signals.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(ISignal fidSignal : signals) {
			xSeries[index] = fidSignal.getX();
			ySeries[index] = fidSignal.getY();
			index++;
		}
		return new SeriesData(xSeries, ySeries, id);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createScanChart(parent);
	}

	private void createToolbarMain(Composite parent) {

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		// toolBar.setFont(JFaceResources.getTextFont());
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(toolBar);
		// createInfoLabel(toolBar);
		createRawProcessedButton(toolBar);
	}

	// public void createInfoLabel(ToolBar toolBar) {
	//
	// ToolItem toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
	// labelDataInfo = new CLabel(toolBar, SWT.CENTER);
	// toolItem.setWidth(200);
	// toolItem.setControl(labelDataInfo);
	// }
	private void createRawProcessedButton(ToolBar toolBar) {

		toggleRawMode = new ToolItem(toolBar, SWT.CHECK);
		toggleRawMode.setToolTipText("Toggle the FID/Frequency modus");
		toggleRawMode.setImage(IMAGE_FREQUENCY);
		toggleRawMode.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartNMR.modifyChart(toggleRawMode.getSelection());
				updateScan();
			}
		});
	}

	private void createScanChart(Composite parent) {

		chartNMR = new ChartNMR(parent, SWT.NONE);
		chartNMR.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chartNMR.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		//
		chartNMR.applySettings(chartSettings);
	}
}
