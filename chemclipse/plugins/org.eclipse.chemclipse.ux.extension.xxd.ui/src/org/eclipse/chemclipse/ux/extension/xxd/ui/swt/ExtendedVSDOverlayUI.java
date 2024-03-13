/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartVSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.vsd.model.core.ISignalVSD;
import org.eclipse.chemclipse.vsd.model.core.ISpectrumVSD;
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

import jakarta.inject.Inject;

public class ExtendedVSDOverlayUI extends Composite implements IExtendedPartUI {

	private AtomicReference<ChartVSD> chartControl = new AtomicReference<>();
	//
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	//
	private List<ISpectrumVSD> scanSelections = new ArrayList<>();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IColorScheme colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceSupplier.P_COLOR_SCHEME_DISPLAY_OVERLAY));

	@Inject
	public ExtendedVSDOverlayUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void update() {

		scanSelections = editorUpdateSupport.getVibrationalSpectroscopySelections();
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

		ChartVSD chartVSD = new ChartVSD(parent, SWT.BORDER, true); // TODO
		chartVSD.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		chartControl.set(chartVSD);
	}

	private void applySettings() {

		refreshUpdateOverlayChart();
	}

	private void refreshUpdateOverlayChart() {

		ChartVSD chartVSD = chartControl.get();
		chartVSD.deleteSeries();
		if(!scanSelections.isEmpty()) {
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			int i = 1;
			Color color = colorSchemeNormal.getColor();
			//
			for(ISpectrumVSD spectrumVSD : scanSelections) {
				/*
				 * Get the data.
				 */
				ILineSeriesData lineSeriesData = getLineSeriesData(spectrumVSD, "VSD_" + i++);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setLineColor(color);
				lineSeriesSettings.setEnableArea(false);
				//
				lineSeriesDataList.add(lineSeriesData);
				color = colorSchemeNormal.getNextColor();
			}
			//
			chartVSD.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
		}
	}

	private ILineSeriesData getLineSeriesData(ISpectrumVSD spectrumVSD, String id) {

		ILineSeriesData lineSeriesData = new LineSeriesData(getSeriesDataProcessed(spectrumVSD, id));
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineColor(Colors.RED);
		lineSeriesSettings.setEnableArea(true);
		//
		return lineSeriesData;
	}

	private ISeriesData getSeriesDataProcessed(ISpectrumVSD spectrumVSD, String id) {

		double[] xSeries;
		double[] ySeries;
		//
		if(spectrumVSD != null) {
			int size = spectrumVSD.getScanVSD().getProcessedSignals().size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(ISignalVSD scanSignal : spectrumVSD.getScanVSD().getProcessedSignals()) {
				xSeries[index] = scanSignal.getWavenumber();
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
