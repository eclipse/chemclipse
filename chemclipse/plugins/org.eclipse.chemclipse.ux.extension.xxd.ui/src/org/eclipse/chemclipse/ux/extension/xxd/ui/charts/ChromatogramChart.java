/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

public class ChromatogramChart extends LineChart {

	private final ChartSupport chartSupport;
	//
	private String titleSeconds = "";
	private String titleMinutes = "";
	private String titleRelativeIntensity = "";
	private final IPreferenceStore preferenceStore;

	public ChromatogramChart() {

		super();
		this.preferenceStore = Activator.getDefault().getPreferenceStore();
		this.chartSupport = new ChartSupport(preferenceStore);
		initialize();
	}

	public ChromatogramChart(Composite parent, int style) {

		this(parent, style, Activator.getDefault().getPreferenceStore());
	}

	/**
	 * creates a new chart with the given parent, style and preference store. Make sure to properly initialize the store
	 * 
	 * @param parent
	 * @param style
	 * @param preferenceStore
	 */
	public ChromatogramChart(Composite parent, int style, IPreferenceStore preferenceStore) {

		super(parent, style);
		chartSupport = new ChartSupport(preferenceStore);
		this.preferenceStore = preferenceStore;
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

		/*
		 * Initialize secondary axis titles.
		 */
		titleSeconds = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_SECONDS);
		titleMinutes = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MINUTES);
		titleRelativeIntensity = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_RELATIVE_INTENSITY);
		//
		IChartSettings chartSettings = getChartSettings();
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
		primaryAxisSettingsX.setTitle(preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MILLISECONDS));
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
		//
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_X_AXIS_MILLISECONDS);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_X_AXIS_MILLISECONDS);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_X_AXIS_MILLISECONDS);
		primaryAxisSettingsX.setTitleFont(Fonts.getCachedFont(getBaseChart().getDisplay(), name, height, style));
	}

	private void modifyYAxisIntensity() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_INTENSITY));
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
		//
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_Y_AXIS_INTENSITY);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_Y_AXIS_INTENSITY);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_Y_AXIS_INTENSITY);
		primaryAxisSettingsY.setTitleFont(Fonts.getCachedFont(getBaseChart().getDisplay(), name, height, style));
	}

	private void modifyYAxisRelativeIntensity() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsY(titleRelativeIntensity, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY;
		String pattern = "0.00";
		String colorNode = PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY);
		boolean isShowAxisTitle = chartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY);
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
				chartSupport.setAxisSettings(secondaryAxisSettingsY, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsY.setTitleVisible(isShowAxisTitle);
				secondaryAxisSettingsY.setTitleFont(titleFont);
				chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
			} else {
				chartSupport.setAxisSettings(axisSettings, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
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

	private void modifyXAxisSeconds() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsX(titleSeconds, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_SECONDS;
		String pattern = "0.00";
		String colorNode = PreferenceConstants.P_COLOR_X_AXIS_SECONDS;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_SECONDS);
		boolean isShowAxisTitle = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SECONDS);
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
				chartSupport.setAxisSettings(secondaryAxisSettingsX, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsX.setTitleFont(titleFont);
				secondaryAxisSettingsX.setTitleVisible(isShowAxisTitle);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				chartSupport.setAxisSettings(axisSettings, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
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

	private void modifyXAxisMinutes() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsX(titleMinutes, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_MINUTES;
		String pattern = "0.00";
		String colorNode = PreferenceConstants.P_COLOR_X_AXIS_MINUTES;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_MINUTES);
		boolean isShowAxisTitle = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MINUTES);
		//
		String title = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MINUTES);
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_X_AXIS_MINUTES);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_X_AXIS_MINUTES);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_X_AXIS_MINUTES);
		Font titleFont = Fonts.getCachedFont(getBaseChart().getDisplay(), name, height, style);
		//
		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(title, new MillisecondsToMinuteConverter());
				chartSupport.setAxisSettings(secondaryAxisSettingsX, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				secondaryAxisSettingsX.setTitleFont(titleFont);
				secondaryAxisSettingsX.setTitleVisible(isShowAxisTitle);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				chartSupport.setAxisSettings(axisSettings, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
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
		titleMinutes = title;
	}

	/**
	 * Initialize the defaults of the given preference store used by this class
	 * 
	 * @param preferenceStore
	 *            the preference store to use
	 * @return the given preference store for chaining
	 */
	public static IPreferenceStore initializeDefaults(IPreferenceStore preferenceStore) {

		preferenceStore.setDefault(PreferenceConstants.P_TITLE_X_AXIS_SECONDS, PreferenceConstants.DEF_TITLE_X_AXIS_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_X_AXIS_SECONDS, PreferenceConstants.DEF_SHOW_X_AXIS_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_POSITION_X_AXIS_SECONDS, PreferenceConstants.DEF_POSITION_X_AXIS_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_COLOR_X_AXIS_SECONDS, PreferenceConstants.DEF_COLOR_X_AXIS_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_NAME_X_AXIS_SECONDS, PreferenceConstants.DEF_FONT_NAME_X_AXIS_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_SIZE_X_AXIS_SECONDS, PreferenceConstants.DEF_FONT_SIZE);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_STYLE_X_AXIS_SECONDS, PreferenceConstants.DEF_FONT_STYLE_X_AXIS_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SECONDS, PreferenceConstants.DEF_SHOW_X_AXIS_TITLE_SECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_TITLE_X_AXIS_MINUTES, PreferenceConstants.DEF_TITLE_X_AXIS_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_X_AXIS_MINUTES, PreferenceConstants.DEF_SHOW_X_AXIS_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_POSITION_X_AXIS_MINUTES, PreferenceConstants.DEF_POSITION_X_AXIS_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_COLOR_X_AXIS_MINUTES, PreferenceConstants.DEF_COLOR_X_AXIS_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_NAME_X_AXIS_MINUTES, PreferenceConstants.DEF_FONT_NAME_X_AXIS_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_SIZE_X_AXIS_MINUTES, PreferenceConstants.DEF_FONT_SIZE);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_STYLE_X_AXIS_MINUTES, PreferenceConstants.DEF_FONT_STYLE_X_AXIS_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MINUTES, PreferenceConstants.DEF_SHOW_X_AXIS_TITLE_MINUTES);
		preferenceStore.setDefault(PreferenceConstants.P_TITLE_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_TITLE_Y_AXIS_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_SHOW_Y_AXIS_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_POSITION_Y_AXIS_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_COLOR_Y_AXIS_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_NAME_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_FONT_NAME_Y_AXIS_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_SIZE_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_FONT_SIZE);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_STYLE_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_FONT_STYLE_Y_AXIS_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY, PreferenceConstants.DEF_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_TITLE_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_TITLE_X_AXIS_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_SHOW_X_AXIS_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_POSITION_X_AXIS_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_COLOR_X_AXIS_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_NAME_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_FONT_NAME_X_AXIS_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_SIZE_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_FONT_SIZE);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_STYLE_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_FONT_STYLE_X_AXIS_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MILLISECONDS, PreferenceConstants.DEF_SHOW_X_AXIS_TITLE_MILLISECONDS);
		preferenceStore.setDefault(PreferenceConstants.P_TITLE_Y_AXIS_INTENSITY, PreferenceConstants.DEF_TITLE_Y_AXIS_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY, PreferenceConstants.DEF_SHOW_Y_AXIS_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY, PreferenceConstants.DEF_POSITION_Y_AXIS_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY, PreferenceConstants.DEF_COLOR_Y_AXIS_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_NAME_Y_AXIS_INTENSITY, PreferenceConstants.DEF_FONT_NAME_Y_AXIS_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_SIZE_Y_AXIS_INTENSITY, PreferenceConstants.DEF_FONT_SIZE);
		preferenceStore.setDefault(PreferenceConstants.P_FONT_STYLE_Y_AXIS_INTENSITY, PreferenceConstants.DEF_FONT_STYLE_Y_AXIS_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY, PreferenceConstants.DEF_GRIDLINE_STYLE_Y_AXIS_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY, PreferenceConstants.DEF_GRIDLINE_COLOR_Y_AXIS_INTENSITY);
		preferenceStore.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_INTENSITY, PreferenceConstants.DEF_SHOW_Y_AXIS_TITLE_INTENSITY);
		return preferenceStore;
	}
}
