/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculation;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculationSupport;
import org.eclipse.chemclipse.model.support.RetentionIndexMap;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.listener.PeakTracesOffsetListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.listener.PenaltyMarkerListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.PenaltyCalculationModel;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class PenaltyCalculationChart extends ChromatogramChart {

	private static final String SERIES_ID_PEAK = "Active Peak";
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final PeakTracesOffsetListener peakTracesOffsetListener = new PeakTracesOffsetListener(this.getBaseChart());
	private PenaltyMarkerListener unknownListener = new PenaltyMarkerListener(this.getBaseChart(), 0);
	private PenaltyMarkerListener referenceListener = new PenaltyMarkerListener(this.getBaseChart(), 1);
	private final PeakChartSupport peakChartSupport = new PeakChartSupport();
	//
	private IPeak peak = null;
	private PenaltyCalculationModel penaltyCalculationModel = null;
	private RetentionIndexMap retentionIndexMap = new RetentionIndexMap();

	public PenaltyCalculationChart() {

		super();
		initialize();
	}

	public PenaltyCalculationChart(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	public void setInput(PenaltyCalculationModel penaltyCalculationModel) {

		this.penaltyCalculationModel = penaltyCalculationModel;
		updateChart();
	}

	public void setInput(IPeak peak) {

		this.peak = peak;
		retentionIndexMap.update(getChromatogram());
		updateChart();
	}

	private void initialize() {

		BaseChart baseChart = getBaseChart();
		IPlotArea plotArea = baseChart.getPlotArea();
		plotArea.addCustomPaintListener(peakTracesOffsetListener);
		plotArea.addCustomPaintListener(referenceListener);
		plotArea.addCustomPaintListener(unknownListener);
	}

	private void updateChart() {

		updatePeak();
		updatePenaltyModel();
	}

	private void updatePeak() {

		deleteSeries(SERIES_ID_PEAK);
		peakTracesOffsetListener.setOffsetRetentionTime(0);
		//
		if(peak != null) {
			//
			IPeakModel peakModel = peak.getPeakModel();
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			ILineSeriesData lineSeriesData = peakChartSupport.getPeak(peak, false, false, Colors.RED, SERIES_ID_PEAK);
			/*
			 * Modify the peak series.
			 */
			int offsetRetentionTime = preferenceStore.getInt(PreferenceConstants.P_PEAK_TRACES_OFFSET_RETENTION_TIME);
			peakTracesOffsetListener.setOffsetRetentionTime(offsetRetentionTime);
			int startRetentionTime = peakModel.getStartRetentionTime() - offsetRetentionTime;
			int stopRetentionTime = peakModel.getStopRetentionTime() + offsetRetentionTime;
			//
			ISeriesData seriesData = lineSeriesData.getSeriesData();
			double[] xSeries = adjust(seriesData.getXSeries(), startRetentionTime, stopRetentionTime);
			double[] ySeriesOld = seriesData.getYSeries();
			double[] ySeries = adjust(ySeriesOld, ySeriesOld[0], ySeriesOld[ySeriesOld.length - 1]);
			ILineSeriesData lineSeriesDataAdjusted = new LineSeriesData(new SeriesData(xSeries, ySeries, seriesData.getId()));
			lineSeriesData.getSettings().transfer(lineSeriesDataAdjusted.getSettings());
			//
			lineSeriesDataList.add(lineSeriesDataAdjusted);
			addSeriesData(lineSeriesDataList);
		}
	}

	private void updatePenaltyModel() {

		referenceListener.reset();
		unknownListener.reset();
		//
		if(penaltyCalculationModel != null && peak != null) {
			PenaltyCalculation penaltyCalculation = penaltyCalculationModel.getPenaltyCalculation();
			switch(penaltyCalculation) {
				case RETENTION_TIME_MS:
					displayRetentionTimePenalty(false);
					break;
				case RETENTION_TIME_MIN:
					displayRetentionTimePenalty(true);
					break;
				case RETENTION_INDEX:
					displayRetentionIndexPenalty();
					break;
				default:
					break;
			}
		}
	}

	private void displayRetentionTimePenalty(boolean useMinutes) {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
		/*
		 * Reference
		 */
		double retentionTimeMinutesReference;
		int retentionTimeReference;
		String labelReference = "Reference: ";
		if(useMinutes) {
			retentionTimeMinutesReference = penaltyCalculationModel.getReferenceValue();
			retentionTimeReference = (int)(retentionTimeMinutesReference * IChromatogram.MINUTE_CORRELATION_FACTOR);
			labelReference += decimalFormat.format(retentionTimeMinutesReference) + " [min]";
		} else {
			retentionTimeReference = (int)penaltyCalculationModel.getReferenceValue();
			retentionTimeMinutesReference = retentionTimeReference / IChromatogram.MINUTE_CORRELATION_FACTOR;
			labelReference += Integer.toString(retentionTimeReference) + " [ms]";
		}
		/*
		 * Unknown
		 */
		int retentionTimeUnknown = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
		double retentionTimeMinutesUnknown = retentionTimeUnknown / IChromatogram.MINUTE_CORRELATION_FACTOR;
		String labelUnknown = "Unknown: ";
		if(useMinutes) {
			labelUnknown += decimalFormat.format(retentionTimeMinutesUnknown) + " [min]";
		} else {
			labelUnknown += Integer.toString(retentionTimeUnknown) + " [ms]";
		}
		double penaltyWindow = penaltyCalculationModel.getPenaltyWindow();
		double penaltyLevelFactor = penaltyCalculationModel.getPenaltyLevelFactor();
		double maxPenalty = penaltyCalculationModel.getMaxPenalty();
		double penalty;
		if(useMinutes) {
			penalty = PenaltyCalculationSupport.calculatePenalty(retentionTimeMinutesUnknown, retentionTimeMinutesReference, penaltyWindow, penaltyLevelFactor, maxPenalty);
		} else {
			penalty = PenaltyCalculationSupport.calculatePenalty(retentionTimeUnknown, retentionTimeReference, penaltyWindow, penaltyLevelFactor, maxPenalty);
		}
		String labelPenalty = "-" + decimalFormat.format(penalty) + "%";
		/*
		 * Marker
		 */
		referenceListener.setData(retentionTimeReference, labelReference, "100%");
		unknownListener.setData(retentionTimeUnknown, labelUnknown, labelPenalty);
	}

	private void displayRetentionIndexPenalty() {

		IChromatogram<?> chromatogram = getChromatogram();
		if(chromatogram != null) {
			IPeakModel peakModel = peak.getPeakModel();
			IScan peakMaximum = peakModel.getPeakMaximum();
			float retentionIndexUnknown = peakMaximum.getRetentionIndex();
			if(retentionIndexUnknown > 0) {
				/*
				 * Unknown
				 */
				int retentionTimeUnknown = peakMaximum.getRetentionTime();
				String labelUnknown = "Unknown RI: " + (int)retentionIndexUnknown;
				unknownListener.setData(retentionTimeUnknown, labelUnknown, "-- %");
				//
				double retentionIndexReference = penaltyCalculationModel.getReferenceValue();
				int retentionTimeReference = retentionIndexMap.getRetentionTime((int)retentionIndexReference);
				if(retentionTimeReference > -1) {
					/*
					 * Reference
					 */
					String labelReference = "Reference RI: " + (int)retentionIndexReference;
					double penaltyWindow = penaltyCalculationModel.getPenaltyWindow();
					double penaltyLevelFactor = penaltyCalculationModel.getPenaltyLevelFactor();
					double maxPenalty = penaltyCalculationModel.getMaxPenalty();
					double penalty = PenaltyCalculationSupport.calculatePenalty(retentionIndexUnknown, retentionIndexReference, penaltyWindow, penaltyLevelFactor, maxPenalty);
					DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
					String labelPenalty = "-" + decimalFormat.format(penalty) + "%";
					/*
					 * Marker
					 * Adjust retention time if the RI is the same. Deviation could occur due to rounding operations.
					 */
					retentionTimeReference = ((int)retentionIndexUnknown == (int)retentionIndexReference) ? retentionTimeUnknown : retentionTimeReference;
					referenceListener.setData(retentionTimeReference, labelReference, "100%");
					unknownListener.setData(retentionTimeUnknown, labelUnknown, labelPenalty);
				}
			}
		}
	}

	private double[] adjust(double[] series, double start, double stop) {

		int index = 0;
		double[] seriesAdjusted = new double[series.length + 2];
		//
		seriesAdjusted[index++] = start;
		for(double value : series) {
			seriesAdjusted[index++] = value;
		}
		seriesAdjusted[index++] = stop;
		//
		return seriesAdjusted;
	}

	private IChromatogram<?> getChromatogram() {

		IChromatogram<?> chromatogram = null;
		if(peak instanceof IChromatogramPeak chromatogramPeak) {
			chromatogram = chromatogramPeak.getChromatogram();
		}
		//
		return chromatogram;
	}
}
