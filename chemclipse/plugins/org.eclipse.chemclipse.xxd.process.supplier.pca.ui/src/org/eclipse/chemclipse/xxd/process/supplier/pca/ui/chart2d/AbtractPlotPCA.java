/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.chart2d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.extensions.axisconverter.PassThroughConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.scattercharts.ScatterChart;

public abstract class AbtractPlotPCA extends ScatterChart {

	private DecimalFormat decimalFormat = new DecimalFormat(("0.00E0"), new DecimalFormatSymbols(Locale.ENGLISH));
	private String title = "";

	public AbtractPlotPCA(Composite parent, int style, String title) {

		super(parent, style);
		this.title = title;
		initialize();
	}

	private void addSecondaryAxisSet(IChartSettings chartSettings) {

		ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings("PC1", new PassThroughConverter());
		secondaryAxisSettingsX.setTitle("");
		secondaryAxisSettingsX.setPosition(Position.Secondary);
		secondaryAxisSettingsX.setDecimalFormat(decimalFormat);
		if(PreferencesSupport.isDarkTheme()) {
			secondaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			secondaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
		//
		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("PC2", new PassThroughConverter());
		secondaryAxisSettingsY.setTitle("");
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(decimalFormat);
		if(PreferencesSupport.isDarkTheme()) {
			secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	public void deselect(Set<String> set) {

		Set<String> selection = new HashSet<>(getBaseChart().getSelectedSeriesIds());
		for(String id : set) {
			selection.remove(id);
		}
		//
		getBaseChart().resetSeriesSettings();
		for(String id : selection) {
			getBaseChart().selectSeries(id);
		}
		getBaseChart().redraw();
	}

	public void deselect(String... set) {

		Set<String> selection = new HashSet<>(getBaseChart().getSelectedSeriesIds());
		for(String id : set) {
			selection.remove(id);
		}
		//
		getBaseChart().resetSeriesSettings();
		for(String id : selection) {
			getBaseChart().selectSeries(id);
		}
		getBaseChart().redraw();
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitle(title);
		chartSettings.setTitleVisible(true);
		if(PreferencesSupport.isDarkTheme()) {
			chartSettings.setTitleColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			chartSettings.setTitleColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		chartSettings.setBackground(null);
		chartSettings.setBackgroundChart(null);
		chartSettings.setBackgroundPlotArea(null);
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(false);
		chartSettings.setVerticalSliderVisible(false);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(false);
		rangeRestriction.setZeroY(false);
		rangeRestriction.setRestrictFrame(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtend(0.25d);
		//
		chartSettings.setShowAxisZeroMarker(true);
		if(PreferencesSupport.isDarkTheme()) {
			chartSettings.setColorAxisZeroMarker(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			chartSettings.setColorAxisZeroMarker(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		chartSettings.setShowSeriesLabelMarker(true);
		if(PreferencesSupport.isDarkTheme()) {
			chartSettings.setColorSeriesLabelMarker(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			chartSettings.setColorSeriesLabelMarker(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		chartSettings.setUseSeriesLabelDescription(true);
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableCompress(false);
		//
		setPrimaryAxisSet(chartSettings);
		addSecondaryAxisSet(chartSettings);
		//
		applySettings(chartSettings);
	}

	protected boolean isPointVisible(Point point, Rectangle plotAreaBounds) {

		return (point.x >= 0 && point.x <= plotAreaBounds.width && point.y >= 0 && point.y <= plotAreaBounds.height);
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("PC1");
		primaryAxisSettingsX.setDecimalFormat(decimalFormat);
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("PC2");
		primaryAxisSettingsY.setDecimalFormat(decimalFormat);
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
	}

	protected void update(int pcX, int pcY, double[] explainedVariances) {

		getChartSettings().getPrimaryAxisSettingsX().setTitle(getTitlePC(pcX, explainedVariances[pcX - 1]));
		getChartSettings().getPrimaryAxisSettingsY().setTitle(getTitlePC(pcY, explainedVariances[pcY - 1]));
		applySettings(getChartSettings());
		getBaseChart().redraw();
	}

	private String getTitlePC(int pc, double explainedVariance) {

		double variance = explainedVariance * 100.0d;
		DecimalFormat decimalFormat;
		if(variance > 1.0d) {
			decimalFormat = ValueFormat.getDecimalFormatEnglish("0.00");
		} else {
			decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0000");
		}
		StringBuilder builder = new StringBuilder();
		//
		builder.append("PC");
		builder.append(" ");
		builder.append(pc);
		builder.append(" ");
		builder.append("(");
		builder.append(decimalFormat.format(variance));
		builder.append("%");
		builder.append(")");
		//
		return builder.toString();
	}
}