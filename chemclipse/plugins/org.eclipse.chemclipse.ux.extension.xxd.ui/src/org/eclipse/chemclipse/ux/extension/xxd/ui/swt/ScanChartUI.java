/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.BarSeriesValue;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.BarSeriesYComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.SignalType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesData;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesSettings;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.customcharts.MassSpectrumChart.LabelOption;
import org.eclipse.swtchart.extensions.exceptions.SeriesException;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;

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
	/*
	 * Initialized on use.
	 */
	private LabelOption labelOption;
	private DataType dataType;
	private SignalType signalType;
	//
	private DecimalFormat decimalFormatNormalIntensity = ValueFormat.getDecimalFormatEnglish("0");
	private DecimalFormat decimalFormatLowIntensity = ValueFormat.getDecimalFormatEnglish("0.0000");
	private ScanChartSupport scanChartSupport = new ScanChartSupport();
	private Font font = DisplayUtils.getDisplay().getSystemFont();

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
			modifyChart(scan, null);
			//
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			Color colorScan1 = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_SCAN_1));
			//
			if(usedSignalType.equals(SignalType.PROFILE)) {
				List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
				ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesData(scan, "", false);
				lineSeriesData.getSettings().setLineColor(colorScan1);
				lineSeriesDataList.add(lineSeriesData);
				addLineSeriesData(lineSeriesDataList);
			} else {
				List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
				IBarSeriesData barSeriesData = scanChartSupport.getBarSeriesData(scan, "", false);
				barSeriesData.getSettings().setBarColor(colorScan1);
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
			modifyChart(scan1, scan2);
			//
			String labelScan1 = "scan1";
			String labelScan2 = "scan2";
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			Color colorScan1 = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_SCAN_1));
			Color colorScan2 = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_SCAN_2));
			//
			if(usedSignalType.equals(SignalType.PROFILE)) {
				List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
				ILineSeriesData lineSeriesDataScan1 = scanChartSupport.getLineSeriesData(scan1, labelScan1, false);
				ILineSeriesData lineSeriesDataScan2 = scanChartSupport.getLineSeriesData(scan2, labelScan2, mirrored);
				lineSeriesDataScan1.getSettings().setLineColor(colorScan1);
				lineSeriesDataScan2.getSettings().setLineColor(colorScan2);
				lineSeriesDataList.add(lineSeriesDataScan1);
				lineSeriesDataList.add(lineSeriesDataScan2);
				addLineSeriesData(lineSeriesDataList);
			} else {
				List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
				IBarSeriesData barSeriesDataScan1 = scanChartSupport.getBarSeriesData(scan1, labelScan1, false);
				IBarSeriesData barSeriesDataScan2 = scanChartSupport.getBarSeriesData(scan2, labelScan2, mirrored);
				barSeriesDataScan1.getSettings().setBarColor(colorScan1);
				barSeriesDataScan2.getSettings().setBarColor(colorScan2);
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

	private void modifyChart(IScan scan1, IScan scan2) {

		/*
		 * If only one signal is contained in the scan, then
		 * zeroY needs to be enabled to display the complete signal.
		 * Otherwise, it's auto calculated.
		 */
		boolean forceZeroY = isForceZeroMinY(scan1);
		if(!forceZeroY && scan2 != null) {
			forceZeroY = isForceZeroMinY(scan2);
		}
		//
		IChartSettings chartSettings = getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setForceZeroMinY(forceZeroY);
		applySettings(chartSettings);
	}

	private boolean isForceZeroMinY(IScan scan) {

		boolean forceZeroY = false;
		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			forceZeroY = (scanMSD.getNumberOfIons() == 1) ? true : false;
		} else if(scan instanceof IScanCSD) {
			forceZeroY = true; // Only 1 signal contained.
		} else if(scan instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)scan;
			forceZeroY = (scanWSD.getNumberOfScanSignals() == 1) ? true : false;
		}
		return forceZeroY;
	}

	private void modifyChart(boolean mirrored) {

		IChartSettings chartSettings = getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroY((mirrored) ? false : true);
		rangeRestriction.setForceZeroMinY((mirrored) ? true : false);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMinY((mirrored) ? 0.25d : 0.0d);
		rangeRestriction.setExtendMaxY(0.25d);
		applySettings(chartSettings);
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
		font = Fonts.getFont(name, height, style);
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
				rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.RELATIVE);
				rangeRestriction.setExtendMinX(0.1d);
				rangeRestriction.setExtendMaxX(0.1d);
				setDataTypeCSD(chartSettings);
				break;
			case WSD:
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
					IBarSeriesSettings barSeriesSettings = barSeriesData.getSettings();
					barSeriesSettings.getSeriesSettingsHighlight(); // Initialize
					IBarSeries barSeries = (IBarSeries)createSeries(optimizedSeriesData, barSeriesSettings);
					barSeriesSettings.setBarOverlay(true);
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
		e.gc.setFont(font);
		//
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
		//
		e.gc.setFont(currentFont);
	}

	private String getLabel(double value) {

		String label;
		switch(labelOption) {
			case NOMIMAL:
				if(value > -1.0d && value < 0.0d || (value > 0.0d && value < 1.0d)) {
					label = decimalFormatLowIntensity.format(value);
				} else {
					label = decimalFormatNormalIntensity.format(value);
				}
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
