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
 * Christoph Läubrich - Adjust API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.PreferenceStoreTargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;

public class ChromatogramPeakChart extends ChromatogramChart {

	private static final String SERIES_ID_CHROMATOGRAM = "Chromatogram";
	private static final String SERIES_ID_PEAKS_NORMAL = "Peaks Normal";
	private static final String SERIES_ID_PEAKS_SELECTED_MARKER = "Peaks Selected Marker";
	private static final String SERIES_ID_PEAKS_SELECTED_SHAPE = "Peaks Selected Shape";
	private static final String SERIES_ID_PEAKS_SELECTED_BACKGROUND = "Peaks Selected Background";
	//
	private final PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private final PeakChartSupport peakChartSupport = new PeakChartSupport();
	private final ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	//
	private final Map<String, IdentificationLabelMarker> peakLabelMarkerMap = new HashMap<>();
	private final Set<String> selectedPeakIds = new HashSet<>();
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ChromatogramPeakChart() {
		super();
		init();
	}

	public ChromatogramPeakChart(Composite parent, int style) {
		super(parent, style);
		init();
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

	public void update(List<IPeak> selectedPeaks) {

		clearSelectedPeakSeries();
		if(selectedPeaks != null && selectedPeaks.size() > 0) {
			int index = 1;
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			for(IPeak peak : selectedPeaks) {
				addSelectedPeak(peak, lineSeriesDataList, index++);
			}
			addLineSeriesData(lineSeriesDataList);
		}
	}

	private void init() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.getRangeRestriction().setRestrictZoom(true);
		applySettings(chartSettings);
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
		addPeaks(lineSeriesDataList, peaks, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, Colors.DARK_GRAY, SERIES_ID_PEAKS_NORMAL, true);
	}

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId, boolean addLabelMarker) {

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
			if(addLabelMarker) {
				removeIdentificationLabelMarker(peakLabelMarkerMap, seriesId);
				TargetDisplaySettings settings = PreferenceStoreTargetDisplaySettings.getSettings(preferenceStore);
				if(settings.isShowPeakLabels()) {
					IPlotArea plotArea = getBaseChart().getPlotArea();
					int indexSeries = lineSeriesDataList.size() - 1;
					IdentificationLabelMarker peakLabelMarker = new IdentificationLabelMarker(getBaseChart(), indexSeries, peaks, IdentificationLabelMarker.getPeakFont(getDisplay()), settings);
					plotArea.addCustomPaintListener(peakLabelMarker);
					peakLabelMarkerMap.put(seriesId, peakLabelMarker);
				}
			}
		}
	}

	private void addSelectedPeak(IPeak peak, List<ILineSeriesData> lineSeriesDataList, int index) {

		if(peak != null) {
			/*
			 * Settings
			 */
			boolean mirrored = false;
			Color colorPeak = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_PEAK));
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			PlotSymbolType symbolTypePeakMarker = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE));
			int scanMarkerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_SIZE);
			PlotSymbolType symbolTypeScanMarker = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_TYPE));
			/*
			 * Peak Marker
			 */
			String peakMarkerId = getSelectedPeakSerieId(SERIES_ID_PEAKS_SELECTED_MARKER, index);
			List<IPeak> peaks = new ArrayList<>();
			peaks.add(peak);
			addPeaks(lineSeriesDataList, peaks, symbolTypePeakMarker, symbolSize, colorPeak, peakMarkerId, false);
			selectedPeakIds.add(peakMarkerId);
			/*
			 * Peak
			 */
			String peakShapeId = getSelectedPeakSerieId(SERIES_ID_PEAKS_SELECTED_SHAPE, index);
			ILineSeriesData lineSeriesData = peakChartSupport.getPeak(peak, true, mirrored, colorPeak, peakShapeId);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setSymbolType(symbolTypeScanMarker);
			lineSeriesSettings.setSymbolColor(colorPeak);
			lineSeriesSettings.setSymbolSize(scanMarkerSize);
			lineSeriesDataList.add(lineSeriesData);
			selectedPeakIds.add(peakShapeId);
			/*
			 * Background
			 */
			String peakBackgroundId = getSelectedPeakSerieId(SERIES_ID_PEAKS_SELECTED_BACKGROUND, index);
			Color colorBackground = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_BACKGROUND));
			lineSeriesData = peakChartSupport.getPeakBackground(peak, mirrored, colorBackground, peakBackgroundId);
			lineSeriesDataList.add(lineSeriesData);
			selectedPeakIds.add(peakBackgroundId);
		}
	}

	private void removeIdentificationLabelMarker(Map<String, IdentificationLabelMarker> markerMap, String seriesId) {

		IPlotArea plotArea = getBaseChart().getPlotArea();
		IdentificationLabelMarker labelMarker = markerMap.get(seriesId);
		/*
		 * Remove the label marker.
		 */
		if(labelMarker != null) {
			plotArea.removeCustomPaintListener(labelMarker);
		}
	}

	private String getSelectedPeakSerieId(String id, int index) {

		return id + " (" + index + ")";
	}

	private void clearSelectedPeakSeries() {

		for(String id : selectedPeakIds) {
			deleteSeries(id);
		}
		selectedPeakIds.clear();
	}
}
