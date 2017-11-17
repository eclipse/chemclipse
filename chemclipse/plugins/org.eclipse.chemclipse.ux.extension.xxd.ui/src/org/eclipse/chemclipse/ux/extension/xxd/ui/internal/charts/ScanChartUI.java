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
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.SignalType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
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
			int barSeriesSize = barSeriesValues.size();
			int limit;
			/*
			 * Positive
			 */
			limit = labelHighestIntensities;
			for(int i = 0; i < limit; i++) {
				if(i < barSeriesSize) {
					BarSeriesValue barSeriesValue = barSeriesValues.get(i);
					printLabel(barSeriesValue, useX, e);
				}
			}
			/*
			 * Negative
			 */
			limit = barSeriesValues.size() - labelHighestIntensities;
			limit = (limit < 0) ? 0 : limit;
			for(int i = barSeriesValues.size() - 1; i >= limit; i--) {
				BarSeriesValue barSeriesValue = barSeriesValues.get(i);
				if(barSeriesValue.getY() < 0) {
					printLabel(barSeriesValue, useX, e);
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

		customLabels.clear();
		deleteSeries();
		//
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
			if(usedSignalType.equals(SignalType.PROFILE)) {
				addLineSeriesData(getLineSeriesDataList(scan));
			} else {
				addBarSeriesData(getBarSeriesDataList(scan));
			}
		}
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
		labelHighestIntensities = preferenceStore.getInt(PreferenceConstants.P_SCAN_LABEL_HIGHEST_INTENSITIES);
		/*
		 * Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		chartSettings.getRangeRestriction().setForceZeroMinY(false);
		rangeRestriction.setRestrictZoom(true);
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

	private List<IBarSeriesData> getBarSeriesDataList(IScan scan) {

		List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
		ISeriesData seriesData = getSeriesData(scan);
		IBarSeriesData barSeriesData = new BarSeriesData(seriesData);
		barSeriesDataList.add(barSeriesData);
		return barSeriesDataList;
	}

	private List<ILineSeriesData> getLineSeriesDataList(IScan scan) {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		ISeriesData seriesData = getSeriesData(scan);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		lineSeriesDataList.add(lineSeriesData);
		return lineSeriesDataList;
	}

	private ISeriesData getSeriesData(IScan scan) {

		double[] xSeries;
		double[] ySeries;
		String scanNumber = (scan.getScanNumber() > 0 ? Integer.toString(scan.getScanNumber()) : "--");
		String id = "Scan " + scanNumber;
		//
		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			List<IIon> ions = scanMSD.getIons();
			int size = ions.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IIon ion : ions) {
				xSeries[index] = ion.getIon();
				ySeries[index] = ion.getAbundance();
				index++;
			}
		} else if(scan instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)scan;
			xSeries = new double[]{scanCSD.getRetentionTime()};
			ySeries = new double[]{scanCSD.getTotalSignal()};
		} else if(scan instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)scan;
			List<IScanSignalWSD> scanSignalsWSD = scanWSD.getScanSignals();
			int size = scanSignalsWSD.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IScanSignalWSD scanSignalWSD : scanSignalsWSD) {
				xSeries[index] = scanSignalWSD.getWavelength();
				ySeries[index] = scanSignalWSD.getAbundance();
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
