/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.ChromatogramSelection;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.exceptions.SeriesException;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;

public class PeakTracesUI extends ScrollableChart {

	private final ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private final PeakChartSupport peakChartSupport = new PeakChartSupport();
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private List<Integer> traces = new ArrayList<>();
	private int selectedTrace = 0;
	private IPeak peak = null;

	public PeakTracesUI() {
		super();
		modifyChart();
	}

	public PeakTracesUI(Composite parent, int style) {
		super(parent, style);
		modifyChart();
	}

	public List<Integer> getTraces() {

		return traces;
	}

	public void setSelectedTrace(int selectedTrace) {

		this.selectedTrace = selectedTrace;
	}

	public void setInput(IPeak peak) {

		this.peak = peak;
		updateChart();
	}

	@SuppressWarnings("rawtypes")
	public void updateChart() {

		modifyChart(peak);
		deleteSeries();
		traces.clear();
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		//
		if(peak instanceof IChromatogramPeakMSD) {
			IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)peak;
			IChromatogramMSD chromatogram = chromatogramPeak.getChromatogram();
			IPeakModelMSD peakModel = chromatogramPeak.getPeakModel();
			//
			int startRetentionTime = peakModel.getStartRetentionTime();
			int stopRetentionTime = peakModel.getStopRetentionTime();
			IPeakMassSpectrum massSpectrum = peakModel.getPeakMassSpectrum();
			IChromatogramSelection chromatogramSelection = new ChromatogramSelection<>(chromatogram);
			chromatogramSelection.setRangeRetentionTime(startRetentionTime, stopRetentionTime, false);
			//
			IColorScheme colors = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_PEAK_TRACES));
			extractTraces(massSpectrum);
			//
			for(Integer trace : traces) {
				IMarkedIons markedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
				markedIons.add(trace);
				ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogramSelection, Integer.toString(trace), DisplayType.SIC, ChromatogramChartSupport.DERIVATIVE_NONE, colors.getColor(), markedIons, false);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				if(trace == selectedTrace) {
					lineSeriesSettings.setLineWidth(2);
				}
				lineSeriesDataList.add(lineSeriesData);
				colors.incrementColor();
			}
		} else if(peak != null) {
			lineSeriesDataList.add(peakChartSupport.getPeak(peak, false, false, Colors.RED, "Peak"));
		}
		//
		addLineSeriesData(lineSeriesDataList);
	}

	private void extractTraces(IScanMSD scanMSD) {

		List<IIon> ions = new ArrayList<>(scanMSD.getIons());
		Collections.sort(ions, (i1, i2) -> Float.compare(i2.getAbundance(), i1.getAbundance()));
		int maxDisplayTraces = preferenceStore.getInt(PreferenceConstants.P_MAX_DISPLAY_PEAK_TRACES);
		//
		exitloop:
		for(IIon ion : ions) {
			/*
			 * Add the trace.
			 */
			int trace = AbstractIon.getIon(ion.getIon());
			if(!traces.contains(trace)) {
				traces.add(trace);
			}
			//
			if(traces.size() >= maxDisplayTraces) {
				break exitloop;
			}
		}
		/*
		 * Sort the traces ascending.
		 */
		Collections.sort(traces);
	}

	private void modifyChart() {

		/*
		 * Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setRestrictZoom(true);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMinY(0.0d);
		rangeRestriction.setExtendMaxY(0.1d);
		//
		applySettings(chartSettings);
	}

	private void modifyChart(IPeak peak) {

		IChartSettings chartSettings = getChartSettings();
		//
		if(peak instanceof IPeakMSD) {
			setDataTypeMSD(chartSettings);
		} else if(peak instanceof IPeakCSD) {
			setDataTypeCSD(chartSettings);
		} else if(peak instanceof IPeakWSD) {
			setDataTypeWSD(chartSettings);
		}
		//
		applySettings(chartSettings);
	}

	private void setDataTypeMSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "Retention Time [ms]", false, "Intensity");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisX(chartSettings, "Minutes");
		addSecondaryAxisY(chartSettings, "Intensity [%]");
	}

	private void setDataTypeCSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "Retention Time [ms]", false, "Current");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisX(chartSettings, "Minutes");
		addSecondaryAxisY(chartSettings, "Current [%]");
	}

	private void setDataTypeWSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "Retention Time [ms]", false, "Intensity");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisX(chartSettings, "Minutes");
		addSecondaryAxisY(chartSettings, "Intensity [%]");
	}

	private void clearSecondaryAxes(IChartSettings chartSettings) {

		chartSettings.getSecondaryAxisSettingsListX().clear();
		chartSettings.getSecondaryAxisSettingsListY().clear();
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings, String xAxisTitle, boolean xAxisVisible, String yAxisTitle) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(xAxisTitle);
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setVisible(xAxisVisible);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(yAxisTitle);
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
	}

	private void addSecondaryAxisX(IChartSettings chartSettings, String xAxisTitle) {

		ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(xAxisTitle, new MillisecondsToMinuteConverter());
		secondaryAxisSettingsX.setPosition(Position.Primary);
		secondaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
	}

	private void addSecondaryAxisY(IChartSettings chartSettings, String yAxisTitle) {

		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(yAxisTitle, new PercentageConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	@SuppressWarnings("rawtypes")
	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
		if(lineSeriesDataList != null && lineSeriesDataList.size() > 0) {
			BaseChart baseChart = getBaseChart();
			baseChart.suspendUpdate(true);
			for(ILineSeriesData lineSeriesData : lineSeriesDataList) {
				/*
				 * Get the series data and apply the settings.
				 */
				try {
					ISeriesData seriesData = lineSeriesData.getSeriesData();
					ISeriesData optimizedSeriesData = calculateSeries(seriesData);
					ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
					lineSeriesSettings.getSeriesSettingsHighlight(); // Initialize
					ILineSeries lineSeries = (ILineSeries)createSeries(optimizedSeriesData, lineSeriesSettings);
					baseChart.applyLineSeriesSettings(lineSeries, lineSeriesSettings);
				} catch(SeriesException e) {
					//
				}
			}
			baseChart.suspendUpdate(false);
			adjustRange(true);
			baseChart.redraw();
		}
	}
}
