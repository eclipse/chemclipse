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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.ScanToSecondsConverter;
import org.eclipse.eavp.service.swtchart.axisconverter.RelativeIntensityConverter;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IPrimaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.IAxis.Position;
import org.swtchart.LineStyle;

public class NMRChart extends LineChart {

	public NMRChart() {
		super();
		initialize();
	}

	public NMRChart(Composite parent, int style) {
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
		primaryAxisSettingsX.setTitle("scan");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setPosition(Position.Primary);
		primaryAxisSettingsX.setVisible(false);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	}

	private void addSecondaryAxisSetRaw(IChartSettings chartSettings) {

		chartSettings.getSecondaryAxisSettingsListY().clear();
		//
		ISecondaryAxisSettings secondaryAxisSettingsX1 = new SecondaryAxisSettings("t1 (sec)", new ScanToSecondsConverter());
		secondaryAxisSettingsX1.setPosition(Position.Primary);
		secondaryAxisSettingsX1.setDecimalFormat(new DecimalFormat(("0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsX1.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX1);
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
		//
		setPrimaryAxisSetProcessed(chartSettings);
		addSecondaryAxisSetProcessed(chartSettings);
		applySettings(chartSettings);
	}

	private void setPrimaryAxisSetProcessed(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("ppm");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsY.setGridLineStyle(LineStyle.NONE);
	}

	private void addSecondaryAxisSetProcessed(IChartSettings chartSettings) {

		chartSettings.getSecondaryAxisSettingsListY().clear();
		//
		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("Relative Intensity [%]", new RelativeIntensityConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}
}
