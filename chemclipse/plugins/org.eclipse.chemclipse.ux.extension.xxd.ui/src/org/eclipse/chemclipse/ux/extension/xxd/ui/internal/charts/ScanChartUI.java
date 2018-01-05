/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonValueComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.SignalType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.wsd.model.comparator.WavelengthValueComparator;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.eavp.service.swtchart.axisconverter.RelativeIntensityConverter;
import org.eclipse.eavp.service.swtchart.barcharts.BarSeriesData;
import org.eclipse.eavp.service.swtchart.barcharts.IBarSeriesData;
import org.eclipse.eavp.service.swtchart.barcharts.IBarSeriesSettings;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IAxisSettings;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IPrimaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.RangeRestriction;
import org.eclipse.eavp.service.swtchart.core.ScrollableChart;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.customcharts.MassSpectrumChart.LabelOption;
import org.eclipse.eavp.service.swtchart.exceptions.SeriesException;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.IAxis.Position;
import org.swtchart.IBarSeries;
import org.swtchart.IBarSeries.BarWidthStyle;
import org.swtchart.ICustomPaintListener;
import org.swtchart.ILineSeries;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries;

public class ScanChartUI extends ScrollableChart {

	private static final int LENGTH_HINT_DATA_POINTS = 5000;
	private static final int COMPRESS_TO_LENGTH = Integer.MAX_VALUE;
	//
	private int labelHighestIntensities = 5;
	private boolean addModuloLabels = false;
	//
	private DecimalFormat defaultDecimalFormat = ValueFormat.getDecimalFormatEnglish();
	private DecimalFormat decimalFormatQ3 = ValueFormat.getDecimalFormatEnglish("0.0");
	//
	private BarSeriesYComparator barSeriesIntensityComparator = new BarSeriesYComparator();
	private Map<Double, String> customLabels = new HashMap<Double, String>();
	private LabelPaintListener labelPaintListenerX = new LabelPaintListener(true);
	private LabelPaintListener labelPaintListenerY = new LabelPaintListener(false);
	private Map<String, Font> fonts = new HashMap<String, Font>();
	/*
	 * Initialized on use.
	 */
	private String fontId;
	private LabelOption labelOption;
	private DataType dataType;
	private SignalType signalType;
	//
	private IonValueComparator ionValueComparator = new IonValueComparator(SortOrder.ASC);
	private WavelengthValueComparator wavelengthValueComparator = new WavelengthValueComparator(SortOrder.ASC);

	private class LabelPaintListener implements ICustomPaintListener {

		private boolean useX;

		/**
		 * If true, the x value will be used. Otherwise, the y value.
		 * 
		 * @param useX
		 */
		public LabelPaintListener(boolean useX) {
			this.useX = useX;
		}

		@Override
		public void paintControl(PaintEvent e) {

			List<BarSeriesValue> barSeriesValues = getBarSeriesValuesList();
			Collections.sort(barSeriesValues, barSeriesIntensityComparator);
			/*
			 * Labels
			 */
			printHighestLabelsNormal(barSeriesValues, e);
			printHighestLabelsMirrored(barSeriesValues, e);
		}

		private void printHighestLabelsNormal(List<BarSeriesValue> barSeriesValues, PaintEvent e) {

			int size = barSeriesValues.size();
			int modulo = size / labelHighestIntensities;
			int limit = (labelHighestIntensities < size) ? labelHighestIntensities : size;
			//
			for(int i = 0; i < size; i++) {
				if(i < limit) {
					BarSeriesValue barSeriesValue = barSeriesValues.get(i);
					printLabel(barSeriesValue, useX, e);
				} else {
					if(addModuloLabels) {
						if(i % modulo == 0) {
							BarSeriesValue barSeriesValue = barSeriesValues.get(i);
							printLabel(barSeriesValue, useX, e);
						}
					}
				}
			}
		}

		private void printHighestLabelsMirrored(List<BarSeriesValue> barSeriesValues, PaintEvent e) {

			int size = barSeriesValues.size();
			int limit = size - labelHighestIntensities;
			limit = (limit < 0) ? 0 : limit;
			int modulo = size / labelHighestIntensities;
			//
			for(int i = size - 1; i >= 0; i--) {
				if(i >= limit) {
					BarSeriesValue barSeriesValue = barSeriesValues.get(i);
					if(barSeriesValue.getY() < 0) {
						printLabel(barSeriesValue, useX, e);
					}
				} else {
					if(addModuloLabels) {
						if(i % modulo == 0) {
							BarSeriesValue barSeriesValue = barSeriesValues.get(i);
							if(barSeriesValue.getY() < 0) {
								printLabel(barSeriesValue, useX, e);
							}
						}
					}
				}
			}
		}

		@Override
		public boolean drawBehindSeries() {

			return false;
		}
	}

	public ScanChartUI() {
		super();
		setDefaultDataAndSignalType();
	}

	public ScanChartUI(Composite parent, int style) {
		super(parent, style);
		setDefaultDataAndSignalType();
	}

	public void setInput(IScan scan) {

		prepareChart();
		if(scan != null) {
			/*
			 * Set the chart data.
			 */
			extractCustomLabels(scan);
			DataType usedDataType = determineDataType(scan);
			SignalType usedSignalType = determineSignalType(scan);
			//
			modifyChart(usedDataType);
			determineLabelOption(usedDataType);
			//
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			Color colorScan1 = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_SCAN_1));
			//
			if(usedSignalType.equals(SignalType.PROFILE)) {
				List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
				ILineSeriesData lineSeriesData = getLineSeriesData(scan, "", false);
				lineSeriesData.getLineSeriesSettings().setLineColor(colorScan1);
				lineSeriesDataList.add(lineSeriesData);
				addLineSeriesData(lineSeriesDataList);
			} else {
				List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
				IBarSeriesData barSeriesData = getBarSeriesData(scan, "", false);
				barSeriesData.getBarSeriesSettings().setBarColor(colorScan1);
				barSeriesDataList.add(barSeriesData);
				addBarSeriesData(barSeriesDataList);
			}
		}
	}

	public void setInput(IScan scan1, IScan scan2, boolean mirrored) {

		prepareChart();
		if(scan1 != null) {
			/*
			 * Set the chart data.
			 */
			extractCustomLabels(scan1);
			DataType usedDataType = determineDataType(scan1);
			SignalType usedSignalType = determineSignalType(scan1);
			//
			modifyChart(usedDataType);
			determineLabelOption(usedDataType);
			modifyChart(mirrored);
			//
			String labelScan1 = "scan1";
			String labelScan2 = "scan2";
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			Color colorScan1 = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_SCAN_1));
			Color colorScan2 = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_SCAN_2));
			//
			if(usedSignalType.equals(SignalType.PROFILE)) {
				List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
				ILineSeriesData lineSeriesDataScan1 = getLineSeriesData(scan1, labelScan1, false);
				ILineSeriesData lineSeriesDataScan2 = getLineSeriesData(scan2, labelScan2, mirrored);
				lineSeriesDataScan1.getLineSeriesSettings().setLineColor(colorScan1);
				lineSeriesDataScan2.getLineSeriesSettings().setLineColor(colorScan2);
				lineSeriesDataList.add(lineSeriesDataScan1);
				lineSeriesDataList.add(lineSeriesDataScan2);
				addLineSeriesData(lineSeriesDataList);
			} else {
				List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
				IBarSeriesData barSeriesDataScan1 = getBarSeriesData(scan1, labelScan1, false);
				IBarSeriesData barSeriesDataScan2 = getBarSeriesData(scan2, labelScan2, mirrored);
				barSeriesDataScan1.getBarSeriesSettings().setBarColor(colorScan1);
				barSeriesDataScan2.getBarSeriesSettings().setBarColor(colorScan2);
				barSeriesDataList.add(barSeriesDataScan1);
				barSeriesDataList.add(barSeriesDataScan2);
				addBarSeriesData(barSeriesDataList);
			}
		}
	}

	private void prepareChart() {

		customLabels.clear();
		deleteSeries();
	}

	private void modifyChart(boolean mirrored) {

		IChartSettings chartSettings = getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroY((mirrored) ? false : true);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMinY((mirrored) ? 0.25d : 0.0d);
		rangeRestriction.setExtendMaxY(0.25d);
		applySettings(chartSettings);
	}

	@Override
	public void dispose() {

		for(Font font : fonts.values()) {
			font.dispose();
		}
		super.dispose();
	}

	public void setDataType(DataType dataType) {

		this.dataType = dataType;
	}

	public void setSignalType(SignalType signalType) {

		this.signalType = signalType;
	}

	private void setDefaultDataAndSignalType() {

		dataType = DataType.AUTO_DETECT;
		signalType = SignalType.AUTO_DETECT;
		modifyChart(DataType.MSD_NOMINAL);
	}

	private DataType determineDataType(IScan scan) {

		DataType usedDataType;
		if(dataType.equals(DataType.AUTO_DETECT)) {
			if(scan instanceof IScanMSD) {
				/*
				 * MSD
				 */
				IScanMSD scanMSD = (IScanMSD)scan;
				if(scanMSD.isTandemMS()) {
					usedDataType = DataType.MSD_TANDEM;
				} else {
					if(scanMSD.isHighResolutionMS()) {
						usedDataType = DataType.MSD_HIGHRES;
					} else {
						usedDataType = DataType.MSD_NOMINAL;
					}
				}
			} else if(scan instanceof IScanCSD) {
				usedDataType = DataType.CSD;
			} else if(scan instanceof IScanWSD) {
				usedDataType = DataType.WSD;
			} else {
				usedDataType = DataType.MSD_NOMINAL;
			}
		} else {
			usedDataType = dataType;
		}
		return usedDataType;
	}

	private SignalType determineSignalType(IScan scan) {

		SignalType usedSignalType;
		if(signalType.equals(SignalType.AUTO_DETECT)) {
			/*
			 * Default is centroid.
			 */
			usedSignalType = SignalType.CENTROID;
			if(scan instanceof IRegularMassSpectrum) {
				IRegularMassSpectrum massSpectrum = (IRegularMassSpectrum)scan;
				if(massSpectrum.getMassSpectrumType() == 1) {
					usedSignalType = SignalType.PROFILE;
				}
			}
		} else {
			usedSignalType = signalType;
		}
		//
		return usedSignalType;
	}

	private void determineLabelOption(DataType dataType) {

		switch(dataType) {
			case MSD_NOMINAL:
				labelOption = LabelOption.NOMIMAL;
				break;
			case MSD_TANDEM:
				labelOption = LabelOption.CUSTOM;
				break;
			case MSD_HIGHRES:
				labelOption = LabelOption.EXACT;
				break;
			case CSD:
				labelOption = LabelOption.NOMIMAL;
				break;
			case WSD:
				labelOption = LabelOption.NOMIMAL;
				break;
			default:
				labelOption = LabelOption.NOMIMAL;
				break;
		}
	}

	private void extractCustomLabels(IScan scan) {

		if(scan instanceof IScanMSD) {
			/*
			 * MSD
			 */
			IScanMSD scanMSD = (IScanMSD)scan;
			if(scanMSD.isTandemMS()) {
				for(IIon ion : scanMSD.getIons()) {
					IIonTransition ionTransition = ion.getIonTransition();
					if(ionTransition != null) {
						customLabels.put(ion.getIon(), ionTransition.getQ1Ion() + " > " + decimalFormatQ3.format(ion.getIon()) + " @" + (int)ionTransition.getCollisionEnergy());
					} else {
						customLabels.put(ion.getIon(), Double.toString(ion.getIon()));
					}
				}
			}
		}
	}

	private void modifyChart(DataType dataType) {

		/*
		 * Preferences
		 */
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String name = preferenceStore.getString(PreferenceConstants.P_SCAN_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_SCAN_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_SCAN_LABEL_FONT_STYLE);
		fontId = name + height + style;
		if(!fonts.containsKey(fontId)) {
			Font font = new Font(Display.getDefault(), name, height, style);
			fonts.put(fontId, font);
		}
		//
		labelHighestIntensities = preferenceStore.getInt(PreferenceConstants.P_SCAN_LABEL_HIGHEST_INTENSITIES);
		addModuloLabels = preferenceStore.getBoolean(PreferenceConstants.P_SCAN_LABEL_MODULO_INTENSITIES);
		/*
		 * Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		chartSettings.getRangeRestriction().setForceZeroMinY(false);
		rangeRestriction.setRestrictZoom(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.ABSOLUTE);
		rangeRestriction.setExtendMinX(2.0d);
		rangeRestriction.setExtendMaxX(2.0d);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMinY(0.0d);
		rangeRestriction.setExtendMaxY(0.25d);
		//
		LabelPaintListener labelPaintListener = labelPaintListenerX;
		//
		switch(dataType) {
			case MSD_NOMINAL:
				setDataTypeMSD(chartSettings);
				break;
			case MSD_TANDEM:
				setDataTypeMSD(chartSettings);
				break;
			case MSD_HIGHRES:
				setDataTypeMSD(chartSettings);
				break;
			case CSD:
				labelPaintListener = labelPaintListenerY;
				chartSettings.getRangeRestriction().setForceZeroMinY(true);
				rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.RELATIVE);
				rangeRestriction.setExtendMinX(0.1d);
				rangeRestriction.setExtendMaxX(0.1d);
				setDataTypeCSD(chartSettings);
				break;
			case WSD:
				chartSettings.getRangeRestriction().setForceZeroMinY(true);
				setDataTypeWSD(chartSettings);
				break;
			default:
				setDataTypeMSD(chartSettings);
				break;
		}
		//
		applySettings(chartSettings);
		addSeriesLabelMarker(labelPaintListener);
	}

	private IBarSeriesData getBarSeriesData(IScan scan, String postfix, boolean mirrored) {

		ISeriesData seriesData = getSeriesData(scan, postfix, mirrored);
		IBarSeriesData barSeriesData = new BarSeriesData(seriesData);
		return barSeriesData;
	}

	private ILineSeriesData getLineSeriesData(IScan scan, String postfix, boolean mirrored) {

		ISeriesData seriesData = getSeriesData(scan, postfix, mirrored);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		return lineSeriesData;
	}

	private ISeriesData getSeriesData(IScan scan, String postfix, boolean mirrored) {

		double[] xSeries;
		double[] ySeries;
		String scanNumber = (scan.getScanNumber() > 0 ? Integer.toString(scan.getScanNumber()) : "--");
		String id = "Scan " + scanNumber;
		if(!"".equals(postfix)) {
			id += " " + postfix;
		}
		/*
		 * Sort the scan data, otherwise the line chart could be odd.
		 */
		if(scan instanceof IScanMSD) {
			/*
			 * MSD
			 */
			IScanMSD scanMSD = (IScanMSD)scan;
			List<IIon> ions = new ArrayList<IIon>(scanMSD.getIons());
			Collections.sort(ions, ionValueComparator);
			int size = ions.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IIon ion : ions) {
				xSeries[index] = ion.getIon();
				ySeries[index] = (mirrored) ? ion.getAbundance() * -1 : ion.getAbundance();
				index++;
			}
		} else if(scan instanceof IScanCSD) {
			/*
			 * CSD
			 */
			IScanCSD scanCSD = (IScanCSD)scan;
			xSeries = new double[]{scanCSD.getRetentionTime()};
			ySeries = new double[]{(mirrored) ? scanCSD.getTotalSignal() * -1 : scanCSD.getTotalSignal()};
		} else if(scan instanceof IScanWSD) {
			/*
			 * WSD
			 */
			IScanWSD scanWSD = (IScanWSD)scan;
			List<IScanSignalWSD> scanSignalsWSD = new ArrayList<IScanSignalWSD>(scanWSD.getScanSignals());
			Collections.sort(scanSignalsWSD, wavelengthValueComparator);
			int size = scanSignalsWSD.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IScanSignalWSD scanSignalWSD : scanSignalsWSD) {
				xSeries[index] = scanSignalWSD.getWavelength();
				ySeries[index] = (mirrored) ? scanSignalWSD.getAbundance() * -1 : scanSignalWSD.getAbundance();
				index++;
			}
		} else {
			xSeries = new double[0];
			ySeries = new double[0];
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private void addBarSeriesData(List<IBarSeriesData> barSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
		if(barSeriesDataList != null && barSeriesDataList.size() > 0) {
			BaseChart baseChart = getBaseChart();
			baseChart.suspendUpdate(true);
			for(IBarSeriesData barSeriesData : barSeriesDataList) {
				/*
				 * Get the series data and apply the settings.
				 */
				try {
					ISeriesData seriesData = barSeriesData.getSeriesData();
					ISeriesData optimizedSeriesData = calculateSeries(seriesData, COMPRESS_TO_LENGTH);
					IBarSeriesSettings barSeriesSettings = barSeriesData.getBarSeriesSettings();
					barSeriesSettings.getSeriesSettingsHighlight(); // Initialize
					IBarSeries barSeries = (IBarSeries)createSeries(optimizedSeriesData, barSeriesSettings);
					baseChart.applyBarSeriesSettings(barSeries, barSeriesSettings);
					/*
					 * Automatically use stretched if it is a large data set.
					 */
					if(isLargeDataSet(optimizedSeriesData.getXSeries(), optimizedSeriesData.getYSeries(), LENGTH_HINT_DATA_POINTS)) {
						barSeries.setBarWidthStyle(BarWidthStyle.STRETCHED);
					} else {
						barSeries.setBarWidthStyle(barSeriesSettings.getBarWidthStyle());
					}
				} catch(SeriesException e) {
					//
				}
			}
			baseChart.suspendUpdate(false);
			adjustRange(true);
			baseChart.redraw();
		}
	}

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
					ISeriesData optimizedSeriesData = calculateSeries(seriesData, COMPRESS_TO_LENGTH);
					ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
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

	private void setDataTypeMSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "m/z", true, "Intensity");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisY(chartSettings, "Intensity [%]");
	}

	private void setDataTypeCSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "Retention Time [ms]", false, "Current");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisX(chartSettings, "Minutes");
		addSecondaryAxisY(chartSettings, "Current [%]");
	}

	private void setDataTypeWSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "wavelength [nm]", true, "Intensity");
		clearSecondaryAxes(chartSettings);
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
		primaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		primaryAxisSettingsX.setVisible(xAxisVisible);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(yAxisTitle);
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	}

	private void addSecondaryAxisX(IChartSettings chartSettings, String xAxisTitle) {

		ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(xAxisTitle, new MillisecondsToMinuteConverter());
		secondaryAxisSettingsX.setPosition(Position.Primary);
		secondaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsX.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
	}

	private void addSecondaryAxisY(IChartSettings chartSettings, String yAxisTitle) {

		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings(yAxisTitle, new RelativeIntensityConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsY.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	private void addSeriesLabelMarker(LabelPaintListener labelPaintListener) {

		/*
		 * Plot the series name above the entry.
		 * Remove and re-add it. There is no way to check if the label
		 * paint listener is already registered.
		 */
		IPlotArea plotArea = (IPlotArea)getBaseChart().getPlotArea();
		plotArea.removeCustomPaintListener(labelPaintListenerX);
		plotArea.removeCustomPaintListener(labelPaintListenerY);
		plotArea.addCustomPaintListener(labelPaintListener);
	}

	private void printLabel(BarSeriesValue barSeriesValue, boolean useX, PaintEvent e) {

		Font currentFont = e.gc.getFont();
		e.gc.setFont(fonts.get(fontId));
		Point point = barSeriesValue.getPoint();
		String label = (useX) ? getLabel(barSeriesValue.getX()) : getLabel(barSeriesValue.getY());
		boolean negative = (barSeriesValue.getY() < 0) ? true : false;
		Point labelSize = e.gc.textExtent(label);
		int x = (int)(point.x + 0.5d - labelSize.x / 2.0d);
		int y = point.y;
		if(!negative) {
			y = point.y - labelSize.y;
		}
		e.gc.drawText(label, x, y, true);
		e.gc.setFont(currentFont);
	}

	private String getLabel(double value) {

		String label;
		switch(labelOption) {
			case NOMIMAL:
				label = Integer.toString((int)value);
				break;
			case EXACT:
				DecimalFormat decimalFormat = getDecimalFormatValue();
				label = decimalFormat.format(value);
				break;
			case CUSTOM:
				label = customLabels.get(value);
				if(label == null) {
					label = "";
				}
				break;
			default:
				label = "";
		}
		return label;
	}

	private List<BarSeriesValue> getBarSeriesValuesList() {

		List<BarSeriesValue> barSeriesIons = new ArrayList<BarSeriesValue>();
		//
		int widthPlotArea = getBaseChart().getPlotArea().getBounds().width;
		ISeries[] series = getBaseChart().getSeriesSet().getSeries();
		for(ISeries barSeries : series) {
			if(barSeries != null) {
				//
				double[] xSeries = barSeries.getXSeries();
				double[] ySeries = barSeries.getYSeries();
				int size = barSeries.getXSeries().length;
				//
				for(int i = 0; i < size; i++) {
					Point point = barSeries.getPixelCoordinates(i);
					if(point.x >= 0 && point.x <= widthPlotArea) {
						barSeriesIons.add(new BarSeriesValue(xSeries[i], ySeries[i], point));
					}
				}
			}
		}
		return barSeriesIons;
	}

	private DecimalFormat getDecimalFormatValue() {

		IAxisSettings axisSettings = getBaseChart().getXAxisSettings(BaseChart.ID_PRIMARY_X_AXIS);
		if(axisSettings != null) {
			return axisSettings.getDecimalFormat();
		} else {
			return defaultDecimalFormat;
		}
	}
}
