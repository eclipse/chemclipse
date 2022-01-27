/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
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
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;

public class ChromatogramPeakChart extends ChromatogramChart implements IRangeSupport {

	private static final String SERIES_ID_CHROMATOGRAM_TIC = "Chromatogram";
	private static final String SERIES_ID_CHROMATOGRAM_XIC = "Chromatogram (XIC)";
	private static final String SERIES_ID_CHROMATOGRAM_SWC = "Chromatogram (SWC)";
	private static final String SERIES_ID_BASELINE = "Baseline";
	private static final String SERIES_ID_PEAKS_NORMAL_ACTIVE = "Peak(s) [Active]";
	private static final String SERIES_ID_PEAKS_NORMAL_INACTIVE = "Peak(s) [Inactive]";
	private static final String SERIES_ID_PEAKS_ISTD_ACTIVE = "Peak(s) ISTD [Active]";
	private static final String SERIES_ID_PEAKS_ISTD_INACTIVE = "Peak(s) ISTD [Inactive]";
	private static final String SERIES_ID_PEAKS_SELECTED_MARKER = "Peaks Selected Marker";
	private static final String SERIES_ID_PEAKS_SELECTED_SHAPE = "Peaks Selected Shape";
	private static final String SERIES_ID_PEAKS_SELECTED_BACKGROUND = "Peaks Selected Background";
	private static final String SERIES_ID_SELECTED_SCAN = "Selected Scan";
	private static final String SERIES_ID_IDENTIFIED_SCANS = "Identified Scans";
	private static final String SERIES_ID_IDENTIFIED_SCAN_SELECTED = "Identified Scans Selected";
	//
	private final PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private final PeakChartSupport peakChartSupport = new PeakChartSupport();
	private final ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private final ScanChartSupport scanChartSupport = new ScanChartSupport();
	//
	private final Map<String, ICustomPaintListener> peakLabelMarkerMap = new HashMap<>();
	private final Map<String, TargetReferenceLabelMarker> scanLabelMarkerMap = new HashMap<>();
	private final Set<String> selectedPeakIds = new HashSet<>();
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private PeakChartSettings peakChartSettingsDefault = new PeakChartSettings();
	//
	private Range selectedRangeX = null;
	private Range selectedRangeY = null;
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void updateChromatogram(IChromatogramSelection chromatogramSelection, PeakChartSettings peakChartSettings) {

		this.chromatogramSelection = chromatogramSelection;
		//
		clearChromatogramSeries();
		clearSelectedPeakSeries();
		clearPeakLabelMarker();
		targetDisplaySettings = null;
		//
		if(chromatogramSelection != null) {
			/*
			 * Add series
			 */
			targetDisplaySettings = chromatogramSelection.getChromatogram();
			List<IPeak> peaks = chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection);
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

		clearSelectedPeakSeries();
		//
		if(peaks != null && peaks.size() > 0) {
			int index = 1;
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			for(IPeak peak : peaks) {
				addPeak(peak, lineSeriesDataList, index++);
			}
			addLineSeriesData(lineSeriesDataList);
		}
	}

	public void updateSelectedScan() {

		deleteSeries(SERIES_ID_SELECTED_SCAN);
		deleteSeries(SERIES_ID_IDENTIFIED_SCANS);
		deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
		//
		assignCurrentRangeSelection();
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		addIdentifiedScansData(lineSeriesDataList, targetDisplaySettings);
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
		adjustChartRange();
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

	@Override
	public void adjustChartRange() {

		setRange(IExtendedChart.X_AXIS, selectedRangeX);
		setRange(IExtendedChart.Y_AXIS, selectedRangeY);
		redrawChart();
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		chartSettings.getRangeRestriction().setRestrictFrame(true);
		rangeRestriction.setExtendMaxY(preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_EXTEND_Y));
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

	private void addChromatogramData(List<ILineSeriesData> lineSeriesDataList, PeakChartSettings peakChartSettings) {

		if(chromatogramSelection != null) {
			boolean containsTraces = containsTraces(chromatogramSelection);
			Color colorActive = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM));
			Color colorInactive = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_INACTIVE));
			Color colorTIC = containsTraces ? colorInactive : colorActive;
			boolean enableChromatogramArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA);
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
				if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
					/*
					 * XIC
					 */
					ILineSeriesData lineSeriesDataXIC = chromatogramChartSupport.getLineSeriesData((IChromatogramSelectionMSD)chromatogramSelection, SERIES_ID_CHROMATOGRAM_XIC, DisplayType.XIC, colorActive, false);
					ILineSeriesSettings settingsXIC = lineSeriesDataXIC.getSettings();
					settingsXIC.setEnableArea(enableChromatogramArea);
					settingsXIC.setVisible(peakChartSettings.isShowChromatogramTraces());
					lineSeriesDataList.add(lineSeriesDataXIC);
				} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
					/*
					 * SWC
					 */
					ILineSeriesData lineSeriesDataSWC = chromatogramChartSupport.getLineSeriesData((IChromatogramSelectionWSD)chromatogramSelection, SERIES_ID_CHROMATOGRAM_SWC, DisplayType.SWC, colorActive, false);
					ILineSeriesSettings settingsSWC = lineSeriesDataSWC.getSettings();
					settingsSWC.setEnableArea(enableChromatogramArea);
					settingsSWC.setVisible(peakChartSettings.isShowChromatogramTraces());
					lineSeriesDataList.add(lineSeriesDataSWC);
				}
			}
		}
	}

	private boolean containsTraces(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			if(chromatogramSelectionMSD.getSelectedIons().size() > 0) {
				return true;
			}
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
			if(chromatogramSelectionWSD.getSelectedWavelengths().size() > 0) {
				return true;
			}
		}
		return false;
	}

	private void addBaselineData(List<ILineSeriesData> lineSeriesDataList, PeakChartSettings peakChartSettings) {

		boolean showChromatogramBaseline = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_BASELINE);
		//
		if(chromatogramSelection != null && showChromatogramBaseline) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_BASELINE));
			boolean enableBaselineArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_BASELINE_AREA);
			ILineSeriesData lineSeriesData = null;
			lineSeriesData = chromatogramChartSupport.getLineSeriesDataBaseline(chromatogramSelection, SERIES_ID_BASELINE, DisplayType.TIC, color, false);
			ILineSeriesSettings settings = lineSeriesData.getSettings();
			settings.setEnableArea(enableBaselineArea);
			settings.setVisible(peakChartSettings.isShowBaseline());
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addPeakData(List<IPeak> peaks, List<ILineSeriesData> lineSeriesDataList) {

		int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
		PlotSymbolType symbolTypeActiveNormal = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL_MARKER_TYPE));
		PlotSymbolType symbolTypeInactiveNormal = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_NORMAL_MARKER_TYPE));
		PlotSymbolType symbolTypeActiveIstd = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_ISTD_MARKER_TYPE));
		PlotSymbolType symbolTypeInactiveIstd = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_ISTD_MARKER_TYPE));
		//
		List<IPeak> peaksActiveNormal = new ArrayList<>();
		List<IPeak> peaksInactiveNormal = new ArrayList<>();
		List<IPeak> peaksActiveISTD = new ArrayList<>();
		List<IPeak> peaksInactiveISTD = new ArrayList<>();
		//
		for(IPeak peak : peaks) {
			if(peak.getInternalStandards().size() > 0) {
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

	private void addPeak(IPeak peak, List<ILineSeriesData> lineSeriesDataList, int index) {

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

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, ITargetDisplaySettings displaySettings) {

		if(chromatogramSelection != null) {
			String seriesId = SERIES_ID_IDENTIFIED_SCANS;
			List<IScan> scans = ChromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram());
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SCAN_MARKER_TYPE));
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

		if(scans.size() > 0) {
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
				Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN));
				int markerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE);
				PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE));
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
			if(selectedIdentifiedScans.size() > 0) {
				String seriesId = SERIES_ID_IDENTIFIED_SCAN_SELECTED;
				Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_IDENTIFIED_SCAN));
				PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_IDENTIFIED_SCAN_MARKER_TYPE));
				int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
				addIdentifiedScansData(chromatogramSelection, lineSeriesDataList, selectedIdentifiedScans, symbolType, symbolSize, color, seriesId);
			}
		}
	}

	private void removeIdentificationLabelMarker(Map<String, ? extends ICustomPaintListener> markerMap, String seriesId) {

		IPlotArea plotArea = getBaseChart().getPlotArea();
		ICustomPaintListener labelMarker = markerMap.get(seriesId);
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

	private void clearChromatogramSeries() {

		deleteSeries(SERIES_ID_CHROMATOGRAM_TIC);
		deleteSeries(SERIES_ID_CHROMATOGRAM_XIC);
		deleteSeries(SERIES_ID_CHROMATOGRAM_SWC);
		deleteSeries(SERIES_ID_BASELINE);
		deleteSeries(SERIES_ID_PEAKS_NORMAL_ACTIVE);
		deleteSeries(SERIES_ID_PEAKS_NORMAL_INACTIVE);
		deleteSeries(SERIES_ID_PEAKS_ISTD_ACTIVE);
		deleteSeries(SERIES_ID_PEAKS_ISTD_INACTIVE);
		deleteSeries(SERIES_ID_SELECTED_SCAN);
		deleteSeries(SERIES_ID_IDENTIFIED_SCANS);
		deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
	}

	private void clearSelectedPeakSeries() {

		/*
		 * Clear the peak series.
		 */
		for(String id : selectedPeakIds) {
			deleteSeries(id);
		}
		//
		selectedPeakIds.clear();
	}

	private void clearPeakLabelMarker() {

		/*
		 * Clear the label marker.
		 */
		Set<String> seriesIds = peakLabelMarkerMap.keySet();
		for(String seriesId : seriesIds) {
			removeIdentificationLabelMarker(peakLabelMarkerMap, seriesId);
		}
		/*
		 * Clear the maps.
		 */
		peakLabelMarkerMap.clear();
	}

	private void redrawChart() {

		getBaseChart().redraw();
	}
}
