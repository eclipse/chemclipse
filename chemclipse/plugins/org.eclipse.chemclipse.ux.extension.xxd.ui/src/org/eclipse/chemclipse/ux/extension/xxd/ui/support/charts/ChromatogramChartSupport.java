/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - allow setting of the preference store via constructor
 * Matthias Mailänder - Add support for Max Plots
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMarkedTrace;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
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
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignals;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ICompressionSupport;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ChromatogramChartSupport {

	public static final String EDITOR_TAB = "_EditorTab#";
	public static final String REFERENCE_MARKER = "_Reference#";
	//
	private IColorScheme colorScheme;
	private final Map<String, Color> usedColors = new HashMap<>();
	private LineStyle lineStyle;
	//
	private boolean showArea = false;
	private final IPreferenceStore preferenceStore;

	/**
	 * Creates a chart support with the default preference store
	 */
	public ChromatogramChartSupport() {

		this(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * creates a chart support with the given preference store, make sure that it is properly initialized (see {@link #initializeDefaults(IPreferenceStore)}
	 * 
	 * @param preferenceStore
	 */
	public ChromatogramChartSupport(IPreferenceStore preferenceStore) {

		this.preferenceStore = preferenceStore;
		loadUserSettings();
	}

	public void loadUserSettings() {

		//
		colorScheme = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_OVERLAY));
		lineStyle = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_OVERLAY));
		showArea = preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_SHOW_AREA);
		//
		resetColorMaps();
	}

	public Color getSeriesColor(String seriesId, DisplayType dataType) {

		Color color = usedColors.get(seriesId);
		if(color == null) {
			color = colorScheme.getColor();
			colorScheme.incrementColor();
			usedColors.put(seriesId, color);
		}
		return color;
	}

	public ILineSeriesData getLineSeriesDataChromatogram(IChromatogramSelection<?, ?> chromatogramSelection, String seriesId, Color color) {

		DisplayType dataType = DisplayType.TIC;
		return getLineSeriesData(chromatogramSelection, seriesId, dataType, Derivative.NONE, color, null, false);
	}

	public ILineSeriesData getLineSeriesDataChromatogram(IChromatogram<?> chromatogram, String seriesId, Color color) {

		DisplayType dataType = DisplayType.TIC;
		return getLineSeriesData(chromatogram, seriesId, dataType, Derivative.NONE, color, null, false);
	}

	public ILineSeriesData getLineSeriesDataBaseline(IChromatogramSelection<?, ?> chromatogramSelection, String seriesId, Color color) {

		DisplayType dataType = DisplayType.TIC;
		return getLineSeriesData(chromatogramSelection, seriesId, dataType, Derivative.NONE, color, null, true);
	}

	public ILineSeriesData getLineSeriesDataBaseline(IChromatogram<?> chromatogram, String seriesId, Color color) {

		DisplayType dataType = DisplayType.TIC;
		return getLineSeriesData(chromatogram, seriesId, dataType, Derivative.NONE, color, null, true);
	}

	public ILineSeriesData getLineSeriesDataBaseline(IChromatogramSelection<?, ?> chromatogramSelection, String seriesId, DisplayType dataType, Color color, boolean timeIntervalSelection) {

		return getLineSeriesData(chromatogramSelection, seriesId, dataType, Derivative.NONE, color, true, timeIntervalSelection);
	}

	public ILineSeriesData getLineSeriesDataBaseline(IChromatogram<?> chromatogram, String seriesId, DisplayType dataType, Color color, IMarkedTraces<? extends IMarkedTrace> signals) {

		return getLineSeriesData(chromatogram, seriesId, dataType, Derivative.NONE, color, signals, true);
	}

	public ILineSeriesData getLineSeriesData(IChromatogram<?> chromatogram, String seriesId, DisplayType displayType, Color color, IMarkedTraces<? extends IMarkedTrace> signals) {

		return getLineSeriesData(chromatogram, seriesId, displayType, color, signals, false);
	}

	public ILineSeriesData getLineSeriesData(IChromatogram<?> chromatogram, String seriesId, DisplayType displayType, Color color, IMarkedTraces<? extends IMarkedTrace> signals, boolean useRetentionIndex) {

		return getLineSeriesData(chromatogram, seriesId, displayType, Derivative.NONE, color, signals, false, useRetentionIndex);
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?, ?> chromatogramSelection, String seriesId, DisplayType dataType, Color color, boolean timeIntervalSelection) {

		return getLineSeriesData(chromatogramSelection, seriesId, dataType, Derivative.NONE, color, false, timeIntervalSelection);
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?, ?> chromatogramSelection, String seriesId, DisplayType dataType, Derivative derivative, Color color, boolean timeIntervalSelection) {

		return getLineSeriesData(chromatogramSelection, seriesId, dataType, derivative, color, false, timeIntervalSelection);
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?, ?> chromatogramSelection, String seriesId, DisplayType dataType, Derivative derivative, Color color, IMarkedTraces<? extends IMarkedTrace> signals, boolean baseline) {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, dataType, derivative, color, signals, baseline, false);
	}

	public ILineSeriesData getLineSeriesData(IChromatogram<?> chromatogram, String seriesId, DisplayType dataType, Derivative derivative, Color color, IMarkedTraces<? extends IMarkedTrace> signals, boolean baseline) {

		return getLineSeriesData(chromatogram, seriesId, dataType, derivative, color, signals, false, false);
	}

	public ILineSeriesData getLineSeriesData(IChromatogram<?> chromatogram, String seriesId, DisplayType dataType, Derivative derivative, Color color, IMarkedTraces<? extends IMarkedTrace> signals, boolean baseline, boolean useRetentionIndex) {

		int startScan = 1;
		int stopScan = chromatogram.getNumberOfScans();
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, dataType, derivative, color, signals, baseline, useRetentionIndex);
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
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setLineStyle(lineStyle);
		lineSeriesSettings.setEnableArea(showArea);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	private ILineSeriesData getLineSeriesData(IChromatogram<?> chromatogram, int startScan, int stopScan, String seriesId, DisplayType dataType, Derivative derivative, Color color, IMarkedTraces<? extends IMarkedTrace> signals, boolean baseline, boolean useRetentionIndex) {

		IBaselineModel baselineModel = null;
		if(baseline) {
			if(chromatogram instanceof IChromatogramWSD chromatogramWSD && signals instanceof IMarkedWavelengths markedWavelengths) {
				if(!markedWavelengths.isEmpty()) {
					double wavelength = markedWavelengths.iterator().next().getTrace();
					baselineModel = chromatogramWSD.getBaselineModel(wavelength);
				}
			} else {
				baselineModel = chromatogram.getBaselineModel();
			}
		}
		//
		LineStyle lineStyle = getLineStyle(dataType);
		boolean condenseCycleNumberScans = preferenceStore.getBoolean(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS);
		boolean handleScanCycleSeriesTIC = chromatogram.containsScanCycles() && condenseCycleNumberScans && dataType.equals(DisplayType.TIC);
		//
		double[] xSeries;
		double[] ySeries;
		//
		if(handleScanCycleSeriesTIC) {
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
				float retentionIndex = totalScanSignal.getRetentionIndex();
				if(useRetentionIndex) {
					if(retentionIndex > 0) {
						xSeries[index] = retentionIndex;
						if(baseline) {
							ySeries[index] = baselineModel.getBackground(retentionTime);
						} else {
							ySeries[index] = totalScanSignal.getTotalSignal();
						}
					} else {
						ySeries[index] = Double.NaN;
					}
				} else {
					xSeries[index] = retentionTime;
					if(baseline) {
						ySeries[index] = baselineModel.getBackground(retentionTime);
					} else {
						ySeries[index] = totalScanSignal.getTotalSignal();
					}
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
				float retentionIndex = scan.getRetentionIndex();
				if(useRetentionIndex) {
					if(retentionIndex > 0) {
						xSeries[index] = retentionIndex;
						if(baseline) {
							ySeries[index] = baselineModel.getBackground(retentionTime);
						} else {
							ySeries[index] = getIntensity(scan, dataType, signals);
						}
					} else {
						ySeries[index] = Double.NaN;
					}
				} else {
					xSeries[index] = retentionTime;
					if(baseline) {
						ySeries[index] = baselineModel.getBackground(retentionTime);
					} else {
						ySeries[index] = getIntensity(scan, dataType, signals);
					}
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
		int order = derivative.order();
		for(int i = 1; i <= order; i++) {
			ySeries = calculateDerivate(ySeries);
		}
		/*
		 * Add the series.
		 */
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setLineStyle(lineStyle);
		lineSeriesSettings.setEnableArea(showArea);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	public int getCompressionLength(String compressionType, int sizeLineSeries) {

		int compressionToLength = 0;
		switch(compressionType) {
			case ICompressionSupport.COMPRESSION_AUTO:
				if(sizeLineSeries >= 15) {
					compressionToLength = ICompressionSupport.EXTREME_COMPRESSION;
				} else if(sizeLineSeries >= 10) {
					compressionToLength = ICompressionSupport.HIGH_COMPRESSION;
				} else if(sizeLineSeries >= 5) {
					compressionToLength = ICompressionSupport.MEDIUM_COMPRESSION;
				}
				break;
			case ICompressionSupport.COMPRESSION_NONE:
				compressionToLength = ICompressionSupport.NO_COMPRESSION;
				break;
			case ICompressionSupport.COMPRESSION_LOW:
				compressionToLength = ICompressionSupport.LOW_COMPRESSION;
				break;
			case ICompressionSupport.COMPRESSION_MEDIUM:
				compressionToLength = ICompressionSupport.MEDIUM_COMPRESSION;
				break;
			case ICompressionSupport.COMPRESSION_HIGH:
				compressionToLength = ICompressionSupport.HIGH_COMPRESSION;
				break;
			case ICompressionSupport.COMPRESSION_EXTREME:
				compressionToLength = ICompressionSupport.EXTREME_COMPRESSION;
				break;
			default:
				compressionToLength = ICompressionSupport.MEDIUM_COMPRESSION;
				break;
		}
		//
		return compressionToLength;
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?, ?> chromatogramSelection, String seriesId, Color color, boolean baseline, boolean timeIntervalSelection) {

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
		return getLineSeriesData(chromatogramSelection, seriesId, dataType, Derivative.NONE, color, baseline, timeIntervalSelection);
	}

	public ILineSeriesData getLineSeriesData(IChromatogramSelection<?, ?> chromatogramSelection, String seriesId, DisplayType dataType, Derivative derivative, Color color, boolean baseline, boolean timeIntervalSelection) {

		/*
		 * refreshUpdateOverlayChart
		 * Select which series shall be displayed.
		 */
		//
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		IMarkedTraces<?> markedSignals = null;
		//
		if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
			if(dataType.equals(DisplayType.SIC) || dataType.equals(DisplayType.XIC)) {
				markedSignals = chromatogramSelectionMSD.getSelectedIons();
			} else if(dataType.equals(DisplayType.TSC)) {
				markedSignals = chromatogramSelectionMSD.getExcludedIons();
			}
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD chromatogramSelectionWSD) {
			if(dataType.equals(DisplayType.SWC) || dataType.equals(DisplayType.XWC)) {
				IMarkedWavelengths markedWavelengths = chromatogramSelectionWSD.getSelectedWavelengths();
				return getLineSeriesData(chromatogram, seriesId, dataType, derivative, color, markedWavelengths, baseline);
			} else if(dataType.equals(DisplayType.MPC)) {
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
				markedWavelengths.add(chromatogramSelectionWSD.getChromatogram().getWavelengths());
				return getLineSeriesData(chromatogram, seriesId, dataType, derivative, color, markedWavelengths, baseline);
			}
		} else if(dataType.equals(DisplayType.TSC)) {
		} else if(dataType.equals(DisplayType.BPC)) {
		} else if(dataType.equals(DisplayType.TIC)) {
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
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, dataType, derivative, color, markedSignals, baseline, false);
	}

	private double getIntensity(IScan scan, DisplayType dataType, IMarkedTraces<? extends IMarkedTrace> signals) {

		double intensity = Double.NaN;
		if(dataType.equals(DisplayType.TIC)) {
			intensity = scan.getTotalSignal();
		} else if(dataType.equals(DisplayType.BPC)) {
			if(scan instanceof IScanMSD scanMSD) {
				IIon ion = scanMSD.getHighestAbundance();
				if(ion != null) {
					intensity = ion.getAbundance();
				}
			}
		} else if(dataType.equals(DisplayType.XIC)) {
			if(scan instanceof IScanMSD scanMSD && signals instanceof IMarkedIons markedIons) {
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = 0.0d;
				for(IMarkedIon markedIon : markedIons) {
					intensity += extractedIonSignal.getAbundance(markedIon.castTrace());
				}
			}
		} else if(dataType.equals(DisplayType.SIC)) {
			if(scan instanceof IScanMSD scanMSD && signals instanceof IMarkedIons markedIons) {
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = 0.0d;
				Iterator<IMarkedIon> it = markedIons.iterator();
				if(it.hasNext()) {
					intensity = extractedIonSignal.getAbundance(it.next().castTrace());
				}
			}
		} else if(dataType.equals(DisplayType.TSC)) {
			if(scan instanceof IScanMSD scanMSD && signals instanceof IMarkedIons markedIons) {
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = scanMSD.getTotalSignal();
				for(IMarkedIon markedIon : markedIons) {
					intensity -= extractedIonSignal.getAbundance(markedIon.castTrace());
				}
			}
		} else if(dataType.equals(DisplayType.SWC) | dataType.equals(DisplayType.XWC)) {
			if(scan instanceof IScanWSD scanWSD && signals instanceof IMarkedWavelengths markedWavelengths) {
				/*
				 * Get the extracted wavelength map.
				 */
				IExtractedWavelengthSignal extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal();
				Set<Integer> wavelengths = new HashSet<>();
				for(double wavelength : markedWavelengths.getWavelengths()) {
					wavelengths.add((int)Math.round(wavelength));
				}
				/*
				 * This fails when running in SWC modus - needs adjustment.
				 * --
				 * Optional<IScanSignalWSD> scanSignal = scanWSD.getScanSignal(wavelength);
				 * if(scanSignal.isPresent()) {
				 * intensity = scanSignal.get().getAbundance();
				 * }
				 */
				for(int wavelength : wavelengths) {
					intensity = extractedWavelengthSignal.getAbundance(wavelength);
				}
			}
		} else if(dataType.equals(DisplayType.MPC)) {
			/*
			 * Max Plot: each point plotted at maximum absorbance
			 */
			if(scan instanceof IScanWSD scanWSD && signals instanceof IMarkedWavelengths markedWavelengths) {
				float maxIntensity = -Float.MAX_VALUE;
				for(double wavelength : markedWavelengths.getWavelengths()) {
					float abundance = scanWSD.getScanSignal(wavelength).get().getAbundance();
					if(abundance > maxIntensity) {
						maxIntensity = abundance;
					}
				}
				intensity = maxIntensity;
			}
		}
		//
		return intensity;
	}

	private LineStyle getLineStyle(DisplayType dataType) {

		return lineStyle;
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

		colorScheme.reset();
		usedColors.clear();
	}

	/**
	 * Initialize the defaults of the given preference store used by this class
	 * 
	 * @param preferenceStore
	 *            the preference store to use
	 * @return the given preference store for chaining
	 */
	public static IPreferenceStore initializeDefaults(IPreferenceStore preferenceStore) {

		preferenceStore.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_OVERLAY, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_OVERLAY);
		preferenceStore.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_OVERLAY);
		preferenceStore.setDefault(PreferenceConstants.P_OVERLAY_SHOW_AREA, PreferenceConstants.DEF_OVERLAY_SHOW_AREA);
		preferenceStore.setDefault(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS, PreferenceConstants.DEF_CONDENSE_CYCLE_NUMBER_SCANS);
		return preferenceStore;
	}
}