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
 * Matthias Mailänder - add color compensation, per channel coloring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartPCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCodes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageWellChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedWellChartUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedWellChartUI.class);
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Combo comboChannels;
	private AtomicReference<ChartPCR> chartControl = new AtomicReference<>();
	//
	private IWell well = null;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private boolean colorCompensation = true;

	public ExtendedWellChartUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IWell well) {

		this.well = well;
		updateLabel();
		updateChannelCombo();
		updateChart();
	}

	private void updateChannelCombo() {

		if(well != null) {
			if(well.isEmptyMeasurement()) {
				comboChannels.setItems();
			} else {
				comboChannels.setItems(getComboItems(well));
				IChannel channel = well.getActiveChannel();
				if(channel != null) {
					String name = channel.getDetectionName();
					String[] items = comboChannels.getItems();
					exitloop:
					for(int i = 0; i < items.length; i++) {
						if(items[i].equals(name)) {
							comboChannels.select(i);
							break exitloop;
						}
					}
				} else {
					comboChannels.select(0);
				}
			}
		} else {
			comboChannels.setItems("");
		}
	}

	private String[] getComboItems(IWell well) {

		if(well != null) {
			List<String> items = new ArrayList<>();
			for(IChannel channel : well.getChannels().values()) {
				items.add(channel.getDetectionName());
			}
			return items.toArray(new String[items.size()]);
		} else {
			return new String[]{};
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		comboChannels = createComboChannels(this);
		createChart(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		createButtonToggleChartLegend(composite, chartControl, IMAGE_LEGEND);
		createResetButton(composite);
		createColorCompensationButton(composite);
		createSettingsButton(composite);
	}

	private void createColorCompensationButton(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setToolTipText("Toggle Color Compensation");
		button.setText("");
		button.setSelection(colorCompensation);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_BAR_CHART, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				colorCompensation = !colorCompensation;
				button.setSelection(colorCompensation);
				updateChart();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Chart");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateChart();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageWellChart.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				updateChart();
			}
		});
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private Combo createComboChannels(Composite parent) {

		Combo combo = EnhancedCombo.create(parent, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateChart();
			}
		});
		//
		return combo;
	}

	private void createChart(Composite parent) {

		ChartPCR chart = new ChartPCR(parent, SWT.NONE);
		chart.setLayoutData(new GridData(GridData.FILL_BOTH));
		IChartSettings chartSettings = chart.getChartSettings();
		chartSettings.setTitleVisible(false);
		chart.applySettings(chartSettings);
		//
		chartControl.set(chart);
	}

	private void updateChart() {

		/*
		 * Clear the chart and reset.
		 */
		chartControl.get().deleteSeries();
		if(well != null && !well.isEmptyMeasurement()) {
			/*
			 * Extract the channels.
			 */
			ColorCodes colorCodes = new ColorCodes();
			colorCodes.load(preferenceStore.getString(PreferenceSupplier.P_PCR_WELL_COLOR_CODES));
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			int index = comboChannels.getSelectionIndex();
			if(index == 0) {
				for(IChannel channel : well.getChannels().values()) {
					Color color = getChannelColor(channel, colorCodes);
					addChannelData(channel, lineSeriesDataList, color);
				}
			} else {
				try {
					IChannel channel = well.getChannels().get(index - 1);
					Color color = getChannelColor(channel, colorCodes);
					addChannelData(channel, lineSeriesDataList, color);
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
			}
			//
			chartControl.get().addSeriesData(lineSeriesDataList);
		} else {
			chartControl.get().adjustRange(true);
		}
	}

	private Color getChannelColor(IChannel channel, ColorCodes colorCodes) {

		String detectionName = channel.getDetectionName();
		String channelName = channel.getName();
		if(colorCodes.containsKey(detectionName)) {
			return colorCodes.get(detectionName).getColor();
		} else if(colorCodes.containsKey(channelName)) {
			return colorCodes.get(channelName).getColor();
		} else {
			return Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_PCR_DEFAULT_COLOR));
		}
	}

	private void addChannelData(IChannel channel, List<ILineSeriesData> lineSeriesDataList, Color color) {

		ILineSeriesData channelCurve = getChannelCurve(channel, color);
		if(channelCurve != null) {
			lineSeriesDataList.add(channelCurve);
		}
	}

	private ILineSeriesData getChannelCurve(IChannel channel, Color color) {

		ILineSeriesData lineSeriesData = null;
		if(channel != null) {
			List<Double> pointList = colorCompensation ? channel.getColorCompensatedFluorescence() : channel.getFluorescence();
			double[] points = new double[pointList.size()];
			for(int index = 0; index < pointList.size(); index++) {
				points[index] = pointList.get(index);
			}
			ISeriesData seriesData = new SeriesData(points, String.valueOf(channel.getId()));
			lineSeriesData = new LineSeriesData(seriesData);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineColor(color);
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettings.setDescription(channel.getDetectionName());
		}
		return lineSeriesData;
	}

	private void updateLabel() {

		toolbarInfo.get().setText(well != null ? well.getLabel() : "--");
	}
}
