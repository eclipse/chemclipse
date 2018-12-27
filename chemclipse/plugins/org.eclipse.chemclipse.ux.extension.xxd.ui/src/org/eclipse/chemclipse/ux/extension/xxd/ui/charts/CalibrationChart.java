/*******************************************************************************
 * Copyright (c) 2018 pwenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * pwenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

public class CalibrationChart extends LineChart {

	private static final String TITLE_X_AXIS_CONCENTRATION = "Concentration";
	private static final String TITLE_Y_AXIS_RESPONSE = "Response";

	public CalibrationChart() {
		super();
		initialize();
	}

	public CalibrationChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(true);
		chartSettings.getRangeRestriction().setZeroY(true);
		//
		modifyAxisSet(true);
	}

	public void modifyAxisSet(boolean applySettings) {

		modifyMillisecondsXAxis();
		modifyIntensityYAxis();
		//
		if(applySettings) {
			IChartSettings chartSettings = getChartSettings();
			applySettings(chartSettings);
		}
	}

	private void modifyMillisecondsXAxis() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(TITLE_X_AXIS_CONCENTRATION);
		setAxisSettings(primaryAxisSettingsX, PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS, "0.0##", PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS, PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS, PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS);
		primaryAxisSettingsX.setVisible(true);
	}

	private void modifyIntensityYAxis() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(TITLE_Y_AXIS_RESPONSE);
		setAxisSettings(primaryAxisSettingsY, PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY, "0.0#E0", PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY, PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY, PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY);
		primaryAxisSettingsY.setVisible(true);
	}

	private void setAxisSettings(IAxisSettings axisSettings, String positionNode, String pattern, String colorNode, String gridLineStyleNode, String gridColorNode) {

		Position position = Position.Primary; // Position.valueOf(preferenceStore.getString(positionNode));
		Color color = Colors.BLACK; // Colors.getColor(preferenceStore.getString(colorNode));
		LineStyle gridLineStyle = LineStyle.DASH; // LineStyle.valueOf(preferenceStore.getString(gridLineStyleNode));
		Color gridColor = Colors.GRAY; // Colors.getColor(preferenceStore.getString(gridColorNode));
		setAxisSettings(axisSettings, position, pattern, color, gridLineStyle, gridColor);
	}

	private void setAxisSettings(IAxisSettings axisSettings, Position position, String decimalPattern, Color color, LineStyle gridLineStyle, Color gridColor) {

		if(axisSettings != null) {
			axisSettings.setPosition(position);
			axisSettings.setDecimalFormat(ValueFormat.getDecimalFormatEnglish(decimalPattern));
			axisSettings.setColor(color);
			axisSettings.setGridColor(gridColor);
			axisSettings.setGridLineStyle(gridLineStyle);
		}
	}
}
