/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.extensions.barcharts.BarChart;
import org.eclipse.swtchart.extensions.barcharts.BarSeriesData;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesData;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SeriesData;

public class ExplainedVarianceChart extends BarChart {

	public ExplainedVarianceChart() {
		super();
		createControl();
	}

	public ExplainedVarianceChart(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	@SuppressWarnings("rawtypes")
	public void setInput(EvaluationPCA evaluationPCA) {

		if(evaluationPCA != null) {
			IResultsPCA resultsPCA = evaluationPCA.getResults();
			updateChart(resultsPCA);
		} else {
			updateChart(null);
		}
	}

	private void createControl() {

		IChartSettings chartSettings = getChartSettings();
		//
		chartSettings.setTitle("Explained Variance");
		chartSettings.setTitleVisible(true);
		chartSettings.setTitleColor(Colors.BLACK);
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(false);
		chartSettings.setVerticalSliderVisible(false);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(false);
		rangeRestriction.setZeroY(false);
		rangeRestriction.setForceZeroMinY(true);
		rangeRestriction.setRestrictFrame(true);
		//
		chartSettings.setShowAxisZeroMarker(true);
		chartSettings.setColorAxisZeroMarker(Colors.BLACK);
		chartSettings.setShowSeriesLabelMarker(false);
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableCompress(false);
		//
		setPrimaryAxisSet(chartSettings);
		//
		applySettings(chartSettings);
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Principal Component");
		primaryAxisSettingsX.setDecimalFormat(ValueFormat.getDecimalFormatEnglish());
		primaryAxisSettingsX.setColor(Colors.BLACK);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Explained Variance (Cumulative)");
		primaryAxisSettingsY.setDecimalFormat(ValueFormat.getDecimalFormatEnglish());
		primaryAxisSettingsY.setColor(Colors.BLACK);
	}

	@SuppressWarnings("rawtypes")
	private void updateChart(IResultsPCA resultsPCA) {

		deleteSeries();
		if(resultsPCA != null) {
			//
			IChartSettings chartSettings = getChartSettings();
			IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
			primaryAxisSettingsX.setEnableCategory(true);
			primaryAxisSettingsX.setCategorySeries(getCategories(resultsPCA));
			applySettings(chartSettings);
			//
			List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
			ISeriesData seriesData = getSeries(resultsPCA);
			IBarSeriesData barSeriesData = new BarSeriesData(seriesData);
			IBarSeriesSettings settings = barSeriesData.getSettings();
			settings.setBarColor(Colors.RED);
			settings.setBarWidthStyle(BarWidthStyle.STRETCHED);
			barSeriesDataList.add(barSeriesData);
			addSeriesData(barSeriesDataList);
		} else {
			getBaseChart().redraw();
		}
	}

	@SuppressWarnings("rawtypes")
	private String[] getCategories(IResultsPCA pcaResults) {

		int size = pcaResults.getCumulativeExplainedVariances().length;
		String[] categories = new String[size];
		//
		for(int i = 0; i < size; i++) {
			categories[i] = "PC" + (i + 1);
		}
		//
		return categories;
	}

	@SuppressWarnings("rawtypes")
	private ISeriesData getSeries(IResultsPCA pcaResults) {

		double[] ySeries = pcaResults.getCumulativeExplainedVariances();
		double[] xSeries = new double[ySeries.length];
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, "Explained Variances (Cumulative)");
		return seriesData;
	}
}
