/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjsut to new API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartXIR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.xir.model.core.IScanXIR;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedXIRScanUI extends Composite implements IExtendedPartUI {

	private ChartXIR chartXIR;
	private IScanXIR scanXIR;
	//
	private Label labelDataInfo;
	private boolean showRawData = false;

	public ExtendedXIRScanUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IScanXIR scanXIR) {

		this.scanXIR = scanXIR;
		if(scanXIR != null) {
			showRawData = (scanXIR.getProcessedSignals().size() > 0) ? false : true;
		}
		chartXIR.modifyChart(showRawData);
		updateScan();
	}

	private void updateScan() {

		chartXIR.deleteSeries();
		String dataInfo = showRawData ? "Raw Data" : "Processed Data";
		//
		if(scanXIR != null) {
			/*
			 * Get the data.
			 */
			dataInfo += " | Rotation Angle: " + scanXIR.getRotationAngle() + "°";
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			ILineSeriesData lineSeriesData;
			ILineSeriesSettings lineSeriesSettings;
			//
			if(showRawData) {
				/*
				 * Raw and Background Data
				 */
				lineSeriesData = new LineSeriesData(getSeriesData(scanXIR, "Raw Signals", true));
				lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setLineColor(Colors.RED);
				lineSeriesSettings.setEnableArea(false);
				lineSeriesDataList.add(lineSeriesData);
				//
				lineSeriesData = new LineSeriesData(getSeriesData(scanXIR, "Background Signals", false));
				lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setLineColor(Colors.BLACK);
				lineSeriesSettings.setEnableArea(false);
				lineSeriesDataList.add(lineSeriesData);
			} else {
				/*
				 * Processed Data
				 */
				lineSeriesData = new LineSeriesData(getSeriesDataProcessed(scanXIR, "Processed Data"));
				lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setLineColor(Colors.RED);
				lineSeriesSettings.setEnableArea(false);
				lineSeriesDataList.add(lineSeriesData);
			}
			//
			chartXIR.addSeriesData(lineSeriesDataList);
		}
		//
		labelDataInfo.setText(dataInfo);
	}

	private ISeriesData getSeriesDataProcessed(IScanXIR scanXIR, String id) {

		double[] xSeries;
		double[] ySeries;
		//
		if(scanXIR != null) {
			int size = scanXIR.getProcessedSignals().size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(ISignalXIR scanSignal : scanXIR.getProcessedSignals()) {
				xSeries[index] = scanSignal.getWavelength();
				ySeries[index] = scanSignal.getIntensity();
				index++;
			}
		} else {
			xSeries = new double[0];
			ySeries = new double[0];
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getSeriesData(IScanXIR scanXIR, String id, boolean raw) {

		double[] ySeries;
		//
		if(scanXIR != null) {
			if(raw) {
				ySeries = scanXIR.getRawSignals().clone();
			} else {
				ySeries = scanXIR.getBackgroundSignals().clone();
			}
		} else {
			ySeries = new double[0];
		}
		//
		return new SeriesData(ySeries, id);
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createScanChart(this);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createDataInfoLabel(composite);
		createRawProcessedButton(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createDataInfoLabel(Composite parent) {

		labelDataInfo = new Label(parent, SWT.NONE);
		labelDataInfo.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		labelDataInfo.setLayoutData(gridData);
	}

	private void createRawProcessedButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the raw/processed modus");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_XIR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				showRawData = !showRawData;
				chartXIR.modifyChart(showRawData);
				updateScan();
			}
		});
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartXIR.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createToggleLegendMarkerButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend marker.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_LEGEND_MARKER, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartXIR.togglePositionLegendVisibility();
				chartXIR.redraw();
			}
		});
	}

	private void createToggleRangeSelectorButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart range selector.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_RANGE_SELECTOR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartXIR.toggleRangeSelectorVisibility();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the scan");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageChromatogram.class, PreferencePageSystem.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		updateScan();
	}

	private void reset() {

		updateScan();
	}

	private void createScanChart(Composite parent) {

		chartXIR = new ChartXIR(parent, SWT.BORDER);
		chartXIR.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chartXIR.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		//
		chartXIR.applySettings(chartSettings);
	}
}
