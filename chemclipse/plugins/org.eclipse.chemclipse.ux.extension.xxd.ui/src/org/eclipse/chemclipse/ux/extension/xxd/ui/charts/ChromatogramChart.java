/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToSecondsConverter;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

public class ChromatogramChart extends LineChart {

	private static final String TITLE_X_AXIS_MILLISECONDS = "Retention Time (milliseconds)";
	private static final String TITLE_X_AXIS_SECONDS = "Seconds";
	private static final String TITLE_X_AXIS_MINUTES = "Minutes";
	private static final String TITLE_Y_AXIS_INTENSITY = "Intensity";
	private static final String TITLE_Y_AXIS_RELATIVE_INTENSITY = "Relative Intensity [%]";
	//
	private ChartSupport chartSupport = new ChartSupport();

	public ChromatogramChart() {
		super();
		initialize();
	}

	public ChromatogramChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	/**
	 * Modifies the x and y axis set given in accordance to the given settings.
	 */
	public void modifyAxes(boolean applySettings) {

		modifyXAxisMilliseconds();
		modifyYAxisIntensity();
		modifyXAxisSeconds();
		modifyXAxisMinutes();
		modifyYAxisRelativeIntensity();
		//
		if(applySettings) {
			IChartSettings chartSettings = getChartSettings();
			applySettings(chartSettings);
		}
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		//
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(true);
		chartSettings.getRangeRestriction().setZeroY(true);
		//
		modifyAxes(true);
	}

	private void modifyXAxisMilliseconds() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(TITLE_X_AXIS_MILLISECONDS);
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS;
		String pattern = "0.0##";
		String colorNode = PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS;
		//
		chartSupport.setAxisSettings(primaryAxisSettingsX, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
		primaryAxisSettingsX.setVisible(chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS));
		primaryAxisSettingsX.setTitleVisible(chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MILLISECONDS));
	}

	private void modifyYAxisIntensity() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(TITLE_Y_AXIS_INTENSITY);
		//
		String positionNode = PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY;
		String pattern = "0.0#E0";
		String colorNode = PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY;
		//
		chartSupport.setAxisSettings(primaryAxisSettingsY, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
		primaryAxisSettingsY.setVisible(chartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY));
		primaryAxisSettingsY.setTitleVisible(chartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_INTENSITY));
	}

	private void modifyYAxisRelativeIntensity() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsY(TITLE_Y_AXIS_RELATIVE_INTENSITY, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY;
		String pattern = "0.00";
		String colorNode = PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY);
		boolean isShowAxisTitle = chartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY);
		//
		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(TITLE_Y_AXIS_RELATIVE_INTENSITY, new PercentageConverter(SWT.VERTICAL, true));
				chartSupport.setAxisSettings(secondaryAxisSettingsY, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsY.setTitleVisible(isShowAxisTitle);
				chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
			} else {
				chartSupport.setAxisSettings(axisSettings, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				axisSettings.setVisible(true);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setVisible(false);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		}
	}

	private void modifyXAxisSeconds() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsX(TITLE_X_AXIS_SECONDS, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_SECONDS;
		String pattern = "0.00";
		String colorNode = PreferenceConstants.P_COLOR_X_AXIS_SECONDS;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_SECONDS);
		boolean isShowAxisTitle = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SECONDS);
		//
		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_SECONDS, new MillisecondsToSecondsConverter());
				chartSupport.setAxisSettings(secondaryAxisSettingsX, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsX.setTitleVisible(isShowAxisTitle);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				chartSupport.setAxisSettings(axisSettings, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				axisSettings.setVisible(true);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setVisible(false);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		}
	}

	private void modifyXAxisMinutes() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsX(TITLE_X_AXIS_MINUTES, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_MINUTES;
		String pattern = "0.00";
		String colorNode = PreferenceConstants.P_COLOR_X_AXIS_MINUTES;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_MINUTES);
		boolean isShowAxisTitle = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MINUTES);
		//
		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_MINUTES, new MillisecondsToMinuteConverter());
				chartSupport.setAxisSettings(secondaryAxisSettingsX, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsX.setTitleVisible(isShowAxisTitle);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				chartSupport.setAxisSettings(axisSettings, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				axisSettings.setVisible(true);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setVisible(false);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		}
	}
}
