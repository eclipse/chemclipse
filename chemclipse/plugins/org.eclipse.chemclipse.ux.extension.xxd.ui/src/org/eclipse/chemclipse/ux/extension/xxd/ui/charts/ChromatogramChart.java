/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToSecondsConverter;
import org.eclipse.eavp.service.swtchart.axisconverter.RelativeIntensityConverter;
import org.eclipse.eavp.service.swtchart.core.IAxisSettings;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IPrimaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IAxis.Position;
import org.swtchart.LineStyle;

public class ChromatogramChart extends LineChart {

	private static final String TITLE_X_AXIS_MILLISECONDS = "Retention Time (milliseconds)";
	private static final String TITLE_X_AXIS_SECONDS = "Seconds";
	private static final String TITLE_X_AXIS_MINUTES = "Minutes";
	private static final String TITLE_Y_AXIS_INTENSITY = "Intensity";
	private static final String TITLE_Y_AXIS_RELATIVE_INTENSITY = "Relative Intensity [%]";
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

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
	public void modifyAxisSet(boolean applySettings) {

		modifyMillisecondsXAxis();
		modifyIntensityYAxis();
		modifySecondsXAxis();
		modifyMinutesXAxis();
		modifyRelativeIntensityYAxis();
		//
		if(applySettings) {
			IChartSettings chartSettings = getChartSettings();
			applySettings(chartSettings);
		}
	}

	public void setAxisSettings(IAxisSettings axisSettings, Position position, String decimalPattern, Color color, LineStyle gridLineStyle, Color gridColor) {

		if(axisSettings != null) {
			axisSettings.setPosition(position);
			axisSettings.setDecimalFormat(ValueFormat.getDecimalFormatEnglish(decimalPattern));
			axisSettings.setColor(color);
			axisSettings.setGridColor(gridColor);
			axisSettings.setGridLineStyle(gridLineStyle);
		}
	}

	public ISecondaryAxisSettings getSecondaryAxisSettingsX(String title) {

		IChartSettings chartSettings = getChartSettings();
		return getSecondaryAxisSettings(chartSettings.getSecondaryAxisSettingsListX(), title);
	}

	public ISecondaryAxisSettings getSecondaryAxisSettingsY(String title) {

		IChartSettings chartSettings = getChartSettings();
		return getSecondaryAxisSettings(chartSettings.getSecondaryAxisSettingsListY(), title);
	}

	private void initialize() {

		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(true);
		chartSettings.getRangeRestriction().setZeroY(true);
		modifyAxisSet(true);
	}

	private void modifyMillisecondsXAxis() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(TITLE_X_AXIS_MILLISECONDS);
		setAxisSettings(primaryAxisSettingsX, PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS, "0.0##", PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS, PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS, PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS);
		primaryAxisSettingsX.setVisible(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS));
	}

	private void modifyIntensityYAxis() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(TITLE_Y_AXIS_INTENSITY);
		setAxisSettings(primaryAxisSettingsY, PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY, "0.0#E0", PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY, PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY, PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY);
		primaryAxisSettingsY.setVisible(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY));
	}

	private void modifyRelativeIntensityYAxis() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = getSecondaryAxisSettingsY(TITLE_Y_AXIS_RELATIVE_INTENSITY);
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY)) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(TITLE_Y_AXIS_RELATIVE_INTENSITY, new RelativeIntensityConverter(SWT.VERTICAL, true));
				setAxisSettings(secondaryAxisSettingsY, PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY, "0.00", PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY);
				chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
			} else {
				setAxisSettings(axisSettings, PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY, "0.00", PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY);
				axisSettings.setVisible(true);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setVisible(false);
			}
		}
	}

	private void modifySecondsXAxis() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = getSecondaryAxisSettingsX(TITLE_X_AXIS_SECONDS);
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_SECONDS)) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_SECONDS, new MillisecondsToSecondsConverter());
				setAxisSettings(secondaryAxisSettingsX, PreferenceConstants.P_POSITION_X_AXIS_SECONDS, "0.00", PreferenceConstants.P_COLOR_X_AXIS_SECONDS, PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS, PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				setAxisSettings(axisSettings, PreferenceConstants.P_POSITION_X_AXIS_SECONDS, "0.00", PreferenceConstants.P_COLOR_X_AXIS_SECONDS, PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS, PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS);
				axisSettings.setVisible(true);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setVisible(false);
			}
		}
	}

	private void modifyMinutesXAxis() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = getSecondaryAxisSettingsX(TITLE_X_AXIS_MINUTES);
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_MINUTES)) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_MINUTES, new MillisecondsToMinuteConverter());
				setAxisSettings(secondaryAxisSettingsX, PreferenceConstants.P_POSITION_X_AXIS_MINUTES, "0.00", PreferenceConstants.P_COLOR_X_AXIS_MINUTES, PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES, PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				setAxisSettings(axisSettings, PreferenceConstants.P_POSITION_X_AXIS_MINUTES, "0.00", PreferenceConstants.P_COLOR_X_AXIS_MINUTES, PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES, PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES);
				axisSettings.setVisible(true);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setVisible(false);
			}
		}
	}

	private ISecondaryAxisSettings getSecondaryAxisSettings(List<ISecondaryAxisSettings> secondaryAxisSettingsList, String title) {

		ISecondaryAxisSettings secondaryAxisSettings = null;
		//
		for(ISecondaryAxisSettings secondaryAxisSetting : secondaryAxisSettingsList) {
			if(title.equals(secondaryAxisSetting.getTitle())) {
				secondaryAxisSettings = secondaryAxisSetting;
			}
		}
		//
		return secondaryAxisSettings;
	}

	public void setAxisSettings(IAxisSettings axisSettings, String positionNode, String pattern, String colorNode, String gridLineStyleNode, String gridColorNode) {

		Position position = Position.valueOf(preferenceStore.getString(positionNode));
		Color color = Colors.getColor(preferenceStore.getString(colorNode));
		LineStyle gridLineStyle = LineStyle.valueOf(preferenceStore.getString(gridLineStyleNode));
		Color gridColor = Colors.getColor(preferenceStore.getString(gridColorNode));
		setAxisSettings(axisSettings, position, pattern, color, gridLineStyle, gridColor);
	}
}
