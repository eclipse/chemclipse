/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
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
		secondaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
		//
		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("PC2", new PassThroughConverter());
		secondaryAxisSettingsY.setTitle("");
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(decimalFormat);
		secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
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
		chartSettings.setTitleColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
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
		chartSettings.setColorAxisZeroMarker(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		chartSettings.setShowSeriesLabelMarker(true);
		chartSettings.setColorSeriesLabelMarker(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
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
		primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("PC2");
		primaryAxisSettingsY.setDecimalFormat(decimalFormat);
		primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
	}

	protected void update(int pcX, int pcY) {

		getChartSettings().getPrimaryAxisSettingsX().setTitle("PC" + pcX);
		getChartSettings().getPrimaryAxisSettingsY().setTitle("PC" + pcY);
		applySettings(getChartSettings());
		getBaseChart().redraw();
	}
}