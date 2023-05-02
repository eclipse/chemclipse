/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support usage of custom preference store
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToSecondsConverter;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

public class ChromatogramChart extends LineChart {

	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private String titleSeconds = "";
	private String titleMinutes = "";
	private String titleRelativeIntensity = "";

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

		/*
		 * Adjust Title, Format, ...
		 */
		adjustAxisMilliseconds();
		adjustAxisIntensity();
		adjustAxisSeconds();
		adjustAxisMinutes();
		adjustAxisRelativeIntensity();
		//
		if(applySettings) {
			IChartSettings chartSettings = getChartSettings();
			applySettings(chartSettings);
		}
	}

	private void initialize() {

		/*
		 * Initialize secondary axis titles.
		 */
		titleSeconds = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_SECONDS);
		titleMinutes = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MINUTES);
		titleRelativeIntensity = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_RELATIVE_INTENSITY);
		//
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitle("");
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		// Use background from StyleSheet.
		chartSettings.setBackground(null);
		chartSettings.setBackgroundChart(null);
		chartSettings.setBackgroundPlotArea(null);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(true);
		rangeRestriction.setZeroY(true);
		rangeRestriction.setReferenceZoomZeroX(false);
		rangeRestriction.setReferenceZoomZeroY(true);
		rangeRestriction.setRestrictZoomX(false);
		rangeRestriction.setRestrictZoomY(true);
		//
		modifyAxes(true);
	}

	private void adjustAxisMilliseconds() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MILLISECONDS));
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS;
		String patternNode = PreferenceConstants.P_FORMAT_X_AXIS_MILLISECONDS;
		String colorNode = PreferencesSupport.isDarkTheme() ? PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS_DARKTHEME : PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS;
		//
		ChartSupport.setAxisSettingsExtended(primaryAxisSettingsX, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
		primaryAxisSettingsX.setVisible(ChartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS));
		primaryAxisSettingsX.setTitleVisible(ChartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MILLISECONDS));
		//
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_X_AXIS_MILLISECONDS);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_X_AXIS_MILLISECONDS);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_X_AXIS_MILLISECONDS);
		primaryAxisSettingsX.setTitleFont(Fonts.getCachedFont(getBaseChart().getDisplay(), name, height, style));
	}

	private void adjustAxisIntensity() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_INTENSITY));
		//
		String positionNode = PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY;
		String patternNode = PreferenceConstants.P_FORMAT_Y_AXIS_INTENSITY;
		String colorNode = PreferencesSupport.isDarkTheme() ? PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY_DARKTHEME : PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY;
		//
		ChartSupport.setAxisSettingsExtended(primaryAxisSettingsY, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
		primaryAxisSettingsY.setVisible(ChartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY));
		primaryAxisSettingsY.setTitleVisible(ChartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_INTENSITY));
		//
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_Y_AXIS_INTENSITY);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_Y_AXIS_INTENSITY);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_Y_AXIS_INTENSITY);
		primaryAxisSettingsY.setTitleFont(Fonts.getCachedFont(getBaseChart().getDisplay(), name, height, style));
	}

	private void adjustAxisRelativeIntensity() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = ChartSupport.getSecondaryAxisSettingsY(titleRelativeIntensity, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY;
		String patternNode = PreferenceConstants.P_FORMAT_Y_AXIS_RELATIVE_INTENSITY;
		String colorNode = PreferencesSupport.isDarkTheme() ? PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY_DARKTHEME : PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY;
		boolean isShowAxis = ChartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY);
		boolean isShowAxisTitle = ChartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY);
		//
		String title = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_RELATIVE_INTENSITY);
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_Y_AXIS_RELATIVE_INTENSITY);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_Y_AXIS_RELATIVE_INTENSITY);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_Y_AXIS_RELATIVE_INTENSITY);
		Font titleFont = Fonts.getCachedFont(getBaseChart().getDisplay(), name, height, style);
		//
		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(title, new PercentageConverter(SWT.VERTICAL, true));
				ChartSupport.setAxisSettingsExtended(secondaryAxisSettingsY, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsY.setTitleVisible(isShowAxisTitle);
				secondaryAxisSettingsY.setTitleFont(titleFont);
				chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
			} else {
				ChartSupport.setAxisSettingsExtended(axisSettings, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
				axisSettings.setTitle(title);
				axisSettings.setTitleFont(titleFont);
				axisSettings.setVisible(true);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setTitle(title);
				axisSettings.setTitleFont(titleFont);
				axisSettings.setVisible(false);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		}
		/*
		 * Update the title to retrieve the correct axis.
		 */
		titleRelativeIntensity = title;
	}

	private void adjustAxisSeconds() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = ChartSupport.getSecondaryAxisSettingsX(titleSeconds, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_SECONDS;
		String patternNode = PreferenceConstants.P_FORMAT_X_AXIS_SECONDS;
		String colorNode = PreferencesSupport.isDarkTheme() ? PreferenceConstants.P_COLOR_X_AXIS_SECONDS_DARKTHEME : PreferenceConstants.P_COLOR_X_AXIS_SECONDS;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS;
		boolean isShowAxis = ChartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_SECONDS);
		boolean isShowAxisTitle = ChartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SECONDS);
		//
		String title = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_SECONDS);
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_X_AXIS_SECONDS);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_X_AXIS_SECONDS);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_X_AXIS_SECONDS);
		Font titleFont = Fonts.getCachedFont(getBaseChart().getDisplay(), name, height, style);
		//
		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(title, new MillisecondsToSecondsConverter());
				ChartSupport.setAxisSettingsExtended(secondaryAxisSettingsX, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsX.setTitleFont(titleFont);
				secondaryAxisSettingsX.setTitleVisible(isShowAxisTitle);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				ChartSupport.setAxisSettingsExtended(axisSettings, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
				axisSettings.setTitle(title);
				axisSettings.setTitleFont(titleFont);
				axisSettings.setVisible(true);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setTitle(title);
				axisSettings.setTitleFont(titleFont);
				axisSettings.setVisible(false);
				axisSettings.setTitleVisible(isShowAxisTitle);
			}
		}
		/*
		 * Update the title to retrieve the correct axis.
		 */
		titleSeconds = title;
	}

	private void adjustAxisMinutes() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = ChartSupport.getSecondaryAxisSettingsX(titleMinutes, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_MINUTES;
		String patternNode = PreferenceConstants.P_FORMAT_X_AXIS_MINUTES;
		String colorNode = PreferencesSupport.isDarkTheme() ? PreferenceConstants.P_COLOR_X_AXIS_MINUTES_DARKTHEME : PreferenceConstants.P_COLOR_X_AXIS_MINUTES;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES;
		boolean isShowAxis = ChartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_MINUTES);
		boolean isShowAxisTitle = ChartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MINUTES);
		//
		String title = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MINUTES);
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_X_AXIS_MINUTES);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_X_AXIS_MINUTES);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_X_AXIS_MINUTES);
		Font titleFont = Fonts.getCachedFont(getBaseChart().getDisplay(), name, height, style);
		boolean drawAxisLine = ChartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_LINE_MINUTES);
		boolean drawPositionMarker = ChartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_POSITION_MARKER_MINUTES);
		//
		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(title, new MillisecondsToMinuteConverter());
				ChartSupport.setAxisSettingsExtended(secondaryAxisSettingsX, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsX.setTitleFont(titleFont);
				secondaryAxisSettingsX.setTitleVisible(isShowAxisTitle);
				secondaryAxisSettingsX.setDrawAxisLine(drawAxisLine);
				secondaryAxisSettingsX.setDrawPositionMarker(drawPositionMarker);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				ChartSupport.setAxisSettingsExtended(axisSettings, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
				axisSettings.setTitle(title);
				axisSettings.setTitleFont(titleFont);
				axisSettings.setVisible(true);
				axisSettings.setTitleVisible(isShowAxisTitle);
				axisSettings.setDrawAxisLine(drawAxisLine);
				axisSettings.setDrawPositionMarker(drawPositionMarker);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setTitle(title);
				axisSettings.setTitleFont(titleFont);
				axisSettings.setVisible(false);
				axisSettings.setTitleVisible(isShowAxisTitle);
				axisSettings.setDrawAxisLine(drawAxisLine);
				axisSettings.setDrawPositionMarker(drawPositionMarker);
			}
		}
		/*
		 * Update the title to retrieve the correct axis.
		 */
		titleMinutes = title;
	}
}
