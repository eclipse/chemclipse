/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
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
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.TargetReferenceLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;

public class ChromatogramPeakChart extends ChromatogramChart implements ITraceSupport {

	private static final String SERIES_ID_CHROMATOGRAM_TIC = "Chromatogram";
	private static final String SERIES_ID_CHROMATOGRAM_XIC = "Chromatogram (XIC)";
	private static final String SERIES_ID_CHROMATOGRAM_SWC = "Chromatogram (SWC)";
	private static final String SERIES_ID_BASELINE = "Baseline";
	private static final String SERIES_ID_PEAKS_NORMAL_ACTIVE = "Peaks [Active]";
	private static final String SERIES_ID_PEAKS_NORMAL_INACTIVE = "Peaks [Inactive]";
	private static final String SERIES_ID_PEAKS_ISTD_ACTIVE = "Peaks ISTD [Active]";
	private static final String SERIES_ID_PEAKS_ISTD_INACTIVE = "Peaks ISTD [Inactive]";
	private static final String SERIES_ID_PEAKS_SELECTED_MARKER = "Peaks Selected Marker";
	private static final String SERIES_ID_PEAKS_SELECTED_SHAPE = "Peaks Selected Shape";
	private static final String SERIES_ID_SELECTED_SCAN = "Selected Scan";
	private static final String SERIES_ID_IDENTIFIED_SCANS = "Identified Scans";
	private static final String SERIES_ID_IDENTIFIED_SCAN_SELECTED = "Identified Scans Selected";
	//
	private final PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private final PeakChartSupport peakChartSupport = new PeakChartSupport();
	private final ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private final ScanChartSupport scanChartSupport = new ScanChartSupport();
	//
	private final Map<String, TargetReferenceLabelMarker> peakLabelMarkerMap = new HashMap<>();
	private final Map<String, TargetReferenceLabelMarker> scanLabelMarkerMap = new HashMap<>();
	private final Set<String> selectedPeakIds = new HashSet<>();
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private PeakChartSettings peakChartSettingsDefault = new PeakChartSettings();
	//
	private ITargetDisplaySettings targetDisplaySettings = null;
	private DisplayType displayType = DisplayType.TIC;
	//
	private IChromatogramSelection<?, ?> chromatogramSelection;

	public ChromatogramPeakChart() {

		super();
		initialize();
	}

	public ChromatogramPeakChart(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	public void updateChromatogram(IChromatogramSelection<?, ?> chromatogramSelection) {

		updateChromatogram(chromatogramSelection, peakChartSettingsDefault);
	}

	public void updateChromatogram(IChromatogramSelection<?, ?> chromatogramSelection, PeakChartSettings peakChartSettings) {

		this.chromatogramSelection = chromatogramSelection;
		//
		clearSeries();
		clearSelectedPeakSeries();
		clearPeakLabelMarker();
		targetDisplaySettings = null;
		//
		if(chromatogramSelection != null) {
			/*
			 * Add series
			 */
			targetDisplaySettings = chromatogramSelection.getChromatogram();
			List<? extends IPeak> peaks = chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection);
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			addChromatogramData(lineSeriesDataList, peakChartSettings);
			addBaselineData(lineSeriesDataList, peakChartSettings);
			addPeakData(peaks, lineSeriesDataList);
			addIdentifiedScansData(lineSeriesDataList, targetDisplaySettings);
			addSelectedScanData(lineSeriesDataList);
			addSelectedIdentifiedScanData(lineSeriesDataList);
			addLineSeriesData(lineSeriesDataList);
		}
	}

	public void updatePeaks(List<IPeak> peaks) {

		updatePeaks(peaks, false);
	}

	public void updatePeaks(List<IPeak> peaks, boolean hideExistingPeaks) {

		/*
		 * Hide the existing peak series on demand.
		 */
		boolean visible = !hideExistingPeaks;
		setPeakSeriesVisibility(visible);
		setPeakLabelMarkerVisibility(visible);
		/*
		 * Set the selected peak.
		 */
		clearSelectedPeakSeries();
		if(peaks != null && !peaks.isEmpty()) {
			int index = 1;
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			for(IPeak peak : peaks) {
				addSelectedPeak(peak, lineSeriesDataList, index++);
			}
			addLineSeriesData(lineSeriesDataList);
		}
	}

	public void updateSelectedScan() {

		clearScanSeries();
		assignCurrentRangeSelection();
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		addIdentifiedScansData(lineSeriesDataList, targetDisplaySettings);
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
		adjustChartRange();
	}

	@Override
	public void focusTraces(int percentOffset) {

		BaseChart baseChart = getBaseChart();
		ISeries<?> series = baseChart.getSeriesSet().getSeries(ChromatogramPeakChart.SERIES_ID_CHROMATOGRAM_XIC);
		if(series == null) {
			series = baseChart.getSeriesSet().getSeries(ChromatogramPeakChart.SERIES_ID_CHROMATOGRAM_SWC);
		}
		//
		if(series != null && series.getXSeries().length > 0) {
			double maxY = getMaxY(series);
			if(maxY > 0) {
				maxY += maxY * percentOffset / 100.0d;
				IAxisSet axisSet = baseChart.getAxisSet();
				IAxis yAxis = axisSet.getYAxis(BaseChart.ID_PRIMARY_Y_AXIS);
				Range selectedRangeY = new Range(yAxis.getRange().lower, maxY);
				updateRangeY(selectedRangeY);
			}
		}
	}

	private double getMaxY(ISeries<?> series) {

		Range selectedRangeX = getCurrentRangeX();
		if(selectedRangeX == null) {
			return Calculations.getMax(series.getYSeries());
		} else {
			double[] xSeries = series.getXSeries();
			double[] ySeries = series.getYSeries();
			double start = selectedRangeX.lower;
			double stop = selectedRangeX.upper;
			double maxY = Double.MIN_VALUE;
			//
			for(int i = 0; i < xSeries.length; i++) {
				double x = xSeries[i];
				if(x >= start && x <= stop) {
					maxY = Math.max(maxY, ySeries[i]);
				}
			}
			//
			return maxY;
		}
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		chartSettings.getRangeRestriction().setRestrictFrame(true);
		rangeRestriction.setExtendMaxY(preferenceStore.getDouble(PreferenceSupplier.P_CHROMATOGRAM_EXTEND_Y));
		applySettings(chartSettings);
		setData("org.eclipse.e4.ui.css.CssClassName", "ChromatogramPeakChart");
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Define the compression level.
		 */
		String compressionType = preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
		addSeriesData(lineSeriesDataList, compressionToLength);
	}

	private void addChromatogramData(List<ILineSeriesData> lineSeriesDataList, PeakChartSettings peakChartSettings) {

		if(chromatogramSelection != null) {
			boolean containsTraces = containsTraces(chromatogramSelection);
			Color colorActive = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_CHROMATOGRAM));
			Color colorInactive = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_CHROMATOGRAM_INACTIVE));
			Color colorTIC = containsTraces ? colorInactive : colorActive;
			boolean enableChromatogramArea = preferenceStore.getBoolean(PreferenceSupplier.P_ENABLE_CHROMATOGRAM_AREA);
			/*
			 * TIC
			 */
			ILineSeriesData lineSeriesDataTIC = chromatogramChartSupport.getLineSeriesData(chromatogramSelection, SERIES_ID_CHROMATOGRAM_TIC, DisplayType.TIC, colorTIC, false);
			ILineSeriesSettings settingsTIC = lineSeriesDataTIC.getSettings();
			settingsTIC.setEnableArea(enableChromatogramArea);
			settingsTIC.setVisible(peakChartSettings.isShowChromatogramTIC());
			lineSeriesDataList.add(lineSeriesDataTIC);
			//
			if(containsTraces) {
				if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
					/*
					 * XIC
					 */
					ILineSeriesData lineSeriesDataXIC = chromatogramChartSupport.getLineSeriesData(chromatogramSelectionMSD, SERIES_ID_CHROMATOGRAM_XIC, DisplayType.XIC, colorActive, false);
					ILineSeriesSettings settingsXIC = lineSeriesDataXIC.getSettings();
					settingsXIC.setEnableArea(enableChromatogramArea);
					settingsXIC.setVisible(peakChartSettings.isShowChromatogramTraces());
					lineSeriesDataList.add(lineSeriesDataXIC);
				} else if(chromatogramSelection instanceof IChromatogramSelectionWSD chromatogramSelectionWSD) {
					/*
					 * SWC
					 */
					ILineSeriesData lineSeriesDataSWC = chromatogramChartSupport.getLineSeriesData(chromatogramSelectionWSD, SERIES_ID_CHROMATOGRAM_SWC, DisplayType.SWC, colorActive, false);
					ILineSeriesSettings settingsSWC = lineSeriesDataSWC.getSettings();
					settingsSWC.setEnableArea(enableChromatogramArea);
					settingsSWC.setVisible(peakChartSettings.isShowChromatogramTraces());
					lineSeriesDataList.add(lineSeriesDataSWC);
				}
			}
		}
	}

	private boolean containsTraces(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
			if(!chromatogramSelectionMSD.getSelectedIons().isEmpty()) {
				return true;
			}
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD chromatogramSelectionWSD) {
			if(!chromatogramSelectionWSD.getSelectedWavelengths().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void addBaselineData(List<ILineSeriesData> lineSeriesDataList, PeakChartSettings peakChartSettings) {

		boolean showChromatogramBaseline = preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_CHROMATOGRAM_BASELINE);
		//
		if(chromatogramSelection != null && showChromatogramBaseline) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_CHROMATOGRAM_BASELINE));
			boolean enableBaselineArea = preferenceStore.getBoolean(PreferenceSupplier.P_ENABLE_BASELINE_AREA);
			ILineSeriesData lineSeriesData = null;
			lineSeriesData = chromatogramChartSupport.getLineSeriesDataBaseline(chromatogramSelection, SERIES_ID_BASELINE, DisplayType.TIC, color, false);
			ILineSeriesSettings settings = lineSeriesData.getSettings();
			settings.setEnableArea(enableBaselineArea);
			settings.setVisible(peakChartSettings.isShowBaseline());
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addPeakData(List<? extends IPeak> peaks, List<ILineSeriesData> lineSeriesDataList) {

		int symbolSize = preferenceStore.getInt(PreferenceSupplier.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
		PlotSymbolType symbolTypeActiveNormal = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL_MARKER_TYPE));
		PlotSymbolType symbolTypeInactiveNormal = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_PEAKS_INACTIVE_NORMAL_MARKER_TYPE));
		PlotSymbolType symbolTypeActiveIstd = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_PEAKS_ACTIVE_ISTD_MARKER_TYPE));
		PlotSymbolType symbolTypeInactiveIstd = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_PEAKS_INACTIVE_ISTD_MARKER_TYPE));
		//
		List<IPeak> peaksActiveNormal = new ArrayList<>();
		List<IPeak> peaksInactiveNormal = new ArrayList<>();
		List<IPeak> peaksActiveISTD = new ArrayList<>();
		List<IPeak> peaksInactiveISTD = new ArrayList<>();
		//
		for(IPeak peak : peaks) {
			if(!peak.getInternalStandards().isEmpty()) {
				if(peak.isActiveForAnalysis()) {
					peaksActiveISTD.add(peak);
				} else {
					peaksInactiveISTD.add(peak);
				}
			} else {
				if(peak.isActiveForAnalysis()) {
					peaksActiveNormal.add(peak);
				} else {
					peaksInactiveNormal.add(peak);
				}
			}
		}
		//
		boolean addLabelMarker = true;
		addPeaks(lineSeriesDataList, peaksActiveNormal, symbolTypeActiveNormal, symbolSize, Colors.DARK_GRAY, SERIES_ID_PEAKS_NORMAL_ACTIVE, addLabelMarker);
		addPeaks(lineSeriesDataList, peaksInactiveNormal, symbolTypeInactiveNormal, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_NORMAL_INACTIVE, addLabelMarker);
		addPeaks(lineSeriesDataList, peaksActiveISTD, symbolTypeActiveIstd, symbolSize, Colors.RED, SERIES_ID_PEAKS_ISTD_ACTIVE, addLabelMarker);
		addPeaks(lineSeriesDataList, peaksInactiveISTD, symbolTypeInactiveIstd, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_ISTD_INACTIVE, addLabelMarker);
	}

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId, boolean addLabelMarker) {

		if(!peaks.isEmpty()) {
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
				if(targetDisplaySettings != null && targetDisplaySettings.isShowPeakLabels()) {
					BaseChart baseChart = getBaseChart();
					IPlotArea plotArea = baseChart.getPlotArea();
					TargetReferenceLabelMarker peakLabelMarker = new TargetReferenceLabelMarker(TargetReference.getPeakReferences(peaks, targetDisplaySettings), targetDisplaySettings, symbolSize * 2);
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
			Color colorPeak = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_CHROMATOGRAM_SELECTED_PEAK));
			int symbolSize = preferenceStore.getInt(PreferenceSupplier.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			PlotSymbolType symbolTypePeakMarker = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE));
			int scanMarkerSize = preferenceStore.getInt(PreferenceSupplier.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_SIZE);
			PlotSymbolType symbolTypeScanMarker = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_TYPE));
			/*
			 * Peak Marker
			 */
			String peakMarkerId = getSelectedPeakSerieId(SERIES_ID_PEAKS_SELECTED_MARKER, index);
			List<IPeak> peaks = new ArrayList<>();
			peaks.add(peak);
			addPeaks(lineSeriesDataList, peaks, symbolTypePeakMarker, symbolSize, colorPeak, peakMarkerId, true);
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
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, ITargetDisplaySettings displaySettings) {

		if(chromatogramSelection != null) {
			String seriesId = SERIES_ID_IDENTIFIED_SCANS;
			List<IScan> scans = ChromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram());
			int symbolSize = preferenceStore.getInt(PreferenceSupplier.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_SCAN_MARKER_TYPE));
			addIdentifiedScansData(chromatogramSelection, lineSeriesDataList, scans, symbolType, symbolSize, Colors.DARK_GRAY, seriesId);
			/*
			 * Add the labels.
			 */
			removeIdentificationLabelMarker(scanLabelMarkerMap, seriesId);
			if(displaySettings.isShowScanLabels()) {
				ITargetDisplaySettings targetDisplaySettings = chromatogramSelection.getChromatogram();
				BaseChart baseChart = getBaseChart();
				IPlotArea plotArea = baseChart.getPlotArea();
				TargetReferenceLabelMarker scanLabelMarker = new TargetReferenceLabelMarker(TargetReference.getScanReferences(scans, targetDisplaySettings), displaySettings, symbolSize * 2);
				plotArea.addCustomPaintListener(scanLabelMarker);
				scanLabelMarkerMap.put(seriesId, scanLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(IChromatogramSelection<?, ?> chromatogramSelection, List<ILineSeriesData> lineSeriesDataList, List<IScan> scans, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(!scans.isEmpty()) {
			ILineSeriesData lineSeriesData = null;
			lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scans, false, seriesId, displayType, chromatogramSelection);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addSelectedScanData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			IScan scan = chromatogramSelection.getSelectedScan();
			if(scan != null) {
				Color color = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_CHROMATOGRAM_SELECTED_SCAN));
				int markerSize = preferenceStore.getInt(PreferenceSupplier.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE);
				PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE));
				ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scan, false, SERIES_ID_SELECTED_SCAN, displayType, chromatogramSelection);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setLineStyle(LineStyle.NONE);
				lineSeriesSettings.setSymbolType(symbolType);
				lineSeriesSettings.setSymbolSize(markerSize);
				lineSeriesSettings.setSymbolColor(color);
				lineSeriesDataList.add(lineSeriesData);
			}
		}
	}

	private void addSelectedIdentifiedScanData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			List<IScan> selectedIdentifiedScans = chromatogramSelection.getSelectedIdentifiedScans();
			if(!selectedIdentifiedScans.isEmpty()) {
				String seriesId = SERIES_ID_IDENTIFIED_SCAN_SELECTED;
				Color color = Colors.getColor(preferenceStore.getString(PreferenceSupplier.P_COLOR_CHROMATOGRAM_IDENTIFIED_SCAN));
				PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_IDENTIFIED_SCAN_MARKER_TYPE));
				int symbolSize = preferenceStore.getInt(PreferenceSupplier.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
				addIdentifiedScansData(chromatogramSelection, lineSeriesDataList, selectedIdentifiedScans, symbolType, symbolSize, color, seriesId);
			}
		}
	}

	private void removeIdentificationLabelMarker(Map<String, ? extends ICustomPaintListener> markerMap, String seriesId) {

		IPlotArea plotArea = getBaseChart().getPlotArea();
		ICustomPaintListener labelMarker = markerMap.get(seriesId);
		markerMap.remove(seriesId);
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

	private void clearSeries() {

		clearChromatogramSeries();
		clearPeakSeries();
		clearScanSeries();
	}

	private void clearChromatogramSeries() {

		deleteSeries(SERIES_ID_CHROMATOGRAM_TIC);
		deleteSeries(SERIES_ID_CHROMATOGRAM_XIC);
		deleteSeries(SERIES_ID_CHROMATOGRAM_SWC);
		deleteSeries(SERIES_ID_BASELINE);
	}

	private void clearPeakSeries() {

		deleteSeries(SERIES_ID_PEAKS_NORMAL_ACTIVE);
		deleteSeries(SERIES_ID_PEAKS_NORMAL_INACTIVE);
		deleteSeries(SERIES_ID_PEAKS_ISTD_ACTIVE);
		deleteSeries(SERIES_ID_PEAKS_ISTD_INACTIVE);
	}

	private void clearScanSeries() {

		deleteSeries(SERIES_ID_SELECTED_SCAN);
		deleteSeries(SERIES_ID_IDENTIFIED_SCANS);
		deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
	}

	private void setPeakSeriesVisibility(boolean visible) {

		BaseChart baseChart = getBaseChart();
		setVisibility(baseChart, SERIES_ID_PEAKS_NORMAL_ACTIVE, visible);
		setVisibility(baseChart, SERIES_ID_PEAKS_NORMAL_ACTIVE, visible);
		setVisibility(baseChart, SERIES_ID_PEAKS_NORMAL_INACTIVE, visible);
		setVisibility(baseChart, SERIES_ID_PEAKS_ISTD_ACTIVE, visible);
		setVisibility(baseChart, SERIES_ID_PEAKS_ISTD_INACTIVE, visible);
		baseChart.applySeriesSettings();
	}

	private void clearSelectedPeakSeries() {

		for(String seriesId : selectedPeakIds) {
			deleteSeries(seriesId);
			removeIdentificationLabelMarker(peakLabelMarkerMap, seriesId);
		}
		//
		selectedPeakIds.clear();
	}

	private void clearPeakLabelMarker() {

		/*
		 * Clear the label marker.
		 */
		Set<String> seriesIds = new HashSet<>(peakLabelMarkerMap.keySet());
		for(String seriesId : seriesIds) {
			removeIdentificationLabelMarker(peakLabelMarkerMap, seriesId);
		}
		/*
		 * Clear the maps.
		 */
		peakLabelMarkerMap.clear();
	}

	private void setPeakLabelMarkerVisibility(boolean visible) {

		for(TargetReferenceLabelMarker labelMarker : peakLabelMarkerMap.values()) {
			labelMarker.setVisible(visible);
		}
	}

	private void setVisibility(BaseChart baseChart, String seriesId, boolean visibile) {

		ISeriesSettings seriesSettings = baseChart.getSeriesSettings(seriesId);
		if(seriesSettings != null) {
			seriesSettings.setVisible(visibile);
		}
	}
}