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
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

public class CalibrationChart extends LineChart {

	private static final String TITLE_X_AXIS_CONCENTRATION = "Concentration";
	private static final String TITLE_Y_AXIS_RESPONSE = "Response";
	private static final String TITLE_Y_AXIS_RELATIVE_RESPONSE = "Relative Response [%]";
	//
	private ChartSupport chartSupport = new ChartSupport();

	public CalibrationChart() {
		super();
		initialize();
	}

	public CalibrationChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void setConcentrationLabel(String concentrationUnit) {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		String title = TITLE_X_AXIS_CONCENTRATION;
		if(concentrationUnit != null && !"".equals(concentrationUnit)) {
			title += " [" + concentrationUnit + "]";
		}
		primaryAxisSettingsX.setTitle(title);
		applySettings(chartSettings);
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		//
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(false);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(true);
		chartSettings.getRangeRestriction().setZeroY(true);
		chartSettings.setCreateMenu(true);
		//
		modifyAxes(true);
	}

	public void modifyAxes(boolean applySettings) {

		modifyXAxisConcentration();
		modifyYAxisResponse();
		modifyYAxisRelativeResponse();
		//
		if(applySettings) {
			IChartSettings chartSettings = getChartSettings();
			applySettings(chartSettings);
		}
	}

	private void modifyXAxisConcentration() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(TITLE_X_AXIS_CONCENTRATION);
		//
		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_CONCENTRATION_CALIBRATION;
		String pattern = "0.0##";
		String colorNode = PreferenceConstants.P_COLOR_X_AXIS_CONCENTRATION_CALIBRATION;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_CONCENTRATION_CALIBRATION;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_CONCENTRATION_CALIBRATION;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_CONCENTRATION_CALIBRATION);
		//
		chartSupport.setAxisSettings(primaryAxisSettingsX, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
		primaryAxisSettingsX.setVisible(isShowAxis);
	}

	private void modifyYAxisResponse() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(TITLE_Y_AXIS_RESPONSE);
		//
		String positionNode = PreferenceConstants.P_POSITION_Y_AXIS_RESPONSE_CALIBRATION;
		String pattern = "0.0#E0";
		String colorNode = PreferenceConstants.P_COLOR_Y_AXIS_RESPONSE_CALIBRATION;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RESPONSE_CALIBRATION;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RESPONSE_CALIBRATION;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_RESPONSE_CALIBRATION);
		//
		chartSupport.setAxisSettings(primaryAxisSettingsY, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
		primaryAxisSettingsY.setVisible(isShowAxis);
	}

	private void modifyYAxisRelativeResponse() {

		IChartSettings chartSettings = getChartSettings();
		ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsY(TITLE_Y_AXIS_RELATIVE_RESPONSE, chartSettings);
		//
		String positionNode = PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION;
		String pattern = "0.00";
		String colorNode = PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION;
		boolean isShowAxis = chartSupport.getBoolean(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION);
		//
		if(isShowAxis) {
			if(axisSettings == null) {
				ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(TITLE_Y_AXIS_RELATIVE_RESPONSE, new PercentageConverter(SWT.VERTICAL, true));
				chartSupport.setAxisSettings(secondaryAxisSettingsY, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
			} else {
				chartSupport.setAxisSettings(axisSettings, positionNode, pattern, colorNode, gridLineStyleNode, gridColorNode);
				axisSettings.setVisible(true);
			}
		} else {
			if(axisSettings != null) {
				axisSettings.setVisible(false);
			}
		}
	}
}
