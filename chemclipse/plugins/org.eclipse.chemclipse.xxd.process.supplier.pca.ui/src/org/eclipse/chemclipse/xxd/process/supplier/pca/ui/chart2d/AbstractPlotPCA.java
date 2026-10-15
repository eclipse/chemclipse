/*******************************************************************************
 * Copyright (c) 2017, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements
 * Lorenz Gerber - implement IRangeSupport
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.chart2d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.IRangeSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.axisconverter.PassThroughConverter;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.scattercharts.ScatterChart;

public abstract class AbstractPlotPCA extends ScatterChart implements IRangeSupport {

	private DecimalFormat decimalFormat = new DecimalFormat(("0.00E0"), new DecimalFormatSymbols(Locale.ENGLISH));
	private String title = "";

	private Range selectedRangeX = null;
	private Range selectedRangeY = null;

	protected AbstractPlotPCA(Composite parent, int style, String title) {

		super(parent, style);
		this.title = title;
		initialize();
	}

	private void addSecondaryAxisSet(IChartSettings chartSettings) {

		ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings("PC1", new PassThroughConverter());
		secondaryAxisSettingsX.setTitle("");
		secondaryAxisSettingsX.setPosition(Position.Secondary);
		secondaryAxisSettingsX.setDecimalFormat(decimalFormat);
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);

		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("PC2", new PassThroughConverter());
		secondaryAxisSettingsY.setTitle("");
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(decimalFormat);
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
		chartSettings.setTitle(title);
		chartSettings.setTitleVisible(true);
		chartSettings.setBackground(null);
		chartSettings.setBackgroundChart(null);
		chartSettings.setBackgroundPlotArea(null);
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(false);
		chartSettings.setVerticalSliderVisible(false);

		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(false);
		rangeRestriction.setZeroY(false);
		rangeRestriction.setRestrictFrame(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtend(0.25d);

		chartSettings.setShowAxisZeroMarker(true);
		chartSettings.setShowSeriesLabelMarker(true);
		chartSettings.setColorSeriesLabelMarker(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		chartSettings.setUseSeriesLabelDescription(true);
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableCompress(false);

		setPrimaryAxisSet(chartSettings);
		addSecondaryAxisSet(chartSettings);

		applySettings(chartSettings);
	}

	protected boolean isPointVisible(Point point, Rectangle plotAreaBounds) {

		return (point.x >= 0 && point.x <= plotAreaBounds.width && point.y >= 0 && point.y <= plotAreaBounds.height);
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("PC1");
		primaryAxisSettingsX.setDecimalFormat(decimalFormat);

		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("PC2");
		primaryAxisSettingsY.setDecimalFormat(decimalFormat);
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

		builder.append("PC");
		builder.append(" ");
		builder.append(pc);
		builder.append(" ");
		builder.append("(");
		builder.append(decimalFormat.format(variance));
		builder.append("%");
		builder.append(")");

		return builder.toString();
	}

	@Override
	public void clearSelectedRange() {

		selectedRangeX = null;
		selectedRangeY = null;
	}

	@Override
	public void assignCurrentRangeSelection() {

		BaseChart baseChart = getBaseChart();
		selectedRangeX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
		selectedRangeY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
	}

	@Override
	public Range getCurrentRangeX() {

		BaseChart baseChart = getBaseChart();
		IAxisSet axisSet = baseChart.getAxisSet();
		IAxis xAxis = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
		return new Range(xAxis.getRange().lower, xAxis.getRange().upper);
	}

	@Override
	public void updateRangeX(Range selectedRangeX) {

		updateRange(selectedRangeX, selectedRangeY);
	}

	@Override
	public Range getCurrentRangeY() {

		BaseChart baseChart = getBaseChart();
		IAxisSet axisSet = baseChart.getAxisSet();
		IAxis yAxis = axisSet.getYAxis(BaseChart.ID_PRIMARY_Y_AXIS);
		return new Range(yAxis.getRange().lower, yAxis.getRange().upper);
	}

	@Override
	public void updateRangeY(Range selectedRangeY) {

		updateRange(selectedRangeX, selectedRangeY);
	}

	@Override
	public void updateRange(Range selectedRangeX, Range selectedRangeY) {

		this.selectedRangeX = selectedRangeX;
		this.selectedRangeY = selectedRangeY;
		adjustChartRange();
	}

	@Override
	public void adjustChartRange() {

		setRange(IExtendedChart.X_AXIS, selectedRangeX);
		setRange(IExtendedChart.Y_AXIS, selectedRangeY);
		redraw();
	}
}