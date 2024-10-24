/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.chart2d;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IResultsPCA;
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

public class ErrorResidueChart extends BarChart {

	public ErrorResidueChart() {

		super();
		createControl();
	}

	public ErrorResidueChart(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(EvaluationPCA evaluationPCA) {

		if(evaluationPCA != null) {
			IResultsPCA<?, ?> resultsPCA = evaluationPCA.getResults();
			updateChart(resultsPCA);
		} else {
			updateChart(null);
		}
	}

	private void createControl() {

		IChartSettings chartSettings = getChartSettings();
		//
		chartSettings.setTitle("Error Residues");
		chartSettings.setTitleVisible(true);
		if(PreferencesSupport.isDarkTheme()) {
			chartSettings.setTitleColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			chartSettings.setTitleColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
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
		if(PreferencesSupport.isDarkTheme()) {
			chartSettings.setColorAxisZeroMarker(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			chartSettings.setColorAxisZeroMarker(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
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
		primaryAxisSettingsX.setTitle("Sample Name");
		primaryAxisSettingsX.setDecimalFormat(ValueFormat.getDecimalFormatEnglish());
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Error Values");
		primaryAxisSettingsY.setDecimalFormat(ValueFormat.getDecimalFormatEnglish());
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
	}

	private void updateChart(IResultsPCA<?, ?> pcaResults) {

		deleteSeries();
		if(pcaResults != null) {
			//
			IChartSettings chartSettings = getChartSettings();
			IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
			primaryAxisSettingsX.setEnableCategory(true);
			primaryAxisSettingsX.setCategorySeries(getCategories(pcaResults));
			applySettings(chartSettings);
			//
			List<IBarSeriesData> barSeriesDataList = new ArrayList<>();
			ISeriesData seriesData = getSeries(pcaResults);
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

	private String[] getCategories(IResultsPCA<? extends IResultPCA, ?> pcaResults) {

		List<? extends IResultPCA> pcaResultList = pcaResults.getPcaResultList();
		int size = pcaResultList.size();
		String[] categories = new String[size];
		//
		for(int i = 0; i < size; i++) {
			IResultPCA pcaResult = pcaResultList.get(i);
			categories[i] = pcaResult.getSample().getSampleName();
		}
		//
		return categories;
	}

	private ISeriesData getSeries(IResultsPCA<? extends IResultPCA, ?> pcaResults) {

		List<? extends IResultPCA> pcaResultList = pcaResults.getPcaResultList();
		int size = pcaResultList.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		for(int i = 0; i < size; i++) {
			IResultPCA pcaResult = pcaResultList.get(i);
			xSeries[i] = i;
			ySeries[i] = pcaResult.getErrorMemberShip();
		}
		//
		return new SeriesData(xSeries, ySeries, "Error Residues");
	}
}
