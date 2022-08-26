/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.swt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.BarSeriesIon;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.BarSeriesIonComparator;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.UpdateMenuEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.barcharts.BarChart;
import org.eclipse.swtchart.extensions.barcharts.BarSeriesData;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesData;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.SeriesData;

public class MassSpectrumChartCentroid extends BarChart implements IMassSpectrumChart {

	private static final int MAX_NUMBER_MZ = 50000;

	public enum LabelOption {
		NOMIMAL, EXACT, CUSTOM;
	}

	private static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat();
	//
	private int numberOfHighestIntensitiesToLabel;
	private BarSeriesIonComparator barSeriesIonComparator;
	private LabelOption labelOption;
	private Map<Double, String> customLabels;
	//
	private IScanMSD massSpectrum = null;

	public MassSpectrumChartCentroid() {

		super();
		initialize();
	}

	public MassSpectrumChartCentroid(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	@Override
	public void update(IScanMSD massSpectrum) {

		this.massSpectrum = massSpectrum;
		update();
	}

	@Override
	public void update() {

		deleteSeries();
		if(massSpectrum != null) {
			List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
			ISeriesData seriesData = getMassSpectrum(massSpectrum);
			IBarSeriesData barSeriesData = new BarSeriesData(seriesData);
			barSeriesDataList.add(barSeriesData);
			addSeriesData(barSeriesDataList, MAX_NUMBER_MZ);
		}
	}

	private void initialize() {

		numberOfHighestIntensitiesToLabel = 5;
		barSeriesIonComparator = new BarSeriesIonComparator();
		labelOption = LabelOption.EXACT;
		customLabels = new HashMap<Double, String>();
		//
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(true);
		chartSettings.setCreateMenu(true);
		//
		chartSettings.addMenuEntry(new UpdateMenuEntry());
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(false);
		rangeRestriction.setZeroY(false);
		rangeRestriction.setRestrictFrame(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.ABSOLUTE);
		rangeRestriction.setExtendMinX(2.0d);
		rangeRestriction.setExtendMaxX(2.0d);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMaxY(0.1d);
		//
		setPrimaryAxisSet(chartSettings);
		addSecondaryAxisSet(chartSettings);
		applySettings(chartSettings);
		//
		addSeriesLabelMarker();
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("m/z");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
	}

	private void addSecondaryAxisSet(IChartSettings chartSettings) {

		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("Relative Intensity [%]", new PercentageConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	private void addSeriesLabelMarker() {

		/*
		 * Plot the series name above the entry.
		 */
		IPlotArea plotArea = getBaseChart().getPlotArea();
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				List<BarSeriesIon> barSeriesIons = getBarSeriesIonList();
				Collections.sort(barSeriesIons, barSeriesIonComparator);
				int barSeriesSize = barSeriesIons.size();
				int limit;
				/*
				 * Positive
				 */
				limit = numberOfHighestIntensitiesToLabel;
				for(int i = 0; i < limit; i++) {
					if(i < barSeriesSize) {
						BarSeriesIon barSeriesIon = barSeriesIons.get(i);
						printLabel(barSeriesIon, e);
					}
				}
				/*
				 * Negative
				 */
				limit = barSeriesIons.size() - numberOfHighestIntensitiesToLabel;
				limit = (limit < 0) ? 0 : limit;
				for(int i = barSeriesIons.size() - 1; i >= limit; i--) {
					BarSeriesIon barSeriesIon = barSeriesIons.get(i);
					if(barSeriesIon.getIntensity() < 0) {
						printLabel(barSeriesIon, e);
					}
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
	}

	private void printLabel(BarSeriesIon barSeriesIon, PaintEvent e) {

		Point point = barSeriesIon.getPoint();
		String label = getLabel(barSeriesIon.getMz());
		boolean negative = (barSeriesIon.getIntensity() < 0);
		Point labelSize = e.gc.textExtent(label);
		int x = (int)(point.x + 0.5d - labelSize.x / 2.0d);
		int y = point.y;
		if(!negative) {
			y = point.y - labelSize.y;
		}
		e.gc.drawText(label, x, y, true);
	}

	private String getLabel(double mz) {

		String label;
		switch(labelOption) {
			case NOMIMAL:
				label = Integer.toString((int)mz);
				break;
			case EXACT:
				DecimalFormat decimalFormat = getDecimalFormatMZ();
				label = decimalFormat.format(mz);
				break;
			case CUSTOM:
				label = customLabels.get(mz);
				if(label == null) {
					label = "";
				}
				break;
			default:
				label = "";
		}
		return label;
	}

	private List<BarSeriesIon> getBarSeriesIonList() {

		List<BarSeriesIon> barSeriesIons = new ArrayList<>();
		//
		int widthPlotArea = getBaseChart().getPlotArea().getSize().x;
		ISeries<?>[] series = getBaseChart().getSeriesSet().getSeries();
		for(ISeries<?> barSeries : series) {
			if(barSeries != null) {
				//
				double[] xSeries = barSeries.getXSeries();
				double[] ySeries = barSeries.getYSeries();
				int size = barSeries.getXSeries().length;
				//
				for(int i = 0; i < size; i++) {
					Point point = barSeries.getPixelCoordinates(i);
					if(point.x >= 0 && point.x <= widthPlotArea) {
						barSeriesIons.add(new BarSeriesIon(xSeries[i], ySeries[i], point));
					}
				}
			}
		}
		return barSeriesIons;
	}

	private DecimalFormat getDecimalFormatMZ() {

		IAxisSettings axisSettings = getBaseChart().getXAxisSettings(BaseChart.ID_PRIMARY_X_AXIS);
		if(axisSettings != null) {
			return axisSettings.getDecimalFormat();
		} else {
			return DEFAULT_DECIMAL_FORMAT;
		}
	}

	private ISeriesData getMassSpectrum(IScanMSD massSpectrum) {

		List<IIon> ions = massSpectrum.getIons();
		int size = ions.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		for(int i = 0; i < size; i++) {
			IIon ion = ions.get(i);
			xSeries[i] = ion.getIon();
			ySeries[i] = ion.getAbundance();
		}
		//
		return new SeriesData(xSeries, ySeries, "Mass Spectrum");
	}
}
