/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.swt.SWT;
import org.swtchart.LineStyle;

public class PreferenceConstants {

	public static String[][] PART_STACKS = new String[][]{//
			{"--", PartSupport.PARTSTACK_NONE}, //
			{"Left Top", PartSupport.PARTSTACK_LEFT_TOP}, //
			{"Left Center", PartSupport.PARTSTACK_LEFT_CENTER}, //
			{"Right Top", PartSupport.PARTSTACK_RIGHT_TOP}, //
			{"Bottom Left", PartSupport.PARTSTACK_BOTTOM_LEFT}, //
			{"Bottom Center", PartSupport.PARTSTACK_BOTTOM_CENTER}, //
			{"Bottom Right", PartSupport.PARTSTACK_BOTTOM_RIGHT}//
	};
	//
	public static String[][] FONT_STYLES = new String[][]{//
			{"Normal", Integer.toString(SWT.NORMAL)}, //
			{"Bold", Integer.toString(SWT.BOLD)}, //
			{"Italic", Integer.toString(SWT.ITALIC)}//
	};
	//
	public static String[][] COMPRESSION_TYPES = new String[][]{//
			{LineChart.COMPRESSION_EXTREME, LineChart.COMPRESSION_EXTREME}, //
			{LineChart.COMPRESSION_HIGH, LineChart.COMPRESSION_HIGH}, //
			{LineChart.COMPRESSION_MEDIUM, LineChart.COMPRESSION_MEDIUM}, //
			{LineChart.COMPRESSION_LOW, LineChart.COMPRESSION_LOW}, //
			{LineChart.COMPRESSION_NONE, LineChart.COMPRESSION_NONE}//
	};
	/*
	 * General / Task Quick Access
	 */
	public static final String P_STACK_POSITION_OVERLAY = "stackPositionOverlay";
	public static final String DEF_STACK_POSITION_OVERLAY = PartSupport.PARTSTACK_BOTTOM_LEFT;
	public static final String P_STACK_POSITION_TARGETS = "stackPositionTargets";
	public static final String DEF_STACK_POSITION_TARGETS = PartSupport.PARTSTACK_BOTTOM_LEFT;
	public static final String P_STACK_POSITION_SCAN_CHART = "stackPositionScanChart";
	public static final String DEF_STACK_POSITION_SCAN_CHART = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_SCAN_TABLE = "stackPositionScanTable";
	public static final String DEF_STACK_POSITION_SCAN_TABLE = PartSupport.PARTSTACK_BOTTOM_RIGHT;
	public static final String P_STACK_POSITION_PEAK_CHART = "stackPositionPeakChart";
	public static final String DEF_STACK_POSITION_PEAK_CHART = PartSupport.PARTSTACK_BOTTOM_RIGHT;
	public static final String P_STACK_POSITION_PEAK_DETAILS = "stackPositionPeakDetails";
	public static final String DEF_STACK_POSITION_PEAK_DETAILS = PartSupport.PARTSTACK_BOTTOM_LEFT;
	public static final String P_STACK_POSITION_PEAK_DETECTOR = "stackPositionPeakDetector";
	public static final String DEF_STACK_POSITION_PEAK_DETECTOR = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_PEAK_LIST = "stackPositionPeakList";
	public static final String DEF_STACK_POSITION_PEAK_LIST = PartSupport.PARTSTACK_LEFT_TOP;
	public static final String P_STACK_POSITION_SUBTRACT_SCAN_PART = "stackPositionSubtractScanPart";
	public static final String DEF_STACK_POSITION_SUBTRACT_SCAN_PART = PartSupport.PARTSTACK_RIGHT_TOP;
	public static final String P_STACK_POSITION_COMBINED_SCAN_PART = "stackPositionCombinedScanPart";
	public static final String DEF_STACK_POSITION_COMBINED_SCAN_PART = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_COMPARISON_SCAN_CHART = "stackPositionComparisonScanChart";
	public static final String DEF_STACK_POSITION_COMPARISON_SCAN_CHART = PartSupport.PARTSTACK_RIGHT_TOP;
	public static final String P_STACK_POSITION_QUANTITATION = "stackPositionQuantitation";
	public static final String DEF_STACK_POSITION_QUANTITATION = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_INTEGRATION_AREA = "stackPositionIntegrationArea";
	public static final String DEF_STACK_POSITION_INTEGRATION_AREA = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_INTERNAL_STANDARDS = "stackPositionInternalStandards";
	public static final String DEF_STACK_POSITION_INTERNAL_STANDARDS = PartSupport.PARTSTACK_BOTTOM_RIGHT;
	/*
	 * Overlay
	 */
	public static final String P_COLOR_SCHEME_OVERLAY_NORMAL = "colorSchemeOverlayNormal";
	public static final String DEF_COLOR_SCHEME_OVERLAY_NORMAL = Colors.COLOR_SCHEME_RED;
	public static final String P_COLOR_SCHEME_OVERLAY_SIC = "colorSchemeOverlaySIC";
	public static final String DEF_COLOR_SCHEME_OVERLAY_SIC = Colors.COLOR_SCHEME_HIGH_CONTRAST;
	//
	public static final String P_LINE_STYLE_OVERLAY_TIC = "lineStyleOverlayTIC";
	public static final String DEF_LINE_STYLE_OVERLAY_TIC = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_OVERLAY_BPC = "lineStyleOverlayBPC";
	public static final String DEF_LINE_STYLE_OVERLAY_BPC = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_OVERLAY_XIC = "lineStyleOverlayXIC";
	public static final String DEF_LINE_STYLE_OVERLAY_XIC = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_OVERLAY_SIC = "lineStyleOverlaySIC";
	public static final String DEF_LINE_STYLE_OVERLAY_SIC = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_OVERLAY_TSC = "lineStyleOverlayTSC";
	public static final String DEF_LINE_STYLE_OVERLAY_TSC = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_OVERLAY_DEFAULT = "lineStyleOverlayDefault";
	public static final String DEF_LINE_STYLE_OVERLAY_DEFAULT = LineStyle.SOLID.toString();
	//
	public static final String P_MINUTES_SHIFT_X = "minutesShiftX";
	public static final double DEF_MINUTES_SHIFT_X = 0.5d;
	public static final String P_ABSOLUTE_SHIFT_Y = "absoluteShiftY";
	public static final double DEF_ABSOLUTE_SHIFT_Y = 100000.0d;
	/*
	 * Scans
	 */
	public static final String P_SCAN_LABEL_FONT_NAME = "scanLabelFontName";
	public static final String DEF_SCAN_LABEL_FONT_NAME = "Ubuntu";
	public static final String P_SCAN_LABEL_FONT_SIZE = "scanLabelFontSize";
	public static final int MIN_SCAN_LABEL_FONT_SIZE = 1;
	public static final int MAX_SCAN_LABEL_FONT_SIZE = 72;
	public static final int DEF_SCAN_LABEL_FONT_SIZE = 11;
	public static final String P_SCAN_LABEL_FONT_STYLE = "scanLabelFontStyle";
	public static final int DEF_SCAN_LABEL_FONT_STYLE = SWT.NORMAL;
	public static final String P_COLOR_SCAN_1 = "colorScan1";
	public static final String DEF_COLOR_SCAN_1 = "255,0,0";
	public static final String P_COLOR_SCAN_2 = "colorScan2";
	public static final String DEF_COLOR_SCAN_2 = "0,0,0";
	public static final String P_SCAN_LABEL_HIGHEST_INTENSITIES = "scanLabelHighestIntensities";
	public static final int MIN_SCAN_LABEL_HIGHEST_INTENSITIES = 1;
	public static final int MAX_SCAN_LABEL_HIGHEST_INTENSITIES = 32;
	public static final int DEF_SCAN_LABEL_HIGHEST_INTENSITIES = 5;
	public static final String P_SCAN_LABEL_MODULO_INTENSITIES = "scanLabelModuloIntensities";
	public static final boolean DEF_SCAN_LABEL_MODULO_INTENSITIES = false;
	public static final String P_AUTOFOCUS_SUBTRACT_SCAN_PART = "autofocusSubtractScanPart";
	public static final boolean DEF_AUTOFOCUS_SUBTRACT_SCAN_PART = true;
	/*
	 * Peaks
	 */
	public static final String P_SHOW_PEAK_BACKGROUND = "showPeakBackground";
	public static final boolean DEF_SHOW_PEAK_BACKGROUND = true;
	public static final String P_COLOR_PEAK_BACKGROUND = "colorPeakBackground";
	public static final String DEF_COLOR_PEAK_BACKGROUND = "0,0,0";
	public static final String P_SHOW_PEAK_BASELINE = "showPeakBaseline";
	public static final boolean DEF_SHOW_PEAK_BASELINE = true;
	public static final String P_COLOR_PEAK_BASELINE = "colorPeakBaseline";
	public static final String DEF_COLOR_PEAK_BASELINE = "0,0,0";
	public static final String P_SHOW_PEAK = "showPeak";
	public static final boolean DEF_SHOW_PEAK = true;
	public static final String P_COLOR_PEAK_1 = "colorPeak1";
	public static final String DEF_COLOR_PEAK_1 = "255,0,0";
	public static final String P_COLOR_PEAK_2 = "colorPeak2";
	public static final String DEF_COLOR_PEAK_2 = "0,0,0";
	public static final String P_SHOW_PEAK_TANGENTS = "showPeakTangents";
	public static final boolean DEF_SHOW_PEAK_TANGENTS = true;
	public static final String P_COLOR_PEAK_TANGENTS = "colorPeakTangents";
	public static final String DEF_COLOR_PEAK_TANGENTS = "0,0,0";
	public static final String P_SHOW_PEAK_WIDTH_50 = "showPeakWidth50";
	public static final boolean DEF_SHOW_PEAK_WIDTH_50 = true;
	public static final String P_COLOR_PEAK_WIDTH_50 = "colorPeakWidth50";
	public static final String DEF_COLOR_PEAK_WIDTH_50 = "0,0,0";
	public static final String P_SHOW_PEAK_WIDTH_0 = "showPeakWidth0";
	public static final boolean DEF_SHOW_PEAK_WIDTH_0 = false;
	public static final String P_COLOR_PEAK_WIDTH_0 = "colorPeakWidth0";
	public static final String DEF_COLOR_PEAK_WIDTH_0 = "0,0,0";
	public static final String P_SHOW_PEAK_WIDTH_CONDAL_BOSH = "showPeakWidthCondalBosh";
	public static final boolean DEF_SHOW_PEAK_WIDTH_CONDAL_BOSH = false;
	public static final String P_COLOR_PEAK_WIDTH_CONDAL_BOSH = "colorPeakWidthCondalBosh";
	public static final String DEF_COLOR_PEAK_WIDTH_CONDAL_BOSH = "0,0,0";
	/*
	 * Targets
	 */
	public static final String P_USE_TARGET_LIST = "useTargetList";
	public static final boolean DEF_USE_TARGET_LIST = true;
	public static final String P_TARGET_LIST = "targetList";
	public static final String DEF_TARGET_LIST = "";
	public static final String P_PROPAGATE_TARGET_ON_UPDATE = "propagateTargetOnUpdate";
	public static final boolean DEF_PROPAGATE_TARGET_ON_UPDATE = false;
	/*
	 * Chromatogram
	 */
	public static final String P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS = "showRetentionIndexWithoutDecimals";
	public static final boolean DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS = true;
	public static final String P_SHOW_AREA_WITHOUT_DECIMALS = "showAreaWithoutDecimals";
	public static final boolean DEF_SHOW_AREA_WITHOUT_DECIMALS = true;
	public static final String P_SHOW_PEAKS_IN_SELECTED_RANGE = "showPeaksInSelectedRange";
	public static final boolean DEF_SHOW_PEAKS_IN_SELECTED_RANGE = true;
	public static final String P_CHROMATOGRAM_CHART_COMPRESSION_TYPE = "chartCompressionType";
	public static final String DEF_CHROMATOGRAM_CHART_COMPRESSION_TYPE = LineChart.COMPRESSION_MEDIUM;
}
