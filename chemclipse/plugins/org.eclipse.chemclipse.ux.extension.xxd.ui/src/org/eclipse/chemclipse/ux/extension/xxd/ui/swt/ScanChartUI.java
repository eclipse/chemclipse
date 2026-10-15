/*******************************************************************************
 * Copyright (c) 2017, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.model.types.SignalType;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonMSn;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
import org.eclipse.chemclipse.msd.model.support.ScanSupport;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.BarSeriesValue;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.BarSeriesYComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.LabelOption;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.chemclipse.vsd.model.core.IScanVSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesData;
import org.eclipse.swtchart.extensions.barcharts.IBarSeriesSettings;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.ChartType;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.exceptions.SeriesException;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.model.ICustomSeries;
import org.eclipse.swtchart.extensions.model.TextElement;
import org.eclipse.swtchart.extensions.support.ElementSupport;
import org.eclipse.swtchart.extensions.support.PointPrimary;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

public class ScanChartUI extends ScrollableChart {

	public static final String LABEL_SCAN1 = "Unknown";
	public static final String LABEL_SCAN2 = "Reference";

	private static final int LENGTH_HINT_DATA_POINTS = 5000;
	private static final int COMPRESS_TO_LENGTH = Integer.MAX_VALUE;

	private int labelHighestIntensities = 5;
	private boolean addModuloLabels = false;

	private BarSeriesYComparator barSeriesIntensityComparator = new BarSeriesYComparator();
	private Map<Double, String> customLabels = new HashMap<>();
	private LabelPaintListener labelPaintListenerX = new LabelPaintListener(true);
	private LabelPaintListener labelPaintListenerY = new LabelPaintListener(false);
	private SymbolPaintListener symbolPaintListener = new SymbolPaintListener();
	/*
	 * Initialized on use.
	 */
	private LabelOption labelOption;
	private DataType dataType;
	private SignalType signalType;
	private ICustomSeries customSeries;

	private Double molecularIon;

	private DecimalFormat decimalFormatNormalIntensity = ValueFormat.getDecimalFormatEnglish("0");
	private DecimalFormat decimalFormatLowIntensity = ValueFormat.getDecimalFormatEnglish("0.0000");
	private DecimalFormat decimalFormatHighResolution = ValueFormat.getDecimalFormatEnglish("0.000###");
	private ScanChartSupport scanChartSupport = new ScanChartSupport();
	private Font systemFont = Display.getDefault().getSystemFont();

	private ScanDataSupport scanDataSupport = new ScanDataSupport();

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
			customSeries.getTextElements().clear();
			printHighestLabelsNormal(barSeriesValues, e);
			printHighestLabelsMirrored(barSeriesValues, e);
		}

		private void printHighestLabelsNormal(List<BarSeriesValue> barSeriesValues, PaintEvent e) {

			int size = barSeriesValues.size();
			int modulo = size / labelHighestIntensities;
			int limit = (labelHighestIntensities < size) ? labelHighestIntensities : size;

			for(int i = 0; i < size; i++) {
				if(i < limit) {
					BarSeriesValue barSeriesValue = barSeriesValues.get(i);
					printLabel(barSeriesValue, useX, e);
				} else {
					if(addModuloLabels && i % modulo == 0) {
						BarSeriesValue barSeriesValue = barSeriesValues.get(i);
						printLabel(barSeriesValue, useX, e);
					}
				}
			}
		}

		private void printHighestLabelsMirrored(List<BarSeriesValue> barSeriesValues, PaintEvent e) {

			int size = barSeriesValues.size();
			int limit = size - labelHighestIntensities;
			limit = (limit < 0) ? 0 : limit;
			int modulo = size / labelHighestIntensities;

			for(int i = size - 1; i >= 0; i--) {
				if(i >= limit) {
					BarSeriesValue barSeriesValue = barSeriesValues.get(i);
					if(barSeriesValue.getY() < 0) {
						printLabel(barSeriesValue, useX, e);
					}
				} else {
					if(addModuloLabels && i % modulo == 0) {
						BarSeriesValue barSeriesValue = barSeriesValues.get(i);
						if(barSeriesValue.getY() < 0) {
							printLabel(barSeriesValue, useX, e);
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

	private class SymbolPaintListener implements ICustomPaintListener {

		@Override
		public void paintControl(PaintEvent e) {

			List<BarSeriesValue> barSeriesValues = getBarSeriesValuesList();
			Collections.sort(barSeriesValues, barSeriesIntensityComparator);
			printMolecularIon(barSeriesValues, e);
		}

		private void printMolecularIon(List<BarSeriesValue> barSeriesValues, PaintEvent e) {

			if(molecularIon == null) {
				return;
			}
			for(BarSeriesValue barSeriesValue : barSeriesValues) {
				if(barSeriesValue.getX() == molecularIon) {
					// only for the mirrored reference spectrum
					if(barSeriesValue.getY() < 0) {
						drawTriangle(barSeriesValue, 10, e);
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
		addCustomTextSeries();
	}

	public ScanChartUI(Composite parent, int style) {

		super(parent, style);
		setDefaultDataAndSignalType();
		addCustomTextSeries();
	}

	public void activateLabelMarkerX() {

		addSeriesLabelMarker(labelPaintListenerX);
	}

	public void activateLabelMarkerY() {

		addSeriesLabelMarker(labelPaintListenerY);
	}

	public void setMolecularIon(IIon molecularIon) {

		this.molecularIon = molecularIon.getIon();
	}

	private void activateMolecularIonMarker() {

		IPlotArea plotArea = getBaseChart().getPlotArea();
		plotArea.removeCustomPaintListener(symbolPaintListener);
		if(symbolPaintListener != null) {
			plotArea.addCustomPaintListener(symbolPaintListener);
		}
	}

	@Override
	public void setChartType(ChartType chartType) {

		super.setChartType(chartType);
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

			modifyChart(usedDataType, usedSignalType);
			determineLabelOption(usedDataType);
			modifyChart(scan, null);

			IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
			ITheme currentTheme = themeManager.getCurrentTheme();
			ColorRegistry colorRegistry = currentTheme.getColorRegistry();
			Color colorScan1 = colorRegistry.get("org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ScanChart.ColorScanOne");

			if(usedSignalType.equals(SignalType.PROFILE)) {
				setChartType(ChartType.LINE);
				List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
				ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesData(scan, "", false);
				lineSeriesData.getSettings().setLineColor(colorScan1);
				lineSeriesDataList.add(lineSeriesData);
				addLineSeriesData(lineSeriesDataList);
			} else {
				setChartType(ChartType.BAR);
				List<IBarSeriesData> barSeriesDataList = new ArrayList<>();
				IBarSeriesData barSeriesData = scanChartSupport.getBarSeriesData(scan, "", false);
				barSeriesData.getSettings().setBarColor(colorScan1);
				barSeriesDataList.add(barSeriesData);
				addBarSeriesData(barSeriesDataList);
			}
		} else {
			redraw();
			getBaseChart().redraw();
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

			modifyChart(usedDataType, usedSignalType);
			determineLabelOption(usedDataType);
			modifyChart(mirrored);
			modifyChart(scan1, scan2);

			IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
			ITheme currentTheme = themeManager.getCurrentTheme();
			ColorRegistry colorRegistry = currentTheme.getColorRegistry();
			Color colorScan1 = colorRegistry.get("org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ScanChart.ColorScanOne");
			Color colorScan2 = colorRegistry.get("org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ScanChart.ColorScanTwo");

			if(usedSignalType.equals(SignalType.PROFILE)) {
				setChartType(ChartType.LINE);
				List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
				ILineSeriesData lineSeriesDataScan1 = scanChartSupport.getLineSeriesData(scan1, LABEL_SCAN1, false);
				ILineSeriesData lineSeriesDataScan2 = scanChartSupport.getLineSeriesData(scan2, LABEL_SCAN2, mirrored);
				lineSeriesDataScan1.getSettings().setLineColor(colorScan1);
				lineSeriesDataScan2.getSettings().setLineColor(colorScan2);
				lineSeriesDataList.add(lineSeriesDataScan1);
				lineSeriesDataList.add(lineSeriesDataScan2);
				addLineSeriesData(lineSeriesDataList);
			} else {
				setChartType(ChartType.BAR);
				List<IBarSeriesData> barSeriesDataList = new ArrayList<>();
				IBarSeriesData barSeriesDataScan1 = scanChartSupport.getBarSeriesData(scan1, LABEL_SCAN1, false);
				IBarSeriesData barSeriesDataScan2 = scanChartSupport.getBarSeriesData(scan2, LABEL_SCAN2, mirrored);
				IBarSeriesSettings barSeriesSettings1 = barSeriesDataScan1.getSettings();
				IBarSeriesSettings barSeriesSettings2 = barSeriesDataScan2.getSettings();
				barSeriesSettings1.setBarColor(colorScan1);
				barSeriesSettings1.setBarOverlay(true);
				barSeriesSettings2.setBarColor(colorScan2);
				barSeriesSettings2.setBarOverlay(true);
				barSeriesDataList.add(barSeriesDataScan1);
				barSeriesDataList.add(barSeriesDataScan2);
				addBarSeriesData(barSeriesDataList);
			}
		}
	}

	@Override
	public void dispose() {

		systemFont.dispose();
		super.dispose();
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

		IChartSettings chartSettings = getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setForceZeroMinY(forceZeroY);
		rangeRestriction.setReferenceZoomZeroX(false);
		rangeRestriction.setReferenceZoomZeroY(true);
		rangeRestriction.setRestrictZoomX(false);
		rangeRestriction.setRestrictZoomY(true);
		applySettings(chartSettings);
	}

	private boolean isForceZeroMinY(IScan scan) {

		boolean forceZeroY = false;
		if(scan instanceof IScanMSD scanMSD) {
			forceZeroY = (scanMSD.getNumberOfIons() == 1);
		} else if(scan instanceof IScanCSD) {
			forceZeroY = true; // Only 1 signal contained.
		} else if(scan instanceof IScanWSD scanWSD) {
			forceZeroY = (scanWSD.getNumberOfScanSignals() == 1);
		} else if(scan instanceof IScanVSD scanVSD) {
			forceZeroY = (scanVSD.getProcessedSignals().size() == 1);
		}
		return forceZeroY;
	}

	private void modifyChart(boolean mirrored) {

		IChartSettings chartSettings = getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroY(!mirrored);
		rangeRestriction.setForceZeroMinY(mirrored);
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

	public void setLabelOption(LabelOption labelOption) {

		this.labelOption = labelOption;
	}

	private void setDefaultDataAndSignalType() {

		dataType = DataType.AUTO_DETECT;
		signalType = SignalType.AUTO_DETECT;
		modifyChart(DataType.MSD_NOMINAL, signalType);
	}

	private void addCustomTextSeries() {

		customSeries = getBaseChart().createCustomSeries("Masses", "Highest m/z values");
	}

	private DataType determineDataType(IScan scan) {

		DataType usedDataType;
		if(dataType.equals(DataType.AUTO_DETECT)) {
			if(scan instanceof IScanMSD scanMSD) {
				/*
				 * MSD
				 */
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
			} else if(scan instanceof IScanVSD) {
				usedDataType = DataType.VSD;
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
			if(scan instanceof IRegularMassSpectrum massSpectrum) {
				if(massSpectrum.getMassSpectrumType() == MassSpectrumType.PROFILE) {
					usedSignalType = SignalType.PROFILE;
				}
			} else if(scan instanceof IScanWSD scanWSD) {
				if(scanWSD.getNumberOfScanSignals() > 1) {
					usedSignalType = SignalType.PROFILE;
				} else {
					usedSignalType = SignalType.CENTROID;
				}
			} else if(scan instanceof IScanVSD scanVSD) {
				if(scanVSD.getProcessedSignals().size() > 1) {
					usedSignalType = SignalType.PROFILE;
				} else {
					usedSignalType = SignalType.CENTROID;
				}
			}
		} else {
			usedSignalType = signalType;
		}

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
			case VSD:
				labelOption = LabelOption.NOMIMAL;
				break;
			default:
				labelOption = LabelOption.NOMIMAL;
				break;
		}
	}

	private void extractCustomLabels(IScan scan) {

		if(scan instanceof IScanMSD scanMSD) {
			/*
			 * MS/MS
			 */
			if(scanMSD.isTandemMS()) {
				for(IIon ion : scanMSD.getIons()) {
					if(ion instanceof IIonMSn ionMSn) {
						customLabels.put(ion.getIon(), ScanSupport.getLabelTandemMS(ionMSn));
					}
				}
			}
		}
	}

	private void modifyChart(DataType dataType, SignalType signalType) {

		/*
		 * Font
		 */
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();
		FontRegistry fontRegistry = currentTheme.getFontRegistry();
		systemFont = fontRegistry.get("org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ScanChart.Font");
		/*
		 * Preferences
		 */
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		labelHighestIntensities = preferenceStore.getInt(PreferenceSupplier.P_SCAN_LABEL_HIGHEST_INTENSITIES);
		addModuloLabels = preferenceStore.getBoolean(PreferenceSupplier.P_SCAN_LABEL_MODULO_INTENSITIES);
		boolean enableCompress = preferenceStore.getBoolean(PreferenceSupplier.P_SCAN_CHART_ENABLE_COMPRESS);
		/*
		 * Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitle("");
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableCompress(enableCompress);

		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setRestrictFrame(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.ABSOLUTE);
		rangeRestriction.setExtendMinX(2.0d);
		rangeRestriction.setExtendMaxX(2.0d);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMinY(0.0d);
		rangeRestriction.setExtendMaxY(0.25d);

		rangeRestriction.setForceZeroMinY(false);
		rangeRestriction.setZeroY(true);

		LabelPaintListener labelPaintListener = labelPaintListenerX;

		switch(dataType) {
			case MSD_NOMINAL:
			case MSD_TANDEM:
			case MSD_HIGHRES:
				scanDataSupport.setDataTypeMSD(chartSettings);
				IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
				primaryAxisSettingsX.setDecimalFormat(new DecimalFormat());
				activateMolecularIonMarker();
				break;
			case CSD:
				labelPaintListener = labelPaintListenerY;
				rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.RELATIVE);
				rangeRestriction.setExtendMinX(0.1d);
				rangeRestriction.setExtendMaxX(0.1d);
				rangeRestriction.setZeroY(false);
				scanDataSupport.setDataTypeCSD(chartSettings);
				break;
			case WSD:
				/*
				 * No display of wavelengths when DAD is shown.
				 */
				if(signalType == SignalType.PROFILE) {
					labelPaintListener = null;
				}
				scanDataSupport.setDataTypeWSD(chartSettings);
				rangeRestriction.setZeroY(false);
				break;
			case VSD:
				/*
				 * No display of wavenumbers when Raman is shown.
				 */
				if(signalType == SignalType.PROFILE) {
					labelPaintListener = null;
				}
				scanDataSupport.setDataTypeVSD(chartSettings);
				rangeRestriction.setZeroY(true);
				break;
			default:
				scanDataSupport.setDataTypeMSD(chartSettings);
				break;
		}

		for(ISecondaryAxisSettings secondaryAxisSettingsY : chartSettings.getSecondaryAxisSettingsListY()) {
			secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat());
		}

		applySettings(chartSettings);
		addSeriesLabelMarker(labelPaintListener);
	}

	protected void addBarSeriesData(List<IBarSeriesData> barSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
		if(barSeriesDataList != null && !barSeriesDataList.isEmpty()) {
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
					IBarSeries<?> barSeries = (IBarSeries<?>)createSeries(optimizedSeriesData, barSeriesSettings);
					barSeriesSettings.setBarOverlay(true);
					baseChart.applySeriesSettings(barSeries, barSeriesSettings);
					/*
					 * Automatically use stretched if it is a large data set.
					 */
					if(isLargeDataSet(optimizedSeriesData.getXSeries(), optimizedSeriesData.getYSeries(), LENGTH_HINT_DATA_POINTS)) {
						barSeries.setBarWidthStyle(BarWidthStyle.STRETCHED);
					} else {
						barSeries.setBarWidthStyle(barSeriesSettings.getBarWidthStyle());
					}
				} catch(SeriesException e) {

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
		if(lineSeriesDataList != null && !lineSeriesDataList.isEmpty()) {
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
					ISeries<?> lineSeries = createSeries(optimizedSeriesData, lineSeriesSettings);
					baseChart.applySeriesSettings(lineSeries, lineSeriesSettings);
				} catch(SeriesException e) {

				}
			}
			baseChart.suspendUpdate(false);
			adjustRange(true);
			baseChart.redraw();
		}
	}

	private void addSeriesLabelMarker(LabelPaintListener labelPaintListener) {

		/*
		 * Plot the series name above the entry.
		 * Remove and re-add it. There is no way to check if the label
		 * paint listener is already registered.
		 */
		IPlotArea plotArea = getBaseChart().getPlotArea();
		plotArea.removeCustomPaintListener(labelPaintListenerX);
		plotArea.removeCustomPaintListener(labelPaintListenerY);

		if(labelPaintListener != null) {
			plotArea.addCustomPaintListener(labelPaintListener);
		}
	}

	private void printLabel(BarSeriesValue barSeriesValue, boolean useX, PaintEvent e) {

		Font currentFont = e.gc.getFont();
		e.gc.setFont(systemFont);

		Point point = barSeriesValue.getPoint();
		String label = (useX) ? getLabel(barSeriesValue.getX()) : getLabel(barSeriesValue.getY());
		boolean negative = (barSeriesValue.getY() < 0);
		Point labelSize = e.gc.textExtent(label);
		int x = (int)(point.x + 0.5d - labelSize.x / 2.0d);
		int y = point.y;
		if(!negative) {
			y = point.y - labelSize.y;
		}
		e.gc.drawText(label, x, y, true);
		e.gc.setFont(currentFont);
		/*
		 * Print Export
		 */
		IAxis axisX = getBaseChart().getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
		IAxis axisY = getBaseChart().getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS);
		ElementSupport elementSupport = new ElementSupport(axisX.getRange(), axisY.getRange(), e.width, e.height);
		TextElement textElement = new TextElement();
		textElement.setLabel(label);
		textElement.setColor(Colors.BLACK);
		PointPrimary pointLabel = elementSupport.convertPoint(point.x, point.y);
		PointPrimary pointSize = elementSupport.convertPoint(labelSize.x, labelSize.y);
		textElement.setX(pointLabel.getX() - pointSize.getX() / 32);
		textElement.setY(pointLabel.getY());
		textElement.setRotation(0);
		customSeries.getTextElements().add(textElement);
	}

	private void drawTriangle(BarSeriesValue barSeriesValue, int size, PaintEvent e) {

		int x = barSeriesValue.getPoint().x;
		int y = barSeriesValue.getPoint().y - size;

		int[] triangle = {x, y, // top
				x - size / 2, y + size, // bottom left
				x + size / 2, y + size // bottom right
		};

		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_DARK_GRAY));
		e.gc.fillPolygon(triangle);
	}

	private String getLabel(double value) {

		String label = "";
		if(labelOption != null) {
			switch(labelOption) {
				case NOMIMAL:
					if(value > -1.0d && value < 0.0d || (value > 0.0d && value < 1.0d)) {
						label = decimalFormatLowIntensity.format(value);
					} else {
						label = decimalFormatNormalIntensity.format(value);
					}
					break;
				case EXACT:
					label = decimalFormatHighResolution.format(value);
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
		}

		return label;
	}

	private List<BarSeriesValue> getBarSeriesValuesList() {

		List<BarSeriesValue> barSeriesIons = new ArrayList<>();

		int widthPlotArea = getBaseChart().getPlotArea().getSize().x;
		ISeries<?>[] series = getBaseChart().getSeriesSet().getSeries();
		for(ISeries<?> barSeries : series) {
			if(barSeries != null) {

				double[] xSeries = barSeries.getXSeries();
				double[] ySeries = barSeries.getYSeries();
				int size = barSeries.getXSeries().length;

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
}
