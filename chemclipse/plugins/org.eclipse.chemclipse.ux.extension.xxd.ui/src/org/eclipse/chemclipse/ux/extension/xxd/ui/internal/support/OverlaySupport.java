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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.swtchart.LineStyle;

public class OverlaySupport {

	/*
	 * Overlay Type
	 */
	public static final String OVERLAY_TYPE_TIC = "TIC"; // Total Intensity Chromatogram
	public static final String OVERLAY_TYPE_BPC = "BPC"; // Base Peak Chromatogram
	public static final String OVERLAY_TYPE_XIC = "XIC"; // Extracted Ion Chromatogram
	public static final String OVERLAY_TYPE_SIC = "SIC"; // Selected Ion Chromatogram
	public static final String OVERLAY_TYPE_TSC = "TSC"; // Total Substracted Chromatogram
	// public static final String OVERLAY_TYPE_SRM = "SRM"; // Single Reaction Monitoring
	// public static final String OVERLAY_TYPE_MRM = "MRM"; // Single Reaction Monitoring
	/*
	 * Delimiters
	 */
	public static final String OVERLAY_TYPE_CONCATENATOR = "+";
	public static final String ESCAPE_CONCATENATOR = "\\";
	public static final String SELECTED_IONS_CONCATENATOR = " ";
	public static final String EDITOR_TAB = "_EditorTab#";
	public static final String OVERLAY_START_MARKER = "_(";
	public static final String OVERLAY_STOP_MARKER = ")";
	public static final String DELIMITER_ION_DERIVATIVE = ",";
	//
	public static final String DERIVATIVE_NONE = "--";
	public static final String DERIVATIVE_FIRST = "1st";
	public static final String DERIVATIVE_SECOND = "2nd";
	public static final String DERIVATIVE_THIRD = "3rd";
	//
	public static final String SELECTED_IONS_DEFAULT = "18 28 32 84 207";
	public static final String SELECTED_IONS_HYDROCARBONS = "Hydrocarbons";
	public static final String SELECTED_IONS_FATTY_ACIDS = "Fatty Acids";
	public static final String SELECTED_IONS_FAME = "FAME";
	public static final String SELECTED_IONS_SOLVENT_TAILING = "Solvent Tailing";
	public static final String SELECTED_IONS_COLUMN_BLEED = "Column Bleed";
	//
	public static final String DISPLAY_MODUS_NORMAL = "Normal";
	public static final String DISPLAY_MODUS_MIRRORED = "Mirrored";
	//
	private String[] overlayTypes;
	private String[] derivativeTypes;
	private String[] selectedIons;
	private String[] displayModi;
	private Map<String, String> selectedIonsMap;
	//
	private IColorScheme colorSchemeNormal;
	private Map<String, Color> usedColorsNormal;
	private IColorScheme colorSchemeSIC;
	private Map<String, Color> usedColorsSIC;
	//
	private LineStyle lineStyleTIC;
	private LineStyle lineStyleBPC;
	private LineStyle lineStyleXIC;
	private LineStyle lineStyleSIC;
	private LineStyle lineStyleTSC;
	private LineStyle lineStyleDefault;

	public OverlaySupport() {
		initialize();
	}

	public String[] getOverlayTypes() {

		return overlayTypes;
	}

	public String[] getDerivativeTypes() {

		return derivativeTypes;
	}

	public String[] getSelectedIons() {

		return selectedIons;
	}

	public String[] getDisplayModi() {

		return displayModi;
	}

	public Map<String, String> getSelectedIonsMap() {

		return selectedIonsMap;
	}

	public double getIntensity(IScan scan, String overlayType, String derivativeType, List<Integer> ions) {

		double intensity = 0.0d;
		if(overlayType.equals(OVERLAY_TYPE_TIC)) {
			/*
			 * TIC
			 */
			intensity = scan.getTotalSignal();
		} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
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
		} else if(overlayType.equals(OVERLAY_TYPE_XIC) || overlayType.equals(OVERLAY_TYPE_SIC)) {
			/*
			 * XIC, SIC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				for(int ion : ions) {
					intensity += extractedIonSignal.getAbundance(ion);
				}
			}
		} else if(overlayType.equals(OVERLAY_TYPE_TSC)) {
			/*
			 * TSC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = scanMSD.getTotalSignal();
				for(int ion : ions) {
					intensity -= extractedIonSignal.getAbundance(ion);
				}
			}
		}
		//
		return intensity;
	}

	public ILineSeriesData getLineSeriesData(IChromatogram chromatogram, String seriesId, String overlayType, String derivativeType, Color color, List<Integer> ions) {

		double[] xSeries = new double[chromatogram.getNumberOfScans()];
		double[] ySeries = new double[chromatogram.getNumberOfScans()];
		LineStyle lineStyle = getLineStyle(overlayType);
		/*
		 * Get the data.
		 */
		int index = 0;
		for(IScan scan : chromatogram.getScans()) {
			/*
			 * Get the retention time and intensity.
			 */
			xSeries[index] = scan.getRetentionTime();
			ySeries[index] = getIntensity(scan, overlayType, derivativeType, ions);
			index++;
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
		ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
		lineSerieSettings.setLineColor(color);
		lineSerieSettings.setLineStyle(lineStyle);
		lineSerieSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSerieSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	public Color getSeriesColor(String seriesId, String overlayType) {

		Color color;
		if(OverlaySupport.OVERLAY_TYPE_SIC.equals(overlayType)) {
			/*
			 * SIC
			 */
			color = usedColorsSIC.get(seriesId);
			if(color == null) {
				color = colorSchemeSIC.getColor();
				colorSchemeSIC.incrementColor();
				usedColorsSIC.put(seriesId, color);
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

	public void resetColorMaps() {

		colorSchemeNormal.reset();
		usedColorsNormal.clear();
		colorSchemeSIC.reset();
		usedColorsSIC.clear();
	}

	public void loadUserSettings() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		//
		colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_OVERLAY_NORMAL));
		colorSchemeSIC = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_OVERLAY_SIC));
		//
		lineStyleTIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_TIC));
		lineStyleBPC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_BPC));
		lineStyleXIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_XIC));
		lineStyleSIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_SIC));
		lineStyleTSC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_TSC));
		lineStyleDefault = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_DEFAULT));
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

	private LineStyle getLineStyle(String overlayType) {

		LineStyle lineStyle;
		if(overlayType.equals(OVERLAY_TYPE_TIC)) {
			lineStyle = lineStyleTIC;
		} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
			lineStyle = lineStyleBPC;
		} else if(overlayType.equals(OVERLAY_TYPE_XIC)) {
			lineStyle = lineStyleXIC;
		} else if(overlayType.equals(OVERLAY_TYPE_SIC)) {
			lineStyle = lineStyleSIC;
		} else if(overlayType.equals(OVERLAY_TYPE_TSC)) {
			lineStyle = lineStyleTSC;
		} else {
			lineStyle = lineStyleDefault;
		}
		return lineStyle;
	}

	private void initialize() {

		usedColorsNormal = new HashMap<String, Color>();
		usedColorsSIC = new HashMap<String, Color>();
		//
		overlayTypes = new String[]{//
				OVERLAY_TYPE_TIC, //
				OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TSC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_TSC};
		//
		derivativeTypes = new String[]{//
				DERIVATIVE_NONE, //
				DERIVATIVE_FIRST, //
				DERIVATIVE_SECOND, //
				DERIVATIVE_THIRD};
		//
		selectedIons = new String[]{//
				SELECTED_IONS_HYDROCARBONS, //
				SELECTED_IONS_FATTY_ACIDS, //
				SELECTED_IONS_FAME, //
				SELECTED_IONS_SOLVENT_TAILING, //
				SELECTED_IONS_COLUMN_BLEED};
		//
		displayModi = new String[]{//
				DISPLAY_MODUS_NORMAL, //
				DISPLAY_MODUS_MIRRORED //
		};
		//
		selectedIonsMap = new HashMap<String, String>();
		selectedIonsMap.put(SELECTED_IONS_HYDROCARBONS, "57 71 85");
		selectedIonsMap.put(SELECTED_IONS_FATTY_ACIDS, "74 84");
		selectedIonsMap.put(SELECTED_IONS_FAME, "79 81");
		selectedIonsMap.put(SELECTED_IONS_SOLVENT_TAILING, "84");
		selectedIonsMap.put(SELECTED_IONS_COLUMN_BLEED, "207");
		//
		loadUserSettings();
	}
}
