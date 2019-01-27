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
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

public class ChartXIR extends LineChart {

	public ChartXIR() {
		super();
		initialize();
	}

	public ChartXIR(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void modifyChart(boolean rawData) {

		if(rawData) {
			modifyRaw();
		} else {
			modifyProcessed();
		}
	}

	private void initialize() {

		modifyProcessed();
	}

	private void modifyRaw() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(false);
		chartSettings.getRangeRestriction().setZeroY(false);
		//
		setPrimaryAxisSetRaw(chartSettings);
		addSecondaryAxisSetRaw(chartSettings);
		applySettings(chartSettings);
	}

	private void setPrimaryAxisSetRaw(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Mirror Position");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setPosition(Position.Primary);
		primaryAxisSettingsX.setVisible(true);
		primaryAxisSettingsX.setReversed(false);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Light measured by detector");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
	}

	private void addSecondaryAxisSetRaw(IChartSettings chartSettings) {

		deleteSecondaryAxes(chartSettings);
		/*
		 * Y
		 */
		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("Relative Intensity [%]", new PercentageConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	private void modifyProcessed() {

		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(true);
		chartSettings.getRangeRestriction().setZeroY(true);
		chartSettings.getRangeRestriction().setForceZeroMinY(true);
		//
		setPrimaryAxisSetProcessed(chartSettings);
		addSecondaryAxisSetProcessed(chartSettings);
		applySettings(chartSettings);
	}

	private void setPrimaryAxisSetProcessed(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Wavelength (nm)");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setVisible(true);
		primaryAxisSettingsX.setReversed(true);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsY.setGridLineStyle(LineStyle.NONE);
		primaryAxisSettingsY.setVisible(true);
		primaryAxisSettingsY.setReversed(true);
	}

	private void addSecondaryAxisSetProcessed(IChartSettings chartSettings) {

		deleteSecondaryAxes(chartSettings);
		/*
		 * Y
		 */
		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("Relative Intensity [%]", new PercentageConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	private void deleteSecondaryAxes(IChartSettings chartSettings) {

		chartSettings.getSecondaryAxisSettingsListX().clear();
		chartSettings.getSecondaryAxisSettingsListY().clear();
	}
}
