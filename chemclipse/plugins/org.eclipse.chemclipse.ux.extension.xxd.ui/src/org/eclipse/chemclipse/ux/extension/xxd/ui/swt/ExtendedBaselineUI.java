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

import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageBaseline;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.jface.preference.IPreferenceStore;
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

public class ExtendedBaselineUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<ChromatogramChart> chartControl = new AtomicReference<>();
	private IChromatogram<?> chromatogram;
	//
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IColorScheme colorScheme = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_BASELINE));
	private String compressionType = preferenceStore.getString(PreferenceConstants.DEF_BASELINE_CHART_COMPRESSION_TYPE);

	public ExtendedBaselineUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IChromatogramSelection<?, ?> chromatogramSelection) {

		toolbarInfo.get().setText(ChromatogramDataSupport.getChromatogramSelectionLabel(chromatogramSelection));
		//
		if(chromatogramSelection != null) {
			chromatogram = chromatogramSelection.getChromatogram();
		} else {
			chromatogram = null;
		}
		//
		refreshBaselineChart(chromatogram);
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createOverlayChart(this);
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
		composite.setLayout(new GridLayout(4, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		createButtonToggleChartLegend(composite, chartControl, IMAGE_LEGEND);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				refreshBaselineChart(chromatogram);
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageBaseline.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createOverlayChart(Composite parent) {

		ChromatogramChart chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chromatogramChart.applySettings(chartSettings);
		//
		chartControl.set(chromatogramChart);
	}

	private void applySettings() {

		colorScheme = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_BASELINE));
		compressionType = preferenceStore.getString(PreferenceConstants.DEF_BASELINE_CHART_COMPRESSION_TYPE);
		refreshBaselineChart(chromatogram);
	}

	private void refreshBaselineChart(IChromatogram<? extends IPeak> chromatogram) {

		ChromatogramChart chromatogramChart = chartControl.get();
		chromatogramChart.deleteSeries();
		if(chromatogram != null) {
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			colorScheme.reset();
			//
			ILineSeriesData lineSeriesDataChromatogram = getLineSeriesData(chromatogram, "Chromatogram", false);
			ILineSeriesSettings lineSeriesSettingsChromatogram = lineSeriesDataChromatogram.getSettings();
			lineSeriesSettingsChromatogram.setLineColor(colorScheme.getColor());
			lineSeriesSettingsChromatogram.setEnableArea(false);
			lineSeriesDataList.add(lineSeriesDataChromatogram);
			//
			ILineSeriesData lineSeriesDataBaseline = getLineSeriesData(chromatogram, "Baseline", true);
			ILineSeriesSettings lineSeriesSettingsBaseline = lineSeriesDataBaseline.getSettings();
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
				ySeries[index] = (baseline) ? baselineModel.getBackground(retentionTime) : scan.getTotalSignal();
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
