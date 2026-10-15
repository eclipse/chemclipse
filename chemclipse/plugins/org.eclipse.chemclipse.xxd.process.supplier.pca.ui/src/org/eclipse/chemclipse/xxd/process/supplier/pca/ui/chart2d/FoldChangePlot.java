/*******************************************************************************
 * Copyright (c) 2025, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Lorenz Gerber- initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.chart2d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.IRangeSupport;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IEvaluation;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResult;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.support.SeriesConverter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt.FoldChangeLimits;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.axisconverter.PassThroughConverter;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.scattercharts.IScatterSeriesData;
import org.eclipse.swtchart.extensions.scattercharts.ScatterChart;

public class FoldChangePlot extends ScatterChart implements IRangeSupport {

	private DecimalFormat decimalFormat = new DecimalFormat(("0.00E0"), new DecimalFormatSymbols(Locale.ENGLISH));
	private String title = "";

	private Range selectedRangeX = null;
	private Range selectedRangeY = null;
	private FoldChangeLimits foldChangeLimits;

	public FoldChangePlot(Composite parent, int style) {

		super(parent, style);
		this.title = "Fold Change Plot";
		initialize();
	}

	public void setInput(IEvaluation<IVariable, ISample, IResult> evaluation, String group1, String group2) {

		deleteSeries();
		if(evaluation != null) {
			addLimits();
			ISamplesPCA<IVariable, ISample> samples = evaluation.getSamples();
			List<IScatterSeriesData> series;
			series = SeriesConverter.foldChangeToSeries(samples, evaluation.getHighlightedVariables(), group1, group2);
			addSeriesData(series);
		}
		getBaseChart().redraw();
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitle(title);
		chartSettings.setTitleVisible(true);
		chartSettings.setTitleColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
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
		chartSettings.setColorAxisZeroMarker(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		chartSettings.setShowSeriesLabelMarker(true);
		chartSettings.setColorSeriesLabelMarker(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		chartSettings.setUseSeriesLabelDescription(true);
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableCompress(false);

		setPrimaryAxisSet(chartSettings);
		addSecondaryAxisSet(chartSettings);

		applySettings(chartSettings);

	}

	private void addLimits() {

		BaseChart baseChart = getBaseChart();
		IPlotArea plotArea = baseChart.getPlotArea();

		if(foldChangeLimits != null) {
			plotArea.removeCustomPaintListener(foldChangeLimits);
		}
		foldChangeLimits = new FoldChangeLimits(baseChart);
		plotArea.addCustomPaintListener(foldChangeLimits);

	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("log2(fold change)");
		primaryAxisSettingsX.setDecimalFormat(decimalFormat);
		primaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("-log10(pValue)");
		primaryAxisSettingsY.setDecimalFormat(decimalFormat);
		primaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
	}

	private void addSecondaryAxisSet(IChartSettings chartSettings) {

		ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings("log2(fold change)", new PassThroughConverter());
		secondaryAxisSettingsX.setTitle("");
		secondaryAxisSettingsX.setPosition(Position.Secondary);
		secondaryAxisSettingsX.setDecimalFormat(decimalFormat);
		secondaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);

		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("-log10(pValue)", new PassThroughConverter());
		secondaryAxisSettingsY.setTitle("");
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(decimalFormat);
		secondaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
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
