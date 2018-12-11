/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.axisconverter.PassThroughConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.scattercharts.ScatterChart;
import org.eclipse.swtchart.IAxis.Position;

public abstract class PCA2DPlot extends ScatterChart {

	private Color COLOR_BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private DecimalFormat decimalFormat = new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH));
	//
	private String chartTitle = "";
	private String xAxisTitle = "PC1";
	private String yAxisTitle = "PC2";

	public PCA2DPlot(Composite parent, String chartTitle) {
		super(parent, SWT.None);
		this.chartTitle = chartTitle;
		initialize();
	}

	private void addSecondaryAxisSet(IChartSettings chartSettings) {

		ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(xAxisTitle, new PassThroughConverter());
		secondaryAxisSettingsX.setTitle("");
		secondaryAxisSettingsX.setPosition(Position.Secondary);
		secondaryAxisSettingsX.setDecimalFormat(decimalFormat);
		secondaryAxisSettingsX.setColor(COLOR_BLACK);
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
		//
		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(yAxisTitle, new PassThroughConverter());
		secondaryAxisSettingsY.setTitle("");
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(decimalFormat);
		secondaryAxisSettingsY.setColor(COLOR_BLACK);
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	public void deselect(Set<String> set) {

		Set<String> selection = new HashSet<>(getBaseChart().getSelectedSeriesIds());
		for(String id : set) {
			selection.remove(id);
		}
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
		getBaseChart().resetSeriesSettings();
		for(String id : selection) {
			getBaseChart().selectSeries(id);
		}
		getBaseChart().redraw();
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitle(chartTitle);
		chartSettings.setTitleVisible(true);
		chartSettings.setTitleColor(COLOR_BLACK);
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(false);
		chartSettings.setVerticalSliderVisible(false);
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(false);
		rangeRestriction.setZeroY(false);
		rangeRestriction.setRestrictZoom(false);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtend(0.25d);
		chartSettings.setShowAxisZeroMarker(true);
		chartSettings.setColorAxisZeroMarker(COLOR_BLACK);
		chartSettings.setShowSeriesLabelMarker(true);
		chartSettings.setColorSeriesLabelMarker(COLOR_BLACK);
		chartSettings.setCreateMenu(true);
		//
		setPrimaryAxisSet(chartSettings);
		addSecondaryAxisSet(chartSettings);
		//
		applySettings(chartSettings);
	}

	protected boolean isPointVisible(Point point, Rectangle plotAreaBounds) {

		if(point.x >= 0 && point.x <= plotAreaBounds.width && point.y >= 0 && point.y <= plotAreaBounds.height) {
			return true;
		} else {
			return false;
		}
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(xAxisTitle);
		primaryAxisSettingsX.setDecimalFormat(decimalFormat);
		primaryAxisSettingsX.setColor(COLOR_BLACK);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(yAxisTitle);
		primaryAxisSettingsY.setDecimalFormat(decimalFormat);
		primaryAxisSettingsY.setColor(COLOR_BLACK);
	}

	protected void update(int pcX, int pcY) {

		getChartSettings().getPrimaryAxisSettingsX().setTitle("PC" + pcX);
		getChartSettings().getPrimaryAxisSettingsY().setTitle("PC" + pcY);
		applySettings(getChartSettings());
		update();
		getBaseChart().redraw();
	}
}
