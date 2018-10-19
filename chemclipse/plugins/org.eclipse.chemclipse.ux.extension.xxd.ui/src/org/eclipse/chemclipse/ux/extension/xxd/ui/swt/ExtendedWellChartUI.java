/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartPCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePCR;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ExtendedWellChartUI {

	private static final Logger logger = Logger.getLogger(ExtendedWellChartUI.class);
	//
	private static final String ALL_CHANNELS = "Show all Channels";
	private static final String CHANNEL = "Channel ";
	//
	private Label labelInfo;
	private Composite toolbarInfo;
	private Combo comboChannels;
	private ChartPCR chartPCR;
	//
	private IWell well = null;

	@Inject
	public ExtendedWellChartUI(Composite parent) {
		initialize(parent);
	}

	public void update(IWell well) {

		this.well = well;
		if(well != null) {
			if(well.isEmptyMeasurement()) {
				labelInfo.setText("Position: " + (well.getPosition().getId() + 1));
				comboChannels.setItems(new String[]{});
				chartPCR.deleteSeries();
			} else {
				labelInfo.setText("Position: " + (well.getPosition().getId() + 1) + ", Id:" + well.getSampleId());
				comboChannels.setItems(getComboItems(well));
				comboChannels.select(0);
				updateChart();
			}
		} else {
			labelInfo.setText("No well data available.");
			comboChannels.setItems(getComboItems(well));
			chartPCR.deleteSeries();
		}
	}

	private String[] getComboItems(IWell well) {

		if(well != null) {
			List<Integer> channels = new ArrayList<>(well.getChannels().keySet());
			Collections.sort(channels);
			//
			List<String> items = new ArrayList<>();
			items.add(ALL_CHANNELS);
			for(int channel : channels) {
				items.add(CHANNEL + channel);
			}
			return items.toArray(new String[items.size()]);
		} else {
			return new String[]{};
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		comboChannels = createComboChannels(parent);
		chartPCR = createChart(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonToggleToolbarInfo(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartPCR.toggleSeriesLegendVisibility();
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

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePagePCR();
				preferencePage.setTitle("PCR");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						updateChart();
					} catch(Exception e1) {
						System.out.println(e1);
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
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

	private ChartPCR createChart(Composite parent) {

		ChartPCR chart = new ChartPCR(parent, SWT.NONE);
		chart.setLayoutData(new GridData(GridData.FILL_BOTH));
		return chart;
	}

	private void updateChart() {

		chartPCR.deleteSeries();
		IColorScheme colorScheme = Colors.getColorScheme(Colors.COLOR_SCHEME_PRINT);
		//
		if(well != null) {
			/*
			 * Extract the channels.
			 */
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			String channelSelection = comboChannels.getText().trim();
			if(channelSelection.equals(ALL_CHANNELS)) {
				for(IChannel channel : well.getChannels().values()) {
					ILineSeriesData lineSeriesData = extractChannel(channel, colorScheme.getColor());
					if(lineSeriesData != null) {
						lineSeriesDataList.add(lineSeriesData);
						colorScheme.incrementColor();
					}
				}
			} else {
				try {
					int channelNumber = Integer.parseInt(channelSelection.replaceAll(CHANNEL, ""));
					IChannel channel = well.getChannels().get(channelNumber);
					ILineSeriesData lineSeriesData = extractChannel(channel, colorScheme.getColor());
					if(lineSeriesData != null) {
						lineSeriesDataList.add(lineSeriesData);
						colorScheme.incrementColor();
					}
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
			}
			//
			chartPCR.addSeriesData(lineSeriesDataList);
		}
	}

	private ILineSeriesData extractChannel(IChannel channel, Color color) {

		ILineSeriesData lineSeriesData = null;
		if(channel != null) {
			List<Double> pointList = channel.getPoints();
			double[] points = new double[pointList.size()];
			for(int index = 0; index < pointList.size(); index++) {
				points[index] = pointList.get(index);
			}
			ISeriesData seriesData = new SeriesData(points, "Channel " + channel.getId());
			lineSeriesData = new LineSeriesData(seriesData);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setLineColor(color);
			lineSeriesSettings.setEnableArea(false);
		}
		return lineSeriesData;
	}
}
