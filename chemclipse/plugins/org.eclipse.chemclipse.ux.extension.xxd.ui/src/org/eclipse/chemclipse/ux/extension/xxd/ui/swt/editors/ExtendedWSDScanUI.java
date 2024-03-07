/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartWSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.wsd.model.core.ISignalWSD;
import org.eclipse.chemclipse.wsd.model.core.ISpectrumWSD;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedWSDScanUI extends Composite implements IExtendedPartUI {

	private ChartWSD chartWSD;
	private ISpectrumWSD spectrumWSD;
	//
	private boolean showAbsorbance = true;

	public ExtendedWSDScanUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(ISpectrumWSD spectrumWSD) {

		this.spectrumWSD = spectrumWSD;
		chartWSD.modifyChart(showAbsorbance);
		updateScan();
	}

	private void updateScan() {

		chartWSD.deleteSeries();
		//
		if(spectrumWSD != null) {
			/*
			 * Get the data.
			 */
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			ILineSeriesData lineSeriesData;
			ILineSeriesSettings lineSeriesSettings;
			//
			lineSeriesData = new LineSeriesData(getSeriesData(spectrumWSD, "Data"));
			lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineColor(Colors.RED);
			lineSeriesSettings.setEnableArea(false);
			lineSeriesDataList.add(lineSeriesData);
			//
			chartWSD.addSeriesData(lineSeriesDataList);
		}
	}

	private ISeriesData getSeriesData(ISpectrumWSD spectrumWSD, String id) {

		double[] xSeries;
		double[] ySeries;
		//
		if(spectrumWSD != null) {
			int size = spectrumWSD.getSignals().size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(ISignalWSD scanSignal : spectrumWSD.getSignals()) {
				xSeries[index] = scanSignal.getWavelength();
				if(showAbsorbance) {
					ySeries[index] = scanSignal.getAbsorbance();
				}
				index++;
			}
		} else {
			xSeries = new double[0];
			ySeries = new double[0];
		}
		//
		return new SeriesData(xSeries, ySeries, id);
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
		composite.setLayout(new GridLayout(8, false));
		//
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartWSD.toggleSeriesLegendVisibility();
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

				chartWSD.togglePositionLegendVisibility();
				chartWSD.redraw();
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

				chartWSD.toggleRangeSelectorVisibility();
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

		chartWSD = new ChartWSD(parent, SWT.BORDER, showAbsorbance);
		chartWSD.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chartWSD.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		//
		chartWSD.applySettings(chartSettings);
	}
}
