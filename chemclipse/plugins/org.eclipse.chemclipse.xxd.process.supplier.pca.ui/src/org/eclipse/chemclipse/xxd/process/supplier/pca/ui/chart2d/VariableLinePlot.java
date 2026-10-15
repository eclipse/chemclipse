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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.IRangeSupport;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IEvaluation;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResult;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.support.FeatureColumnLabels;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.support.SeriesConverter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt.VariableLinePlotHighlights;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.Resources;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

public class VariableLinePlot extends LineChart implements IRangeSupport {

	private DecimalFormat decimalFormat = new DecimalFormat(("0.00E0"), new DecimalFormatSymbols(Locale.ENGLISH));
	private String title = "";

	private Range selectedRangeX = null;
	private Range selectedRangeY = null;
	private Font smallAxisFont = null;
	private VariableLinePlotHighlights variableLinePlotHighlights;

	private FeatureColumnLabels categoryLabelType = FeatureColumnLabels.SAMPLENAMES;

	public VariableLinePlot(Composite parent, int style) {

		super(parent, style);
		this.title = "Variable Line Plot";
		this.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				if(smallAxisFont != null && !smallAxisFont.isDisposed()) {
					smallAxisFont.dispose();
				}
			}
		});
		initialize();
	}

	public void setInput(IEvaluation<IVariable, ISample, IResult> evaluation, String variable, ArrayList<Integer> sortedSampleIndices) {

		deleteSeries();
		if(evaluation != null) {
			ISamplesPCA<IVariable, ISample> samples = evaluation.getSamples();
			setCategories(samples.getSamples(), sortedSampleIndices);
			adjustXAxisFont(evaluation.getSamples().getAnalysisSettings().getVariableLinePlotFontSize());
			List<ILineSeriesData> series;
			series = SeriesConverter.variableLineToSeries(samples, variable, categoryLabelType);
			addSeriesData(series);
			addHighlights(evaluation, sortedSampleIndices);

		}
		getBaseChart().redraw();
	}

	private void setCategories(List<ISample> samples, ArrayList<Integer> sortedSampleIndices) {

		ArrayList<String> categoryList = new ArrayList<>();
		for(Integer index : sortedSampleIndices) {
			ISample sample = samples.get(index);
			if(sample.isSelected()) {
				if(categoryLabelType.equals(FeatureColumnLabels.GROUPNAMES)) {
					categoryList.add(sample.getGroupName());
				} else {
					categoryList.add(sample.getSampleName());
				}

			}
		}
		String[] categories = new String[categoryList.size()];
		categoryList.toArray(categories);
		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setCategorySeries(categories);
		primaryAxisSettingsX.setEnableCategory(true);

		applySettings(chartSettings);

	}

	private void adjustXAxisFont(Long fontSize) {

		this.getBaseChart().getAxisSet().getXAxis(0).getTick().setTickLabelAngle(90);
		FontData fontData = this.getBaseChart().getAxisSet().getXAxis(0).getTick().getFont().getFontData()[0];
		fontData.height = fontSize;
		Font smallAxisFont = Resources.getFont(fontData);
		this.getBaseChart().getAxisSet().getXAxis(0).getTick().setFont(smallAxisFont);
	}

	public void setCategoryLabelType(FeatureColumnLabels labels) {

		this.categoryLabelType = labels;
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
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(true);
		rangeRestriction.setZeroY(true);
		rangeRestriction.setRestrictFrame(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.ABSOLUTE);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMaxY(0.1);
		rangeRestriction.setRestrictSelectX(true);
		rangeRestriction.setReferenceZoomZeroX(true);
		//
		chartSettings.setShowSeriesLabelMarker(false);
		chartSettings.setUseSeriesLabelDescription(true);
		chartSettings.setEnableCompress(false);
		//
		setPrimaryAxisSet(chartSettings);

		applySettings(chartSettings);

	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Samples");
		primaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Signal");
		primaryAxisSettingsY.setDecimalFormat(decimalFormat);
		primaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

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

	private void addHighlights(IEvaluation<IVariable, ISample, IResult> evaluation, ArrayList<Integer> sortedSampleIndices) {

		BaseChart baseChart = getBaseChart();
		IPlotArea plotArea = baseChart.getPlotArea();
		ISamplesPCA<IVariable, ISample> samples = evaluation.getSamples();
		List<ISample> highlightedSamples = evaluation.getHighlightedSamples();
		List<ISample> sortedActiveSamples = sortedSampleIndices.stream().map(x -> samples.getSamples().get(x)).filter(x -> x.isSelected()).toList();

		ArrayList<Integer> highlightedIndices = new ArrayList<>();
		for(int j = 0; j < sortedActiveSamples.size(); j++) {
			int current = j;
			if(highlightedSamples.stream().anyMatch(x -> x.equals(sortedActiveSamples.get(current)))) {
				highlightedIndices.add(current);
			}
		}

		int[] highlightIndices = highlightedIndices.stream().mapToInt(Integer::intValue).toArray();

		if(variableLinePlotHighlights != null) {
			plotArea.removeCustomPaintListener(variableLinePlotHighlights);
		}
		variableLinePlotHighlights = new VariableLinePlotHighlights(baseChart, highlightIndices);
		plotArea.addCustomPaintListener(variableLinePlotHighlights);

	}

}
