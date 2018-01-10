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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.SolverException;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.eavp.service.swtchart.axisconverter.RelativeIntensityConverter;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IPrimaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.RangeRestriction;
import org.eclipse.eavp.service.swtchart.core.ScrollableChart;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.exceptions.SeriesException;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.IAxis.Position;
import org.swtchart.ILineSeries;

public class PeakChartUI extends ScrollableChart {

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
			//
			ISeriesData seriesData;
			ILineSeriesData lineSeriesData;
			ILineSeriesSettings lineSeriesSettings;
			ILineSeriesSettings lineSeriesSettingsHighlight;
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			boolean includeBackground = true;
			/*
			 * Peak
			 */
			seriesData = getPeakSeriesData(peak, includeBackground);
			lineSeriesData = new LineSeriesData(seriesData);
			lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setEnableArea(true);
			lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
			lineSeriesSettingsHighlight.setLineWidth(2);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Increasing Tangent
			 */
			seriesData = getIncreasingInflectionPoints(peak, includeBackground, Sign.POSITIVE);
			lineSeriesData = new LineSeriesData(seriesData);
			lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setLineColor(Colors.BLACK);
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
			lineSeriesSettingsHighlight.setLineWidth(2);
			lineSeriesDataList.add(lineSeriesData);
			//
			addLineSeriesData(lineSeriesDataList);
		}
	}

	public void setInput(IPeak peak1, IPeak peak2, boolean mirrored) {

		prepareChart();
		if(peak1 != null) {
		}
	}

	private void prepareChart() {

		deleteSeries();
	}

	private void modifyChart() {

		/*
		 * Preferences
		 */
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		// String name = preferenceStore.getString(PreferenceConstants.P_SCAN_LABEL_FONT_NAME);
		/*
		 * Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setRestrictZoom(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.ABSOLUTE);
		rangeRestriction.setExtendMinX(2.0d);
		rangeRestriction.setExtendMaxX(2.0d);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMinY(0.0d);
		rangeRestriction.setExtendMaxY(0.25d);
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
		primaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setVisible(xAxisVisible);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(yAxisTitle);
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	}

	private void addSecondaryAxisX(IChartSettings chartSettings, String xAxisTitle) {

		ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(xAxisTitle, new MillisecondsToMinuteConverter());
		secondaryAxisSettingsX.setPosition(Position.Primary);
		secondaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
	}

	private void addSecondaryAxisY(IChartSettings chartSettings, String yAxisTitle) {

		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(yAxisTitle, new RelativeIntensityConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
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
					ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
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

	private ISeriesData getPeakSeriesData(IPeak peak, boolean includeBackground) {

		String id = "Peak";
		IPeakModel peakModel = peak.getPeakModel();
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		int size = retentionTimes.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(int retentionTime : retentionTimes) {
			xSeries[index] = retentionTime;
			if(includeBackground) {
				ySeries[index] = peakModel.getBackgroundAbundance(retentionTime) + peakModel.getIntensity(retentionTime);
			} else {
				ySeries[index] = peakModel.getIntensity(retentionTime);
			}
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getIncreasingInflectionPoints(IPeak peak, boolean includeBackground, Sign sign) {

		String id = "Increasing Tangent";
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
			try {
				LinearEquation increasing = peakModel.getIncreasingInflectionPointEquation();
				LinearEquation decreasing = peakModel.getDecreasingInflectionPointEquation();
				LinearEquation baseline = peakModel.getPercentageHeightBaselineEquation(0.0f);
				/*
				 * Where does the increasing tangent crosses the baseline.
				 */
				IPoint intersection = Equations.calculateIntersection(increasing, baseline);
				double x;
				/*
				 * Take a look if the retention time (X) is lower than the peaks
				 * retention time.<br/> If yes, take the peaks start retention
				 * time, otherwise the values would be 0 by default.
				 */
				double startRetentionTime = peakModel.getStartRetentionTime();
				x = intersection.getX() < startRetentionTime ? startRetentionTime : intersection.getX();
				xSeries[0] = x;
				//
				if(includeBackground) {
					ySeries[0] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[0] = intersection.getY();
				}
				/*
				 * This is the highest point of the peak, given by the tangents.
				 */
				intersection = Equations.calculateIntersection(increasing, decreasing);
				/*
				 * Take a look if the retention time (X) is greater than the
				 * peaks retention time.<br/> If yes, take the peaks stop
				 * retention time, otherwise the values would be 0 by default.
				 */
				double stopRetentionTime = peakModel.getStopRetentionTime();
				x = intersection.getX() > stopRetentionTime ? stopRetentionTime : intersection.getX();
				xSeries[1] = intersection.getX();
				//
				if(includeBackground) {
					ySeries[1] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[1] = intersection.getY();
				}
			} catch(SolverException e) {
				//
			}
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}
}
