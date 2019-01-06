/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMarkedSignal;
import org.eclipse.chemclipse.model.core.IMarkedSignals;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignals;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ChromatogramChartSupport {

	public static final String EDITOR_TAB = "_EditorTab#";
	public static final String REFERENCE_MARKER = "_Reference#";
	public static final String DERIVATIVE_NONE = "--";
	public static final String DERIVATIVE_FIRST = "1st";
	public static final String DERIVATIVE_SECOND = "2nd";
	public static final String DERIVATIVE_THIRD = "3rd";
	//
	private IColorScheme colorSchemeNormal;
	private Map<String, Color> usedColorsNormal;
	private IColorScheme colorSchemeSIC;
	private Map<String, Color> usedColorsSIC;
	private IColorScheme colorSchemeSWC;
	private Map<String, Color> usedColorsSWC;
	//
	private LineStyle lineStyleTIC;
	private LineStyle lineStyleBPC;
	private LineStyle lineStyleXIC;
	private LineStyle lineStyleSIC;
	private LineStyle lineStyleTSC;
	private LineStyle lineStyleSWC;
	private LineStyle lineStyleAWC;
	private LineStyle lineStyleDefault;
	//
	private boolean showArea = false;

	public ChromatogramChartSupport() {
		usedColorsNormal = new HashMap<String, Color>();
		usedColorsSIC = new HashMap<String, Color>();
		usedColorsSWC = new HashMap<String, Color>();
		loadUserSettings();
	}

	public void loadUserSettings() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		//
		colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_NORMAL_OVERLAY));
		colorSchemeSIC = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SIC_OVERLAY));
		colorSchemeSWC = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SWC_OVERLAY));
		lineStyleTIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_TIC_OVERLAY));
		lineStyleBPC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_BPC_OVERLAY));
		lineStyleXIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_XIC_OVERLAY));
		lineStyleSIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_SIC_OVERLAY));
		lineStyleTSC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_TSC_OVERLAY));
		lineStyleSWC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_SWC_OVERLAY));
		lineStyleAWC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_XWC_OVERLAY));
		lineStyleDefault = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_DEFAULT_OVERLAY));
		showArea = preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_SHOW_AREA);
		//
		resetColorMaps();
	}

	public Color getSeriesColor(String seriesId, DisplayType dataType) {

		Color color;
		if(DisplayType.SIC.equals(dataType)) {
			/*
			 * SIC
			 */
			color = usedColorsSIC.get(seriesId);
			if(color == null) {
				color = colorSchemeSIC.getColor();
				colorSchemeSIC.incrementColor();
				usedColorsSIC.put(seriesId, color);
			}
		} else if(DisplayType.SWC.equals(dataType)) {
			/*
			 * SIC
			 */
			color = usedColorsSWC.get(seriesId);
			if(color == null) {
				color = colorSchemeSWC.getColor();
				colorSchemeSWC.incrementColor();
				usedColorsSWC.put(seriesId, color);
			}
		} else {
			/*
			 * Normal
			 */
			color = usedColorsNormal.get(seriesId);
			if(color == null) {
				color = colorSchemeNormal.getColor();
				colorSchemeNormal.incrementColor();
				usedColorsNormal.put(seriesId, color);
			}
		}
		return color;
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesDataChromatogram(IChromatogramSelection chromatogramSelection, String seriesId, Color color) {

		DisplayType dataType = DisplayType.TIC;
		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogramSelection, seriesId, dataType, derivativeType, color, null, false);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesDataChromatogram(IChromatogram chromatogram, String seriesId, Color color) {

		DisplayType dataType = DisplayType.TIC;
		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogram, seriesId, dataType, derivativeType, color, null, false);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesDataBaseline(IChromatogramSelection chromatogramSelection, String seriesId, Color color) {

		DisplayType dataType = DisplayType.TIC;
		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogramSelection, seriesId, dataType, derivativeType, color, null, true);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesDataBaseline(IChromatogram chromatogram, String seriesId, Color color) {

		DisplayType dataType = DisplayType.TIC;
		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogram, seriesId, dataType, derivativeType, color, null, true);
	}

	public ILineSeriesData getLineSeriesDataBaseline(IChromatogramSelection<?> chromatogramSelection, String seriesId, DisplayType dataType, Color color, boolean timeIntervalSelection) {

		return getLineSeriesData(chromatogramSelection, seriesId, dataType, DERIVATIVE_NONE, color, true, timeIntervalSelection);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesDataBaseline(IChromatogram chromatogram, String seriesId, DisplayType dataType, Color color, IMarkedSignals<? extends IMarkedSignal> signals) {

		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogram, seriesId, dataType, derivativeType, color, signals, true);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesData(IChromatogram chromatogram, String seriesId, DisplayType dataType, Color color, IMarkedSignals<? extends IMarkedSignal> signals) {

		return getLineSeriesData(chromatogram, seriesId, dataType, DERIVATIVE_NONE, color, signals, false);
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?> chromatogramSelection, String seriesId, DisplayType dataType, Color color, boolean timeIntervalSelection) {

		return getLineSeriesData(chromatogramSelection, seriesId, dataType, DERIVATIVE_NONE, color, false, timeIntervalSelection);
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?> chromatogramSelection, String seriesId, DisplayType dataType, String derivativeType, Color color, boolean timeIntervalSelection) {

		return getLineSeriesData(chromatogramSelection, seriesId, dataType, derivativeType, color, false, timeIntervalSelection);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesData(IChromatogramSelection chromatogramSelection, String seriesId, DisplayType dataType, String derivativeType, Color color, IMarkedSignals<? extends IMarkedSignal> signals, boolean baseline) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, dataType, derivativeType, color, signals, baseline);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesData(IChromatogram chromatogram, String seriesId, DisplayType dataType, String derivativeType, Color color, IMarkedSignals<? extends IMarkedSignal> signals, boolean baseline) {

		int startScan = 1;
		int stopScan = chromatogram.getNumberOfScans();
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, dataType, derivativeType, color, signals, baseline);
	}

	@Deprecated
	public ILineSeriesData getLineSeriesData(IExtractedWavelengthSignals extractedWavelengthSignals, int wavelength, String seriesId, DisplayType dataType) {

		Color color = getSeriesColor(seriesId, dataType);
		LineStyle lineStyle = getLineStyle(dataType);
		//
		int length = extractedWavelengthSignals.getExtractedWavelengthSignals().size();
		double[] xSeries = new double[length];
		double[] ySeries = new double[length];
		//
		int index = 0;
		for(IExtractedWavelengthSignal extractedWavelengthSignal : extractedWavelengthSignals.getExtractedWavelengthSignals()) {
			/*
			 * X,Y array
			 */
			xSeries[index] = extractedWavelengthSignal.getRetentionTime();
			ySeries[index] = extractedWavelengthSignal.getAbundance(wavelength);
			index++;
		}
		//
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setLineStyle(lineStyle);
		lineSeriesSettings.setEnableArea(showArea);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private ILineSeriesData getLineSeriesData(IChromatogram chromatogram, int startScan, int stopScan, String seriesId, DisplayType dataType, String derivativeType, Color color, IMarkedSignals<? extends IMarkedSignal> signals, boolean baseline) {

		IBaselineModel baselineModel = null;
		if(baseline) {
			if(dataType.equals(DisplayType.TIC)) {
				baselineModel = chromatogram.getBaselineModel();
			} else if(dataType.equals(DisplayType.SWC)) {
				if(chromatogram instanceof IChromatogramWSD && signals instanceof IMarkedWavelengths) {
					IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
					IMarkedWavelengths markedWavelengths = (IMarkedWavelengths)signals;
					if(!markedWavelengths.isEmpty()) {
						double wavelength = markedWavelengths.iterator().next().getWavelength();
						baselineModel = chromatogramWSD.getBaselineModel(wavelength);
					}
				}
			}
		}
		LineStyle lineStyle = getLineStyle(dataType);
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean condenseCycleNumberScans = preferenceStore.getBoolean(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS);
		//
		double[] xSeries;
		double[] ySeries;
		//
		if(chromatogram.containsScanCycles() && condenseCycleNumberScans) {
			/*
			 * TandemMS + Cycles
			 */
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals totalScanSignals = totalIonSignalExtractor.getTotalScanSignals(startScan, stopScan, false, condenseCycleNumberScans);
			//
			int length = totalScanSignals.size();
			xSeries = new double[length];
			ySeries = new double[length];
			//
			int index = 0;
			for(ITotalScanSignal totalScanSignal : totalScanSignals.getTotalScanSignals()) {
				/*
				 * Get the retention time and intensity.
				 */
				int retentionTime = totalScanSignal.getRetentionTime();
				xSeries[index] = retentionTime;
				if(baseline) {
					ySeries[index] = baselineModel.getBackground(retentionTime);
				} else {
					ySeries[index] = totalScanSignal.getTotalSignal();
				}
				index++;
			}
		} else {
			/*
			 * Normal
			 */
			int length = stopScan - startScan + 1;
			xSeries = new double[length];
			ySeries = new double[length];
			//
			int index = 0;
			for(int i = startScan; i <= stopScan; i++) {
				/*
				 * Get the retention time and intensity.
				 */
				IScan scan = chromatogram.getScan(i);
				int retentionTime = scan.getRetentionTime();
				xSeries[index] = retentionTime;
				if(baseline) {
					ySeries[index] = baselineModel.getBackground(retentionTime);
				} else {
					ySeries[index] = getIntensity(scan, dataType, signals);
				}
				index++;
			}
		}
		/*
		 * remove NAN values
		 */
		int numberNaN = (int)Arrays.stream(ySeries).filter(Double::isNaN).count();
		if(numberNaN > 0) {
			int newSize = ySeries.length - numberNaN;
			double[] xSeriesWithoutNanValues = new double[newSize];
			double[] ySeriesWithoutNanValues = new double[newSize];
			int j = 0;
			for(int i = 0; i < ySeries.length; i++) {
				if(!Double.isNaN(ySeries[i])) {
					xSeriesWithoutNanValues[j] = xSeries[i];
					ySeriesWithoutNanValues[j] = ySeries[i];
					j++;
				}
			}
			xSeries = xSeriesWithoutNanValues;
			ySeries = ySeriesWithoutNanValues;
		}
		/*
		 * Calculate a derivative?
		 */
		int derivatives = getNumberOfDerivatives(derivativeType);
		for(int i = 1; i <= derivatives; i++) {
			ySeries = calculateDerivate(ySeries);
		}
		/*
		 * Add the series.
		 */
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setLineStyle(lineStyle);
		lineSeriesSettings.setEnableArea(showArea);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	public int getCompressionLength(String compressionType, int sizeLineSeries) {

		int compressionToLength = LineChart.LOW_COMPRESSION;
		switch(compressionType) {
			case LineChart.COMPRESSION_AUTO:
				if(sizeLineSeries >= 15) {
					compressionToLength = LineChart.EXTREME_COMPRESSION;
				} else if(sizeLineSeries >= 10) {
					compressionToLength = LineChart.HIGH_COMPRESSION;
				} else if(sizeLineSeries >= 5) {
					compressionToLength = LineChart.MEDIUM_COMPRESSION;
				}
				break;
			case LineChart.COMPRESSION_NONE:
				compressionToLength = LineChart.NO_COMPRESSION;
				break;
			case LineChart.COMPRESSION_LOW:
				compressionToLength = LineChart.LOW_COMPRESSION;
				break;
			case LineChart.COMPRESSION_MEDIUM:
				compressionToLength = LineChart.MEDIUM_COMPRESSION;
				break;
			case LineChart.COMPRESSION_HIGH:
				compressionToLength = LineChart.HIGH_COMPRESSION;
				break;
			case LineChart.COMPRESSION_EXTREME:
				compressionToLength = LineChart.EXTREME_COMPRESSION;
				break;
		}
		//
		return compressionToLength;
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?> chromatogramSelection, String seriesId, String derivativeType, Color color, boolean baseline, boolean timeIntervalSelection) {

		DisplayType dataType = null;
		if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			dataType = DisplayType.SWC;
		} else if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			dataType = DisplayType.TIC;
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			dataType = DisplayType.TIC;
		} else {
			dataType = DisplayType.TIC;
		}
		return getLineSeriesData(chromatogramSelection, seriesId, dataType, DERIVATIVE_NONE, color, baseline, timeIntervalSelection);
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?> chromatogramSelection, String seriesId, DisplayType dataType, String derivativeType, Color color, boolean baseline, boolean timeIntervalSelection) {

		/*
		 * refreshUpdateOverlayChart
		 * Select which series shall be displayed.
		 */
		//
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		IMarkedSignals<?> markedSignals = null;
		//
		if(dataType.equals(DisplayType.SIC) || dataType.equals(DisplayType.XIC)) {
			/*
			 * SIC | XIC
			 */
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			markedSignals = chromatogramSelectionMSD.getSelectedIons();
		} else if(dataType.equals(DisplayType.SWC) || dataType.equals(DisplayType.XWC)) {
			/*
			 * SWC
			 */
			IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
			IMarkedWavelengths markedWavelengths = chromatogramSelectionWSD.getSelectedWavelengths();
			return getLineSeriesData(chromatogram, seriesId, dataType, derivativeType, color, markedWavelengths, baseline);
		} else if(dataType.equals(DisplayType.TSC)) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			markedSignals = chromatogramSelectionMSD.getExcludedIons();
		} else if(dataType.equals(DisplayType.BPC)) {
			/*
			 * BPC
			 */
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
		} else if(dataType.equals(DisplayType.TIC)) {
			/*
			 * TIC
			 */
		} else {
			throw new IllegalArgumentException("Type " + dataType + " is not supported");
		}
		int startScan;
		int stopScan;
		if(timeIntervalSelection) {
			startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		} else {
			startScan = 1;
			stopScan = chromatogram.getNumberOfScans();
		}
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, dataType, derivativeType, color, markedSignals, baseline);
	}

	private double getIntensity(IScan scan, DisplayType dataType, IMarkedSignals<? extends IMarkedSignal> signals) {

		double intensity = Double.NaN;
		if(dataType.equals(DisplayType.TIC)) {
			/*
			 * TIC
			 */
			intensity = scan.getTotalSignal();
		} else if(dataType.equals(DisplayType.BPC)) {
			/*
			 * BPC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IIon ion = scanMSD.getHighestAbundance();
				if(ion != null) {
					intensity = ion.getAbundance();
				}
			}
		} else if(dataType.equals(DisplayType.XIC)) {
			/*
			 * XIC
			 */
			if(scan instanceof IScanMSD && signals instanceof IMarkedIons) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IMarkedIons markedIons = (IMarkedIons)signals;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = 0.0d;
				if(signals != null) {
					for(IMarkedIon markedIon : markedIons) {
						intensity += extractedIonSignal.getAbundance((int)markedIon.getIon());
					}
				}
			}
		} else if(dataType.equals(DisplayType.SIC)) {
			/*
			 * SIC
			 */
			if(scan instanceof IScanMSD && signals instanceof IMarkedIons) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IMarkedIons markedIons = (IMarkedIons)signals;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = 0.0d;
				if(signals != null) {
					Iterator<IMarkedIon> it = markedIons.iterator();
					if(it.hasNext()) {
						intensity = extractedIonSignal.getAbundance((int)it.next().getIon());
					}
				}
			}
		} else if(dataType.equals(DisplayType.TSC)) {
			/*
			 * TSC
			 */
			if(scan instanceof IScanMSD && signals instanceof IMarkedIons) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IMarkedIons markedIons = (IMarkedIons)signals;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = scanMSD.getTotalSignal();
				if(signals != null) {
					for(IMarkedIon markedIon : markedIons) {
						intensity -= extractedIonSignal.getAbundance((int)markedIon.getIon());
					}
				}
			}
		} else if(dataType.equals(DisplayType.SWC) | dataType.equals(DisplayType.XWC)) {
			/*
			 * SWC
			 */
			if(scan instanceof IScanWSD && signals instanceof IMarkedWavelengths) {
				IScanWSD scanWSD = (IScanWSD)scan;
				IMarkedWavelengths markedWavelengths = (IMarkedWavelengths)signals;
				Iterator<IMarkedWavelength> it = markedWavelengths.iterator();
				if(it.hasNext()) {
					Optional<IScanSignalWSD> scanSignal = scanWSD.getScanSignal(it.next().getWavelength());
					if(scanSignal.isPresent()) {
						intensity = scanSignal.get().getAbundance();
					}
				}
			}
		}
		//
		return intensity;
	}

	private LineStyle getLineStyle(DisplayType dataType) {

		LineStyle lineStyle;
		if(dataType.equals(DisplayType.TIC)) {
			lineStyle = lineStyleTIC;
		} else if(dataType.equals(DisplayType.BPC)) {
			lineStyle = lineStyleBPC;
		} else if(dataType.equals(DisplayType.XIC)) {
			lineStyle = lineStyleXIC;
		} else if(dataType.equals(DisplayType.SIC)) {
			lineStyle = lineStyleSIC;
		} else if(dataType.equals(DisplayType.TSC)) {
			lineStyle = lineStyleTSC;
		} else if(dataType.equals(DisplayType.SWC)) {
			lineStyle = lineStyleSWC;
		} else if(dataType.equals(DisplayType.XWC)) {
			lineStyle = lineStyleAWC;
		} else {
			lineStyle = lineStyleDefault;
		}
		return lineStyle;
	}

	private int getNumberOfDerivatives(String derivativeType) {

		int derivatives;
		switch(derivativeType) {
			case DERIVATIVE_FIRST:
				derivatives = 1;
				break;
			case DERIVATIVE_SECOND:
				derivatives = 2;
				break;
			case DERIVATIVE_THIRD:
				derivatives = 3;
				break;
			default:
				derivatives = 0;
				break;
		}
		return derivatives;
	}

	private double[] calculateDerivate(double[] values) {

		int size = values.length;
		double[] derivative = new double[size];
		for(int i = 1; i < size; i++) {
			derivative[i] = values[i] - values[i - 1];
		}
		return derivative;
	}

	private void resetColorMaps() {

		colorSchemeNormal.reset();
		usedColorsNormal.clear();
		colorSchemeSIC.reset();
		usedColorsSIC.clear();
		colorSchemeSWC.reset();
		usedColorsSWC.clear();
	}
}
