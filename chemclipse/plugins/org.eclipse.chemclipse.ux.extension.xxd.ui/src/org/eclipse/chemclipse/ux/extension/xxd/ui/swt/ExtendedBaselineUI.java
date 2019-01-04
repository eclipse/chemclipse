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
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageBaseline;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedBaselineUI {

	private ChromatogramChart chromatogramChart;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IColorScheme colorScheme = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_BASELINE));
	private String compressionType = preferenceStore.getString(PreferenceConstants.DEF_BASELINE_CHART_COMPRESSION_TYPE);
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	@SuppressWarnings("rawtypes")
	private IChromatogram chromatogram;

	@Inject
	public ExtendedBaselineUI(Composite parent) {
		initialize(parent);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void update(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null) {
			chromatogram = chromatogramSelection.getChromatogram();
		} else {
			chromatogram = null;
		}
		refreshBaselineChart(chromatogram);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createOverlayChart(parent);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {

				refreshBaselineChart(chromatogram);
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

				IPreferencePage preferencePage = new PreferencePageBaseline();
				preferencePage.setTitle("Baseline");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applyOverlaySettings();
					} catch(Exception e1) {
						System.out.println(e1);
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void createOverlayChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chromatogramChart.applySettings(chartSettings);
	}

	@SuppressWarnings("unchecked")
	private void applyOverlaySettings() {

		colorScheme = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_BASELINE));
		compressionType = preferenceStore.getString(PreferenceConstants.DEF_BASELINE_CHART_COMPRESSION_TYPE);
		refreshBaselineChart(chromatogram);
	}

	private void refreshBaselineChart(IChromatogram<? extends IPeak> chromatogram) {

		chromatogramChart.deleteSeries();
		if(chromatogram != null) {
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			colorScheme.reset();
			//
			ILineSeriesData lineSeriesDataChromatogram = getLineSeriesData(chromatogram, "Chromatogram", false);
			ILineSeriesSettings lineSeriesSettingsChromatogram = lineSeriesDataChromatogram.getLineSeriesSettings();
			lineSeriesSettingsChromatogram.setLineColor(colorScheme.getColor());
			lineSeriesSettingsChromatogram.setEnableArea(false);
			lineSeriesDataList.add(lineSeriesDataChromatogram);
			//
			ILineSeriesData lineSeriesDataBaseline = getLineSeriesData(chromatogram, "Baseline", true);
			ILineSeriesSettings lineSeriesSettingsBaseline = lineSeriesDataBaseline.getLineSeriesSettings();
			lineSeriesSettingsBaseline.setLineColor(colorScheme.getNextColor());
			lineSeriesSettingsBaseline.setEnableArea(false);
			lineSeriesDataList.add(lineSeriesDataBaseline);
			//
			int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
			chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
		}
	}

	private ILineSeriesData getLineSeriesData(IChromatogram<? extends IPeak> chromatogram, String id, boolean baseline) {

		ISeriesData seriesData = getSeriesDataProcessed(chromatogram, id, baseline);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		return lineSeriesData;
	}

	private ISeriesData getSeriesDataProcessed(IChromatogram<? extends IPeak> chromatogram, String id, boolean baseline) {

		double[] xSeries;
		double[] ySeries;
		//
		if(chromatogram != null) {
			IBaselineModel baselineModel = chromatogram.getBaselineModel();
			int size = chromatogram.getNumberOfScans();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IScan scan : chromatogram.getScans()) {
				int retentionTime = scan.getRetentionTime();
				xSeries[index] = retentionTime;
				ySeries[index] = (baseline) ? baselineModel.getBackgroundAbundance(retentionTime) : scan.getTotalSignal();
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
