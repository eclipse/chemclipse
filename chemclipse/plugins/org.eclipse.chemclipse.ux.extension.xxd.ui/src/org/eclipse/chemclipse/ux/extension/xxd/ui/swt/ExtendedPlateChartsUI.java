/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add color compensation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartPCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCodes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePlateChart;
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
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.ISeriesSettings;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.core.SeriesMapper;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedPlateChartsUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedPlateChartsUI.class);
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Combo comboChannels;
	private AtomicReference<ChartPCR> chartControl = new AtomicReference<>();
	private IPlate plate = null;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private boolean colorCompensation = true;

	@Inject
	public ExtendedPlateChartsUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IPlate plate) {

		this.plate = plate;
		updateLabel();
		updateComboChannels();
		updateChartData();
	}

	private void updateLabel() {

		toolbarInfo.get().setText(plate != null ? plate.getName() : "--");
	}

	private void updateComboChannels() {

		if(plate != null) {
			List<String> activeChannels = plate.getActiveChannels();
			comboChannels.setItems(plate.getActiveChannels().toArray(new String[activeChannels.size()]));
			comboChannels.select(plate.getActiveChannel());
		} else {
			comboChannels.setItems(new String[]{""});
		}
	}

	private void updateChartData() {

		if(plate != null) {
			updateChart();
		} else {
			chartControl.get().deleteSeries();
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
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
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createToolbarInfo(composite);
		comboChannels = createComboChannels(composite);
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		createButtonToggleChartLegend(composite, chartControl, IMAGE_LEGEND);
		createResetButton(composite);
		createColorCompensationButton(composite);
		createSettingsButton(composite);
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePlateChart.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

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

	private void createColorCompensationButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle Color Compensation");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_BAR_CHART, IApplicationImage.SIZE_16x16, colorCompensation));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				colorCompensation = !colorCompensation;
				button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_BAR_CHART, IApplicationImage.SIZE_16x16, colorCompensation));
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

		Combo combo = new Combo(parent, SWT.READ_ONLY);
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
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		chart.setLayoutData(gridData);
		chart.toggleSeriesLegendVisibility();
		//
		chartControl.set(chart);
	}

	private void updateChart() {

		/*
		 * Clear the chart and reset.
		 */
		chartControl.get().deleteSeries();
		if(plate != null) {
			ColorCodes colorCodes = new ColorCodes();
			colorCodes.load(preferenceStore.getString(PreferenceConstants.P_PCR_PLATE_COLOR_CODES));
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			//
			for(IWell well : plate.getWells()) {
				try {
					int channelNumber = comboChannels.getSelectionIndex();
					IChannel channel = well.getChannels().get(channelNumber);
					Color color = getWellColor(well, colorCodes);
					ILineSeriesData lineSeriesData = getLineSeriesData(plate, well, channel, color);
					if(lineSeriesData != null) {
						lineSeriesDataList.add(lineSeriesData);
					}
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
			}
			//
			chartControl.get().addSeriesData(lineSeriesDataList);
		}
	}

	private Color getWellColor(IWell well, ColorCodes colorCodes) {

		String sampleSubset = well.getSampleSubset();
		String targetName = well.getTargetName();
		if(colorCodes.containsKey(sampleSubset)) {
			return colorCodes.get(sampleSubset).getColor();
		} else if(colorCodes.containsKey(targetName)) {
			return colorCodes.get(targetName).getColor();
		} else {
			return Colors.getColor(preferenceStore.getString(PreferenceConstants.P_PCR_DEFAULT_COLOR));
		}
	}

	private ILineSeriesData getLineSeriesData(IPlate plate, IWell well, IChannel channel, Color color) {

		ILineSeriesData lineSeriesData = null;
		if(channel != null) {
			List<Double> pointList = colorCompensation ? channel.getColorCompensatedFluorescence() : channel.getFluorescence();
			double[] points = new double[pointList.size()];
			for(int index = 0; index < pointList.size(); index++) {
				points[index] = pointList.get(index);
			}
			//
			String position = Integer.toString(well.getPosition().getId() + 1);
			String seriesId = plate.getName() + " " + channel.getId() + " " + position;
			ISeriesData seriesData = new SeriesData(points, seriesId);
			lineSeriesData = new LineSeriesData(seriesData);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setVisibleInLegend(!well.getSampleSubset().isEmpty());
			lineSeriesSettings.setVisible(!well.getSampleSubset().isEmpty());
			lineSeriesSettings.setLineColor(color);
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettings.setDescription(well.getLabel());
			ISeriesSettings seriesSettingsDefault = SeriesMapper.getSeriesSettingsDefault(seriesId, chartControl.get());
			SeriesMapper.mapSetting(seriesId, lineSeriesSettings, seriesSettingsDefault);
		}
		return lineSeriesData;
	}
}
