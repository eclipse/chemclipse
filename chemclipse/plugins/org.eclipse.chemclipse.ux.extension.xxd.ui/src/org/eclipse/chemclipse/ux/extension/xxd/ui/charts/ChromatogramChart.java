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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToSecondsConverter;
import org.eclipse.eavp.service.swtchart.axisconverter.RelativeIntensityConverter;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IPrimaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
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
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setPosition(Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS)));
		primaryAxisSettingsX.setVisible(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS));
		primaryAxisSettingsX.setGridLineStyle(LineStyle.NONE);
	}

	private void modifyIntensityYAxis() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(TITLE_Y_AXIS_INTENSITY);
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsY.setPosition(Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY)));
		primaryAxisSettingsY.setVisible(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY));
		primaryAxisSettingsY.setGridLineStyle(LineStyle.NONE);
	}

	private void modifyRelativeIntensityYAxis() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = getSecondaryAxisSettingsY(TITLE_Y_AXIS_RELATIVE_INTENSITY);
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY)) {
			Position position = Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY));
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(TITLE_Y_AXIS_RELATIVE_INTENSITY, new RelativeIntensityConverter(SWT.VERTICAL, true));
				secondaryAxisSettingsY.setPosition(position);
				secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
				secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
			} else {
				axisSettings.setPosition(position);
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
			Position position = Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_X_AXIS_SECONDS));
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_SECONDS, new MillisecondsToSecondsConverter());
				secondaryAxisSettingsX.setPosition(position);
				secondaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
				secondaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				axisSettings.setPosition(position);
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
			Position position = Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_X_AXIS_MINUTES));
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_MINUTES, new MillisecondsToMinuteConverter());
				secondaryAxisSettingsX.setPosition(position);
				secondaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
				secondaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
			} else {
				axisSettings.setPosition(position);
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
}
