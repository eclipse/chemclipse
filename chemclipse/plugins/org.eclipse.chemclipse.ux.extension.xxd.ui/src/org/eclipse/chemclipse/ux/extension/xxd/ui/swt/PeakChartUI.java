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
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.ChartType;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.exceptions.SeriesException;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;

public class PeakChartUI extends ScrollableChart {

	/*
	 * Condal-Bosch 15%, 85%
	 */
	private static final float HEIGHT_85 = 0.85f;
	private static final float HEIGHT_50 = 0.5f;
	private static final float HEIGHT_15 = 0.15f;
	private static final float HEIGHT_0 = 0.0f;
	//
	private PeakChartSupport peakChartSupport = new PeakChartSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public PeakChartUI() {

		super();
		modifyChart();
		setChartType(ChartType.LINE);
		initialize();
	}

	public PeakChartUI(Composite parent, int style) {

		super(parent, style);
		modifyChart();
		setChartType(ChartType.LINE);
		initialize();
	}

	public void setInput(IPeak peak) {

		prepareChart();
		if(peak != null) {
			//
			modifyChart(peak);
			List<ILineSeriesData> lineSeriesDataList = getPeakSeriesData(peak, false, "");
			addLineSeriesData(lineSeriesDataList);
		}
	}

	public void setInput(IPeak peak1, IPeak peak2) {

		prepareChart();
		if(peak1 != null) {
			//
			modifyChart(peak1);
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			lineSeriesDataList.addAll(getPeakSeriesData(peak1, false, "Peak1"));
			lineSeriesDataList.addAll(getPeakSeriesData(peak2, false, "Peak2"));
			addLineSeriesData(lineSeriesDataList);
		}
	}

	public void setInput(List<IPeak> peaks) {

		prepareChart();
		if(peaks != null && !peaks.isEmpty()) {
			modifyChart(peaks.get(0));
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			for(int i = 0; i < peaks.size(); i++) {
				IPeak peak = peaks.get(i);
				if(peak != null) {
					List<ILineSeriesData> lineSeriesDataListPeak = getPeakSeriesData(peak, false, Integer.toString((i + 1)));
					lineSeriesDataList.addAll(lineSeriesDataListPeak);
				}
			}
			addLineSeriesData(lineSeriesDataList);
		}
	}

	public void setDataType(IChartSettings chartSettings) {

		String titleX = preferenceStore.getString(PreferenceSupplier.P_TITLE_X_AXIS_MILLISECONDS);
		String titleX1 = preferenceStore.getString(PreferenceSupplier.P_TITLE_X_AXIS_MINUTES);
		String titleY = preferenceStore.getString(PreferenceSupplier.P_TITLE_Y_AXIS_INTENSITY);
		String titleY1 = preferenceStore.getString(PreferenceSupplier.P_TITLE_Y_AXIS_RELATIVE_INTENSITY);
		//
		ChartSupport.setPrimaryAxisSet(chartSettings, titleX, false, titleY);
		ChartSupport.clearSecondaryAxes(chartSettings);
		ChartSupport.addSecondaryAxisX(chartSettings, titleX1);
		ChartSupport.addSecondaryAxisY(chartSettings, titleY1);
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitleVisible(false);
	}

	private List<ILineSeriesData> getPeakSeriesData(IPeak peak, boolean mirrored, String postfix) {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		/*
		 * Peak
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_PEAK)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_PEAK_1));
			lineSeriesDataList.add(peakChartSupport.getPeak(peak, false, mirrored, color, "Peak" + postfix));
		}
		/*
		 * Tangents
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_PEAK_TANGENTS)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_PEAK_TANGENTS));
			lineSeriesDataList.add(peakChartSupport.getIncreasingTangent(peak, false, mirrored, color, postfix));
			lineSeriesDataList.add(peakChartSupport.getDecreasingTangent(peak, false, mirrored, color, postfix));
		}
		/*
		 * Width 50%
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_PEAK_WIDTH_0)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_PEAK_WIDTH_0));
			lineSeriesDataList.add(peakChartSupport.getPeakWidth(peak, false, HEIGHT_0, mirrored, color, postfix));
		}
		/*
		 * Width 0%
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_PEAK_WIDTH_50)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_PEAK_WIDTH_50));
			lineSeriesDataList.add(peakChartSupport.getPeakWidth(peak, false, HEIGHT_50, mirrored, color, postfix));
		}
		/*
		 * Width Condal-Bosh
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_PEAK_WIDTH_CONDAL_BOSH)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_PEAK_WIDTH_CONDAL_BOSH));
			lineSeriesDataList.add(peakChartSupport.getPeakWidth(peak, false, HEIGHT_85, mirrored, color, postfix));
			lineSeriesDataList.add(peakChartSupport.getPeakWidth(peak, false, HEIGHT_15, mirrored, color, postfix));
		}
		//
		return lineSeriesDataList;
	}

	private void prepareChart() {

		deleteSeries();
	}

	private void modifyChart() {

		/*
		 * Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setRestrictFrame(true);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMinY(0.0d);
		rangeRestriction.setExtendMaxY(0.1d);
		//
		applySettings(chartSettings);
	}

	private void modifyChart(IPeak peak) {

		IChartSettings chartSettings = getChartSettings();
		//
		if(peak instanceof IPeakMSD) {
			setDataType(chartSettings);
		} else if(peak instanceof IPeakCSD) {
			setDataType(chartSettings);
		} else if(peak instanceof IPeakWSD) {
			setDataType(chartSettings);
		}
		//
		applySettings(chartSettings);
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
		if(lineSeriesDataList != null && !lineSeriesDataList.isEmpty()) {
			BaseChart baseChart = getBaseChart();
			baseChart.suspendUpdate(true);
			for(ILineSeriesData lineSeriesData : lineSeriesDataList) {
				/*
				 * Get the series data and apply the settings.
				 */
				try {
					ISeriesData seriesData = lineSeriesData.getSeriesData();
					ISeriesData optimizedSeriesData = calculateSeries(seriesData);
					ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
					lineSeriesSettings.getSeriesSettingsHighlight(); // Initialize
					ILineSeries<?> lineSeries = (ILineSeries<?>)createSeries(optimizedSeriesData, lineSeriesSettings);
					baseChart.applySeriesSettings(lineSeries, lineSeriesSettings);
				} catch(SeriesException e) {
					//
				}
			}
			baseChart.suspendUpdate(false);
			adjustRange(true);
			baseChart.redraw();
		}
	}
}