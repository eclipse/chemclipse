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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
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

	public PeakChartUI() {
		super();
		modifyChart();
	}

	public PeakChartUI(Composite parent, int style) {
		super(parent, style);
		modifyChart();
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
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			lineSeriesDataList.addAll(getPeakSeriesData(peak1, false, "Peak1"));
			lineSeriesDataList.addAll(getPeakSeriesData(peak2, false, "Peak2"));
			addLineSeriesData(lineSeriesDataList);
		}
	}

	public void setInput(List<IPeak> peaks) {

		prepareChart();
		if(peaks != null && peaks.size() >= 1) {
			//
			modifyChart(peaks.get(0));
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
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

	private List<ILineSeriesData> getPeakSeriesData(IPeak peak, boolean mirrored, String postfix) {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean includeBackground = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAK_BACKGROUND);
		/*
		 * Peak
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAK)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_1));
			lineSeriesDataList.add(peakChartSupport.getPeak(peak, includeBackground, mirrored, color, "Peak" + postfix));
		}
		/*
		 * Tangents
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAK_TANGENTS)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_TANGENTS));
			lineSeriesDataList.add(peakChartSupport.getIncreasingTangent(peak, includeBackground, mirrored, color, postfix));
			lineSeriesDataList.add(peakChartSupport.getDecreasingTangent(peak, includeBackground, mirrored, color, postfix));
			if(!includeBackground) {
				lineSeriesDataList.add(peakChartSupport.getPeakPerpendicular(peak, includeBackground, mirrored, color, postfix));
			}
		}
		/*
		 * Width 50%
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAK_WIDTH_0)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_WIDTH_0));
			lineSeriesDataList.add(peakChartSupport.getPeakWidth(peak, includeBackground, HEIGHT_0, mirrored, color, postfix));
		}
		/*
		 * Width 0%
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAK_WIDTH_50)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_WIDTH_50));
			lineSeriesDataList.add(peakChartSupport.getPeakWidth(peak, includeBackground, HEIGHT_50, mirrored, color, postfix));
		}
		/*
		 * Width Condal-Bosh
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAK_WIDTH_CONDAL_BOSH)) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_WIDTH_CONDAL_BOSH));
			lineSeriesDataList.add(peakChartSupport.getPeakWidth(peak, includeBackground, HEIGHT_85, mirrored, color, postfix));
			lineSeriesDataList.add(peakChartSupport.getPeakWidth(peak, includeBackground, HEIGHT_15, mirrored, color, postfix));
		}
		/*
		 * Include Baseline
		 */
		if(includeBackground) {
			if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAK_BASELINE)) {
				Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_BASELINE));
				lineSeriesDataList.add(peakChartSupport.getPeakBaseline(peak, mirrored, color, "Peak Baseline" + postfix));
			}
			//
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_BACKGROUND));
			lineSeriesDataList.add(peakChartSupport.getPeakBackground(peak, mirrored, color, "Peak Background" + postfix));
		}
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
		rangeRestriction.setRestrictZoom(true);
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
			setDataTypeMSD(chartSettings);
		} else if(peak instanceof IPeakCSD) {
			setDataTypeCSD(chartSettings);
		} else if(peak instanceof IPeakWSD) {
			setDataTypeWSD(chartSettings);
		}
		//
		applySettings(chartSettings);
	}

	private void setDataTypeMSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "Retention Time [ms]", false, "Intensity");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisX(chartSettings, "Minutes");
		addSecondaryAxisY(chartSettings, "Intensity [%]");
	}

	private void setDataTypeCSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "Retention Time [ms]", false, "Current");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisX(chartSettings, "Minutes");
		addSecondaryAxisY(chartSettings, "Current [%]");
	}

	private void setDataTypeWSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "Retention Time [ms]", false, "Intensity");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisX(chartSettings, "Minutes");
		addSecondaryAxisY(chartSettings, "Intensity [%]");
	}

	private void clearSecondaryAxes(IChartSettings chartSettings) {

		chartSettings.getSecondaryAxisSettingsListX().clear();
		chartSettings.getSecondaryAxisSettingsListY().clear();
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings, String xAxisTitle, boolean xAxisVisible, String yAxisTitle) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(xAxisTitle);
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setVisible(xAxisVisible);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(yAxisTitle);
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
	}

	private void addSecondaryAxisX(IChartSettings chartSettings, String xAxisTitle) {

		ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(xAxisTitle, new MillisecondsToMinuteConverter());
		secondaryAxisSettingsX.setPosition(Position.Primary);
		secondaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
	}

	private void addSecondaryAxisY(IChartSettings chartSettings, String yAxisTitle) {

		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(yAxisTitle, new PercentageConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
		if(lineSeriesDataList != null && lineSeriesDataList.size() > 0) {
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
					ILineSeries lineSeries = (ILineSeries)createSeries(optimizedSeriesData, lineSeriesSettings);
					baseChart.applyLineSeriesSettings(lineSeries, lineSeriesSettings);
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
