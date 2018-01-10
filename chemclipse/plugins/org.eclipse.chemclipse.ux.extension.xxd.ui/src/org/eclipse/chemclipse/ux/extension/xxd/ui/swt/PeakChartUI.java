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
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.IAxis.Position;
import org.swtchart.ILineSeries;

public class PeakChartUI extends ScrollableChart {

	/*
	 * Condal-Bosch 15%, 85%
	 */
	private static final float HEIGHT_85 = 0.85f;
	private static final float HEIGHT_50 = 0.5f;
	private static final float HEIGHT_15 = 0.15f;
	private static final float HEIGHT_0 = 0.0f;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0");

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
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			//
			boolean includeBackground = true;
			boolean mirrored = false;
			//
			lineSeriesDataList.add(getPeak(peak, includeBackground, mirrored));
			lineSeriesDataList.add(getIncreasingTangent(peak, includeBackground, mirrored));
			lineSeriesDataList.add(getDecreasingTangent(peak, includeBackground, mirrored));
			lineSeriesDataList.add(getPeakPerpendicular(peak, includeBackground, mirrored));
			lineSeriesDataList.add(getPeakWidth(peak, includeBackground, HEIGHT_50, mirrored, Colors.BLACK));
			lineSeriesDataList.add(getPeakWidth(peak, includeBackground, HEIGHT_0, mirrored, Colors.BLACK));
			lineSeriesDataList.add(getPeakWidth(peak, includeBackground, HEIGHT_85, mirrored, Colors.RED));
			lineSeriesDataList.add(getPeakWidth(peak, includeBackground, HEIGHT_15, mirrored, Colors.RED));
			if(includeBackground) {
				lineSeriesDataList.add(getPeakBackground(peak, mirrored));
			}
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

	private ILineSeriesData getPeak(IPeak peak, boolean includeBackground, boolean mirrored) {

		ISeriesData seriesData = getPeakSeriesData(peak, includeBackground, mirrored);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setEnableArea(true);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		return lineSeriesData;
	}

	private ILineSeriesData getIncreasingTangent(IPeak peak, boolean includeBackground, boolean mirrored) {

		ISeriesData seriesData = getIncreasingInflectionData(peak, includeBackground, mirrored);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(Colors.BLACK);
		lineSeriesSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		return lineSeriesData;
	}

	private ILineSeriesData getDecreasingTangent(IPeak peak, boolean includeBackground, boolean mirrored) {

		ISeriesData seriesData = getDecreasingInflectionData(peak, includeBackground, mirrored);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(Colors.BLACK);
		lineSeriesSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		return lineSeriesData;
	}

	private ILineSeriesData getPeakPerpendicular(IPeak peak, boolean includeBackground, boolean mirrored) {

		ISeriesData seriesData = getPeakPerpendicularData(peak, includeBackground, mirrored);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(Colors.BLACK);
		lineSeriesSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		return lineSeriesData;
	}

	private ILineSeriesData getPeakWidth(IPeak peak, boolean includeBackground, float height, boolean mirrored, Color color) {

		ISeriesData seriesData = getPeakWidthByInflectionData(peak, includeBackground, height, mirrored);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		return lineSeriesData;
	}

	private ILineSeriesData getPeakBackground(IPeak peak, boolean mirrored) {

		ISeriesData seriesData = getPeakBackgroundData(peak, mirrored);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(Colors.BLACK);
		lineSeriesSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		return lineSeriesData;
	}

	private ISeriesData getPeakSeriesData(IPeak peak, boolean includeBackground, boolean mirrored) {

		String id = "Peak";
		IPeakModel peakModel = peak.getPeakModel();
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		int size = retentionTimes.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(int retentionTime : retentionTimes) {
			//
			xSeries[index] = retentionTime;
			if(includeBackground) {
				ySeries[index] = peakModel.getBackgroundAbundance(retentionTime) + peakModel.getPeakAbundance(retentionTime);
			} else {
				ySeries[index] = peakModel.getPeakAbundance(retentionTime);
			}
			//
			if(mirrored) {
				ySeries[index] = ySeries[index] * -1;
			}
			//
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getIncreasingInflectionData(IPeak peak, boolean includeBackground, boolean mirrored) {

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
				//
				if(mirrored) {
					ySeries[0] = ySeries[0] * -1;
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
				//
				if(mirrored) {
					ySeries[1] = ySeries[1] * -1;
				}
			} catch(SolverException e) {
				//
			}
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getDecreasingInflectionData(IPeak peak, boolean includeBackground, boolean mirrored) {

		String id = "Decreasing Tangent";
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
			try {
				IPoint intersection;
				LinearEquation increasing = peakModel.getIncreasingInflectionPointEquation();
				LinearEquation decreasing = peakModel.getDecreasingInflectionPointEquation();
				LinearEquation baseline = peakModel.getPercentageHeightBaselineEquation(0.0f);
				double x;
				/*
				 * Where does the decreasing tangent crosses the baseline.
				 */
				intersection = Equations.calculateIntersection(decreasing, baseline);
				/*
				 * Take a look if the retention time (X) is greater than the
				 * peaks retention time.<br/> If yes, take the peaks stop
				 * retention time, otherwise the values would be 0 by default.
				 */
				double stopRetentionTime = peakModel.getStopRetentionTime();
				x = intersection.getX() > stopRetentionTime ? stopRetentionTime : intersection.getX();
				xSeries[0] = intersection.getX();
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
				 * Take a look if the retention time (X) is lower than the peaks
				 * retention time.<br/> If yes, take the peaks start retention
				 * time, otherwise the values would be 0 by default.
				 */
				double startRetentionTime = peakModel.getStartRetentionTime();
				x = intersection.getX() < startRetentionTime ? startRetentionTime : intersection.getX();
				xSeries[1] = intersection.getX();
				if(includeBackground) {
					ySeries[1] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[1] = intersection.getY();
				}
			} catch(SolverException e) {
			}
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getPeakPerpendicularData(IPeak peak, boolean includeBackground, boolean mirrored) {

		String id = "Peak Perpendicular";
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
			xSeries[0] = peakModel.getRetentionTimeAtPeakMaximumByInflectionPoints();
			if(includeBackground) {
				ySeries[0] = peakModel.getBackgroundAbundance(peakModel.getRetentionTimeAtPeakMaximumByInflectionPoints());
			} else {
				ySeries[0] = 0.0d;
			}
			try {
				IPoint intersection = Equations.calculateIntersection(peakModel.getIncreasingInflectionPointEquation(), peakModel.getDecreasingInflectionPointEquation());
				/*
				 * Normally a check if the retention time x is outwards of peak
				 * range should not be performed as it must be in peaks
				 * retention time range, it's the maximum.
				 */
				// TODO einfach background includen geht nicht
				xSeries[1] = intersection.getX();
				if(includeBackground) {
					ySeries[1] = intersection.getY() + peakModel.getBackgroundAbundance((int)intersection.getX());
				} else {
					ySeries[1] = intersection.getY();
				}
			} catch(SolverException e) {
			}
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getPeakWidthByInflectionData(IPeak peak, boolean includeBackground, float height, boolean mirrored) {

		String id = "Peak Width";
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
			double x;
			LinearEquation percentageHeightBaseline = peakModel.getPercentageHeightBaselineEquation(height);
			if(percentageHeightBaseline != null) {
				try {
					IPoint p1 = Equations.calculateIntersection(peakModel.getIncreasingInflectionPointEquation(), percentageHeightBaseline);
					IPoint p2 = Equations.calculateIntersection(peakModel.getDecreasingInflectionPointEquation(), percentageHeightBaseline);
					/*
					 * Take a look if the retention time (X) is lower than the
					 * peaks retention time.<br/> If yes, take the peaks start
					 * retention time, otherwise the values would be 0 by
					 * default.
					 */
					double startRetentionTime = peakModel.getStartRetentionTime();
					x = p1.getX() < startRetentionTime ? startRetentionTime : p1.getX();
					xSeries[0] = p1.getX();
					/*
					 * Left intersection between increasing tangent and width at
					 * percentage height.
					 */
					if(includeBackground) {
						ySeries[0] = p1.getY() + peakModel.getBackgroundAbundance((int)x);
					} else {
						ySeries[0] = p1.getY();
					}
					/*
					 * Take a look if the retention time (X) is greater than the
					 * peaks retention time.<br/> If yes, take the peaks stop
					 * retention time, otherwise the values would be 0 by
					 * default.
					 */
					double stopRetentionTime = peakModel.getStopRetentionTime();
					x = p2.getX() > stopRetentionTime ? stopRetentionTime : p2.getX();
					xSeries[1] = p2.getX();
					/*
					 * Right intersection between increasing tangent and width
					 * at percentage height.
					 */
					if(includeBackground) {
						ySeries[1] = p2.getY() + peakModel.getBackgroundAbundance((int)x);
					} else {
						ySeries[1] = p2.getY();
					}
				} catch(SolverException e) {
				}
			}
			id = "Peak Width at " + decimalFormat.format(height * 100) + "%";
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getPeakBackgroundData(IPeak peak, boolean mirrored) {

		String id = "Peak Background";
		IPeakModel peakModel = peak.getPeakModel();
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		int size = retentionTimes.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		int index = 0;
		for(int retentionTime : peakModel.getRetentionTimes()) {
			xSeries[index] = retentionTime;
			ySeries[index] = peakModel.getBackgroundAbundance(retentionTime);
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}
}
