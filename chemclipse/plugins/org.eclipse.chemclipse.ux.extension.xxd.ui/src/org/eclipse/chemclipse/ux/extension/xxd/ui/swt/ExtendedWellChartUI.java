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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartPCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCodes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePCR;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedWellChartUI {

	private static final Logger logger = Logger.getLogger(ExtendedWellChartUI.class);
	//
	private Label labelInfo;
	private Composite toolbarInfo;
	private Combo comboChannels;
	private ChartPCR chartPCR;
	//
	private IWell well = null;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	@Inject
	public ExtendedWellChartUI(Composite parent) {
		initialize(parent);
	}

	public void update(IWell well) {

		this.well = well;
		updateLabel();
		updateChannelCombo();
		updateChartData();
	}

	private void updateLabel() {

		if(well != null) {
			labelInfo.setText(well.getLabel());
		} else {
			labelInfo.setText("No well data available.");
		}
	}

	private void updateChannelCombo() {

		if(well != null) {
			if(well.isEmptyMeasurement()) {
				comboChannels.setItems(new String[]{});
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
			comboChannels.setItems(new String[]{""});
		}
	}

	private void updateChartData() {

		chartPCR.deleteSeries();
		if(well != null) {
			if(!well.isEmptyMeasurement()) {
				updateChart();
			}
		}
	}

	private String[] getComboItems(IWell well) {

		if(well != null) {
			List<String> items = new ArrayList<>();
			items.add(IPlate.ALL_CHANNELS);
			for(IChannel channel : well.getChannels().values()) {
				items.add(channel.getDetectionName());
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
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						updateChart();
					} catch(Exception e1) {
						System.out.println(e1);
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
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

		/*
		 * Clear the chart and reset.
		 */
		chartPCR.deleteSeries();
		//
		if(well != null) {
			/*
			 * Extract the channels.
			 */
			ColorCodes colorCodes = new ColorCodes();
			colorCodes.load(preferenceStore.getString(PreferenceConstants.P_PCR_COLOR_CODES));
			Color color = getWellColor(well, colorCodes);
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			int index = comboChannels.getSelectionIndex();
			if(index == 0) {
				for(IChannel channel : well.getChannels().values()) {
					addChannelData(channel, lineSeriesDataList, color);
				}
			} else {
				try {
					IChannel channel = well.getChannels().get(index - 1);
					addChannelData(channel, lineSeriesDataList, color);
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
			}
			//
			chartPCR.addSeriesData(lineSeriesDataList);
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

	private void addChannelData(IChannel channel, List<ILineSeriesData> lineSeriesDataList, Color color) {

		ILineSeriesData channelCurve = getChannelCurve(channel, color);
		if(channelCurve != null) {
			lineSeriesDataList.add(channelCurve);
		}
		//
		ILineSeriesData crossingPoint = getCrossingPoint(channel, color);
		if(crossingPoint != null) {
			lineSeriesDataList.add(crossingPoint);
		}
	}

	private ILineSeriesData getChannelCurve(IChannel channel, Color color) {

		ILineSeriesData lineSeriesData = null;
		if(channel != null) {
			List<Double> pointList = channel.getPoints();
			double[] points = new double[pointList.size()];
			for(int index = 0; index < pointList.size(); index++) {
				points[index] = pointList.get(index);
			}
			ISeriesData seriesData = new SeriesData(points, "Channel " + channel.getId());
			lineSeriesData = new LineSeriesData(seriesData);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineColor(color);
			lineSeriesSettings.setSymbolColor(color);
			lineSeriesSettings.setSymbolSize(2);
			lineSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
			lineSeriesSettings.setEnableArea(false);
		}
		return lineSeriesData;
	}

	private ILineSeriesData getCrossingPoint(IChannel channel, Color color) {

		ILineSeriesData lineSeriesData = null;
		if(channel != null) {
			IPoint crossingPoint = channel.getCrossingPoint();
			if(crossingPoint != null) {
				double[] xSeries = new double[]{crossingPoint.getX()};
				double[] ySeries = new double[]{crossingPoint.getY()};
				ISeriesData seriesData = new SeriesData(xSeries, ySeries, "Crossing Point " + channel.getId());
				lineSeriesData = new LineSeriesData(seriesData);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setSymbolColor(color);
				lineSeriesSettings.setSymbolSize(8);
				lineSeriesSettings.setSymbolType(PlotSymbolType.CROSS);
				lineSeriesSettings.setEnableArea(false);
			}
		}
		return lineSeriesData;
	}
}
