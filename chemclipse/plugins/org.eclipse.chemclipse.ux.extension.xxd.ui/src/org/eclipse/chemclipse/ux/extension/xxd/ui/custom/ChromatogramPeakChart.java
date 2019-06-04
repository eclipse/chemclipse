/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.IdentificationLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;

public class ChromatogramPeakChart extends ChromatogramChart {

	private static final String SERIES_ID_CHROMATOGRAM = "Chromatogram";
	private static final String SERIES_ID_PEAKS_NORMAL = "Peaks Normal";
	private static final String SERIES_ID_PEAKS_SELECTED = "Peaks Selected";
	//
	private PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private PeakChartSupport peakChartSupport = new PeakChartSupport();
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	//
	private Map<String, IdentificationLabelMarker> peakLabelMarkerMap = new HashMap<>();
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ChromatogramPeakChart() {
		super();
	}

	public ChromatogramPeakChart(Composite parent, int style) {
		super(parent, style);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void update(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection == null) {
			deleteSeries(SERIES_ID_CHROMATOGRAM);
			deleteSeries(SERIES_ID_PEAKS_NORMAL);
		} else {
			List<IPeak> peaks = chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection);
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			addChromatogramData(chromatogramSelection, lineSeriesDataList);
			addPeakData(peaks, lineSeriesDataList);
			addLineSeriesData(lineSeriesDataList);
		}
	}

	@SuppressWarnings("rawtypes")
	public void update(List<IPeak> selectedPeaks) {

		if(selectedPeaks == null || selectedPeaks.size() == 0) {
			deleteSeries(SERIES_ID_PEAKS_SELECTED);
		} else {
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			addPeakData(selectedPeaks, lineSeriesDataList);
			addLineSeriesData(lineSeriesDataList);
		}
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Define the compression level.
		 */
		String compressionType = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
		addSeriesData(lineSeriesDataList, compressionToLength);
	}

	@SuppressWarnings("rawtypes")
	private void addChromatogramData(IChromatogramSelection chromatogramSelection, List<ILineSeriesData> lineSeriesDataList) {

		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM));
		boolean enableChromatogramArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA);
		//
		ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogramSelection, SERIES_ID_CHROMATOGRAM, DisplayType.TIC, color, false);
		lineSeriesData.getSettings().setEnableArea(enableChromatogramArea);
		lineSeriesDataList.add(lineSeriesData);
	}

	private void addPeakData(List<IPeak> peaks, List<ILineSeriesData> lineSeriesDataList) {

		int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
		addPeaks(lineSeriesDataList, peaks, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, Colors.DARK_GRAY, SERIES_ID_PEAKS_NORMAL);
	}

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(peaks.size() > 0) {
			//
			Collections.sort(peaks, peakRetentionTimeComparator);
			ILineSeriesData lineSeriesData = peakChartSupport.getPeaks(peaks, true, false, symbolColor, seriesId);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Add the labels.
			 */
			removeIdentificationLabelMarker(peakLabelMarkerMap, seriesId);
			boolean showChromatogramPeakLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS);
			if(showChromatogramPeakLabels) {
				IPlotArea plotArea = (IPlotArea)getBaseChart().getPlotArea();
				int indexSeries = lineSeriesDataList.size() - 1;
				IdentificationLabelMarker peakLabelMarker = new IdentificationLabelMarker(getBaseChart(), indexSeries, peaks, null);
				plotArea.addCustomPaintListener(peakLabelMarker);
				peakLabelMarkerMap.put(seriesId, peakLabelMarker);
			}
		}
	}

	private void removeIdentificationLabelMarker(Map<String, IdentificationLabelMarker> markerMap, String seriesId) {

		IPlotArea plotArea = (IPlotArea)getBaseChart().getPlotArea();
		IdentificationLabelMarker labelMarker = markerMap.get(seriesId);
		/*
		 * Remove the label marker.
		 */
		if(labelMarker != null) {
			plotArea.removeCustomPaintListener(labelMarker);
		}
	}
}
