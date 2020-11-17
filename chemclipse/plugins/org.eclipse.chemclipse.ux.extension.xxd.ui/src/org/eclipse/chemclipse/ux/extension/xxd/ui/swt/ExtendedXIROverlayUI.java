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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartXIR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.xir.model.core.IScanXIR;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedXIROverlayUI extends Composite implements IExtendedPartUI {

	private AtomicReference<ChartXIR> chartControl = new AtomicReference<>();
	//
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	//
	private List<IScanXIR> scanSelections = new ArrayList<>();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IColorScheme colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_OVERLAY));

	@Inject
	public ExtendedXIROverlayUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update() {

		scanSelections = editorUpdateSupport.getScanSelectionsXIR();
		refreshUpdateOverlayChart();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createOverlayChart(this);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleChartLegend(composite, chartControl, IMAGE_LEGEND);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				applySettings();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageOverlay.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createOverlayChart(Composite parent) {

		ChartXIR chartXIR = new ChartXIR(parent, SWT.BORDER);
		chartXIR.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		chartControl.set(chartXIR);
	}

	private void applySettings() {

		refreshUpdateOverlayChart();
	}

	private void refreshUpdateOverlayChart() {

		ChartXIR chartXIR = chartControl.get();
		chartXIR.deleteSeries();
		if(scanSelections.size() > 0) {
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			int i = 1;
			Color color = colorSchemeNormal.getColor();
			//
			for(IScanXIR scanXIR : scanSelections) {
				/*
				 * Get the data.
				 */
				ILineSeriesData lineSeriesData = getLineSeriesData(scanXIR, "XIR_" + i++);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setLineColor(color);
				lineSeriesSettings.setEnableArea(false);
				//
				lineSeriesDataList.add(lineSeriesData);
				color = colorSchemeNormal.getNextColor();
			}
			//
			chartXIR.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
		}
	}

	private ILineSeriesData getLineSeriesData(IScanXIR scanXIR, String id) {

		ILineSeriesData lineSeriesData = new LineSeriesData(getSeriesDataProcessed(scanXIR, id));
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineColor(Colors.RED);
		lineSeriesSettings.setEnableArea(true);
		//
		return lineSeriesData;
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
}
