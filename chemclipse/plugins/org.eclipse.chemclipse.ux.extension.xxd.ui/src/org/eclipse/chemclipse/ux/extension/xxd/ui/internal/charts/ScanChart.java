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

import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToMinuteConverter;
import org.eclipse.eavp.service.swtchart.axisconverter.RelativeIntensityConverter;
import org.eclipse.eavp.service.swtchart.barcharts.BarChart;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IAxisSettings;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IPrimaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.RangeRestriction;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.customcharts.MassSpectrumChart.LabelOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.IAxis.Position;
import org.swtchart.ICustomPaintListener;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries;

public class ScanChart extends BarChart {

	private static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat();
	//
	private int numberOfHighestIntensitiesToLabel = 5;
	private BarSeriesYComparator barSeriesIntensityComparator = new BarSeriesYComparator();
	private LabelOption labelOption = LabelOption.EXACT;
	private Map<Double, String> customLabels = new HashMap<Double, String>();
	private LabelPaintListener labelPaintListener = new LabelPaintListener(true);
	private Map<String, Font> fonts = new HashMap<String, Font>();
	private String fontId; // is initialized on use

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
			limit = numberOfHighestIntensitiesToLabel;
			for(int i = 0; i < limit; i++) {
				if(i < barSeriesSize) {
					BarSeriesValue barSeriesValue = barSeriesValues.get(i);
					printLabel(barSeriesValue, useX, e);
				}
			}
			/*
			 * Negative
			 */
			limit = barSeriesValues.size() - numberOfHighestIntensitiesToLabel;
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

	public ScanChart() {
		super();
		setDataType(DataType.MSD);
	}

	public ScanChart(Composite parent, int style) {
		super(parent, style);
		setDataType(DataType.MSD);
	}

	@Override
	public void dispose() {

		for(Font font : fonts.values()) {
			font.dispose();
		}
		super.dispose();
	}

	public void setNumberOfHighestIntensitiesToLabel(int numberOfHighestIntensitiesToLabel) {

		if(numberOfHighestIntensitiesToLabel >= 0) {
			this.numberOfHighestIntensitiesToLabel = numberOfHighestIntensitiesToLabel;
		} else {
			this.numberOfHighestIntensitiesToLabel = 0;
		}
	}

	public void setLabelOption(LabelOption labelOption) {

		this.labelOption = labelOption;
	}

	public void setCustomLabels(Map<Double, String> customLabels) {

		if(customLabels != null) {
			this.customLabels = customLabels;
		} else {
			this.customLabels.clear();
		}
	}

	public void setDataType(DataType dataType) {

		String name = "Ubuntu";
		int height = 11;
		int style = SWT.NORMAL;
		fontId = name + height + style;
		if(!fonts.containsKey(fontId)) {
			Font font = new Font(Display.getDefault(), name, height, style);
			fonts.put(fontId, font);
		}
		//
		deleteSeries();
		customLabels.clear();
		//
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
		switch(dataType) {
			case MSD:
				setDataTypeMSD(chartSettings);
				break;
			case CSD:
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
		addSeriesLabelMarker();
	}

	private void setDataTypeMSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "m/z", true, "Intensity");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisY(chartSettings, "Relative Intensity [%]");
	}

	private void setDataTypeCSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "Retention Time [ms]", false, "Current");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisX(chartSettings, "Minutes");
		addSecondaryAxisY(chartSettings, "Relative Current [%]");
	}

	private void setDataTypeWSD(IChartSettings chartSettings) {

		setPrimaryAxisSet(chartSettings, "wavelength [nm]", true, "Intensity");
		clearSecondaryAxes(chartSettings);
		addSecondaryAxisY(chartSettings, "Relative Intensity [%]");
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

	private void addSeriesLabelMarker() {

		/*
		 * Plot the series name above the entry.
		 * Remove and re-add it. There is no way to check if the label
		 * paint listener is already registered.
		 */
		IPlotArea plotArea = (IPlotArea)getBaseChart().getPlotArea();
		plotArea.removeCustomPaintListener(labelPaintListener);
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
			return DEFAULT_DECIMAL_FORMAT;
		}
	}
}
