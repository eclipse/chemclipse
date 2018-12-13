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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

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
			{LineChart.COMPRESSION_LOW, LineChart.COMPRESSION_LOW}, {LineChart.COMPRESSION_AUTO, LineChart.COMPRESSION_AUTO}, //
			{LineChart.COMPRESSION_NONE, LineChart.COMPRESSION_NONE}//
	};
	//
	public static String[][] SYMBOL_TYPES = new String[][]{//
			{"None", PlotSymbolType.NONE.toString()}, //
			{"Circle", PlotSymbolType.CIRCLE.toString()}, //
			{"Cross", PlotSymbolType.CROSS.toString()}, //
			{"Diamond", PlotSymbolType.DIAMOND.toString()}, //
			{"Inverted Triangle", PlotSymbolType.INVERTED_TRIANGLE.toString()}, //
			{"Plus", PlotSymbolType.PLUS.toString()}, //
			{"Square", PlotSymbolType.SQUARE.toString()}, //
			{"Triangle", PlotSymbolType.TRIANGLE.toString()}//
	};
	//
	public static String[][] POSITIONS = new String[][]{//
			{"Primary", Position.Primary.toString()}, //
			{"Secondary", Position.Secondary.toString()}//
	};
	//
	public static String[][] LINE_STYLES = new String[][]{//
			{"None", LineStyle.NONE.toString()}, //
			{"-", LineStyle.DASH.toString()}, //
			{"-.", LineStyle.DASHDOT.toString()}, //
			{"-..", LineStyle.DASHDOTDOT.toString()}, //
			{".", LineStyle.DOT.toString()}, //
			{"Solid", LineStyle.SOLID.toString()}//
	};
	/*
	 * General / Task Quick Access
	 */
	public static final String P_STACK_POSITION_HEADER_DATA = "stackPositionMeasurementHeader";
	public static final String DEF_STACK_POSITION_MEASUREMENT_HEADER = PartSupport.PARTSTACK_BOTTOM_LEFT;
	public static final String P_STACK_POSITION_CHROMATOGRAM_OVERVIEW = "stackPositionChromatogramOverview";
	public static final String DEF_STACK_POSITION_CHROMATOGRAM_OVERVIEW = PartSupport.PARTSTACK_LEFT_CENTER;
	public static final String P_STACK_POSITION_MISCELLANEOUS_INFO = "stackPositionMiscellaneousInfo";
	public static final String DEF_STACK_POSITION_MISCELLANEOUS_INFO = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_CHROMATOGRAM_SCAN_INFO = "stackPositionChromatogramScanInfo";
	public static final String DEF_STACK_POSITION_CHROMATOGRAM_SCAN_INFO = PartSupport.PARTSTACK_NONE;
	public static final String P_STACK_POSITION_OVERLAY_CHROMATOGRAM_DEFAULT = "stackPositionOverlayChromatogramDefault";
	public static final String DEF_STACK_POSITION_OVERLAY_CHROMATOGRAM_DEFAULT = PartSupport.PARTSTACK_BOTTOM_LEFT;
	public static final String P_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA = "stackPositionOverlayChromatogramExtra";
	public static final String DEF_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA = PartSupport.PARTSTACK_BOTTOM_LEFT;
	public static final String P_STACK_POSITION_OVERLAY_NMR = "stackPositionOverlayNMR";
	public static final String DEF_STACK_POSITION_OVERLAY_NMR = PartSupport.PARTSTACK_NONE;
	public static final String P_STACK_POSITION_OVERLAY_XIR = "stackPositionOverlayXIR";
	public static final String DEF_STACK_POSITION_OVERLAY_XIR = PartSupport.PARTSTACK_NONE;
	public static final String P_STACK_POSITION_BASELINE = "stackPositionBaseline";
	public static final String DEF_STACK_POSITION_BASELINE = PartSupport.PARTSTACK_BOTTOM_CENTER;
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
	public static final String P_STACK_POSITION_PEAK_SCAN_LIST = "stackPositionPeakScanList";
	public static final String DEF_STACK_POSITION_PEAK_SCAN_LIST = PartSupport.PARTSTACK_LEFT_TOP;
	public static final String P_STACK_POSITION_PEAK_QUANTITATION_LIST = "stackPositionPeakQuantitationList";
	public static final String DEF_STACK_POSITION_PEAK_QUANTITATION_LIST = PartSupport.PARTSTACK_NONE;
	public static final String P_STACK_POSITION_SUBTRACT_SCAN_PART = "stackPositionSubtractScanPart";
	public static final String DEF_STACK_POSITION_SUBTRACT_SCAN_PART = PartSupport.PARTSTACK_RIGHT_TOP;
	public static final String P_STACK_POSITION_COMBINED_SCAN_PART = "stackPositionCombinedScanPart";
	public static final String DEF_STACK_POSITION_COMBINED_SCAN_PART = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_COMPARISON_SCAN_CHART = "stackPositionComparisonScanChart";
	public static final String DEF_STACK_POSITION_COMPARISON_SCAN_CHART = PartSupport.PARTSTACK_RIGHT_TOP;
	public static final String P_STACK_POSITION_MS_LIBRARY_STACK = "stackPositionMSLibraryStack";
	public static final String DEF_STACK_POSITION_MS_LIBRARY_STACK = PartSupport.PARTSTACK_NONE;
	public static final String P_STACK_POSITION_QUANTITATION = "stackPositionQuantitation";
	public static final String DEF_STACK_POSITION_QUANTITATION = PartSupport.PARTSTACK_RIGHT_TOP;
	public static final String P_STACK_POSITION_INTEGRATION_AREA = "stackPositionIntegrationArea";
	public static final String DEF_STACK_POSITION_INTEGRATION_AREA = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_INTERNAL_STANDARDS = "stackPositionInternalStandards";
	public static final String DEF_STACK_POSITION_INTERNAL_STANDARDS = PartSupport.PARTSTACK_BOTTOM_RIGHT;
	public static final String P_STACK_POSITION_MEASUREMENT_RESULTS = "stackPositionMeasurementResults";
	public static final String DEF_STACK_POSITION_MEASUREMENT_RESULTS = PartSupport.PARTSTACK_BOTTOM_RIGHT;
	public static final String P_STACK_POSITION_CHROMATOGRAM_HEATMAP = "stackPositionChromatogramHeatmap";
	public static final String DEF_STACK_POSITION_CHROMATOGRAM_HEATMAP = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_PEAK_QUANTITATION_REFERENCES = "stackPositionPeakQuantitationReferences";
	public static final String DEF_STACK_POSITION_PEAK_QUANTITATION_REFERENCES = PartSupport.PARTSTACK_BOTTOM_RIGHT;
	public static final String P_STACK_POSITION_PLATE_CHARTS = "stackPositionPlateCharts";
	public static final String DEF_STACK_POSITION_PLATE_CHARTS = PartSupport.PARTSTACK_BOTTOM_LEFT;
	public static final String P_STACK_POSITION_WELL_DATA = "stackPositionWellData";
	public static final String DEF_STACK_POSITION_WELL_DATA = PartSupport.PARTSTACK_BOTTOM_RIGHT;
	public static final String P_STACK_POSITION_WELL_CHART = "stackPositionWellChart";
	public static final String DEF_STACK_POSITION_WELL_CHART = PartSupport.PARTSTACK_BOTTOM_CENTER;
	public static final String P_STACK_POSITION_PLATE_DATA = "stackPositionPlateData";
	public static final String DEF_STACK_POSITION_PLATE_DATA = PartSupport.PARTSTACK_RIGHT_TOP;
	/*
	 * Overlay
	 */
	public static final String P_OVERLAY_CHART_COMPRESSION_TYPE = "overlayChartCompressionType";
	public static final String DEF_OVERLAY_CHART_COMPRESSION_TYPE = LineChart.COMPRESSION_MEDIUM;
	public static final String P_SHOW_REFERENCED_CHROMATOGRAMS = "showReferencedChromatograms";
	public static final boolean DEF_SHOW_REFERENCED_CHROMATOGRAMS = true;
	public static final String P_COLOR_SCHEME_DISPLAY_NORMAL_OVERLAY = "colorSchemeDisplayNormalOverlay";
	public static final String DEF_COLOR_SCHEME_DISPLAY_NORMAL_OVERLAY = Colors.COLOR_SCHEME_RED;
	public static final String P_COLOR_SCHEME_DISPLAY_SIC_OVERLAY = "colorSchemeDisplaySICOverlay";
	public static final String DEF_COLOR_SCHEME_DISPLAY_SIC_OVERLAY = Colors.COLOR_SCHEME_HIGH_CONTRAST;
	public static final String P_COLOR_SCHEME_DISPLAY_SWC_OVERLAY = "colorSchemeDisplaySWCOverlay";
	public static final String DEF_COLOR_SCHEME_DISPLAY_SWC_OVERLAY = Colors.COLOR_SCHEME_HIGH_CONTRAST;
	public static final String P_LINE_STYLE_DISPLAY_TIC_OVERLAY = "lineStyleDisplayTICOverlay";
	public static final String DEF_LINE_STYLE_DISPLAY_TIC_OVERLAY = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_DISPLAY_BPC_OVERLAY = "lineStyleDisplayBPCOverlay";
	public static final String DEF_LINE_STYLE_DISPLAY_BPC_OVERLAY = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_DISPLAY_XIC_OVERLAY = "lineStyleDisplayXICOverlay";
	public static final String DEF_LINE_STYLE_DISPLAY_XIC_OVERLAY = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_DISPLAY_SIC_OVERLAY = "lineStyleDisplaySICOverlay";
	public static final String DEF_LINE_STYLE_DISPLAY_SIC_OVERLAY = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_DISPLAY_TSC_OVERLAY = "lineStyleDisplayTSCOverlay";
	public static final String DEF_LINE_STYLE_DISPLAY_TSC_OVERLAY = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_DISPLAY_XWC_OVERLAY = "lineStyleDisplayXWCOverlay";
	public static final String DEF_LINE_STYLE_DISPLAY_XWC_OVERLAY = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_DISPLAY_SWC_OVERLAY = "lineStyleDisplaySWCOverlay";
	public static final String DEF_LINE_STYLE_DISPLAY_SWC_OVERLAY = LineStyle.SOLID.toString();
	public static final String P_LINE_STYLE_DISPLAY_DEFAULT_OVERLAY = "lineStyleDisplayDefaultOverlay";
	public static final String DEF_LINE_STYLE_DISPLAY_DEFAULT_OVERLAY = LineStyle.SOLID.toString();
	//
	public static final String P_CHROMATOGRAM_OVERLAY_IONS_SELECTION = "chromatogramOverlayIonsSelection";
	public static final String DEF_CHROMATOGRAM_OVERLAY_IONS_SELECTION = OverlayChartSupport.SELECTED_IONS_USERS_CHOICE;
	public static final String P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE = "chromatogramOverlayIonsUsersChoice";
	public static final String DEF_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE = "18 28 32 84 207";
	public static final String P_CHROMATOGRAM_OVERLAY_WAVELENGTHS_USERS_CHOICE = "chromatogramOverlayWavelengthsUsersChoice";
	public static final String DEF_CHROMATOGRAM_OVERLAY_WAVELENGTHS_USERS_CHOICE = "237";
	public static final String P_CHROMATOGRAM_OVERLAY_IONS_HYDROCARBONS = "chromatogramOverlayIonsHydrocarbons";
	public static final String DEF_CHROMATOGRAM_OVERLAY_IONS_HYDROCARBONS = "57 71 85";
	public static final String P_CHROMATOGRAM_OVERLAY_IONS_FATTY_ACIDS = "chromatogramOverlayIonsFattyAcids";
	public static final String DEF_CHROMATOGRAM_OVERLAY_IONS_FATTY_ACIDS = "74 84";
	public static final String P_CHROMATOGRAM_OVERLAY_IONS_FAME = "chromatogramOverlayIonsFAME";
	public static final String DEF_CHROMATOGRAM_OVERLAY_IONS_FAME = "79 81";
	public static final String P_CHROMATOGRAM_OVERLAY_IONS_SOLVENT_TAILING = "chromatogramOverlayIonsSolventTailing";
	public static final String DEF_CHROMATOGRAM_OVERLAY_IONS_SOLVENT_TAILING = "84";
	public static final String P_CHROMATOGRAM_OVERLAY_IONS_COLUMN_BLEED = "chromatogramOverlayIonsColumnBleed";
	public static final String DEF_CHROMATOGRAM_OVERLAY_IONS_COLUMN_BLEED = "207";
	//
	public static final String P_OVERLAY_SHIFT_X = "overlayShiftX";
	public static final double DEF_OVERLAY_SHIFT_X = 0.0d;
	public static final double MIN_OVERLAY_SHIFT_X = 0.0d;
	public static final double MAX_OVERLAY_SHIFT_X = Double.MAX_VALUE;
	public static final String P_INDEX_SHIFT_X = "indexShiftX";
	public static final int DEF_INDEX_SHIFT_X = 0;
	public static final int MIN_INDEX_SHIFT_X = 0;
	public static final int MAX_INDEX_SHIFT_X = 100;
	public static final String P_OVERLAY_SHIFT_Y = "overlayShiftY";
	public static final double DEF_OVERLAY_SHIFT_Y = 0.0d;
	public static final double MIN_OVERLAY_SHIFT_Y = 0.0d;
	public static final double MAX_OVERLAY_SHIFT_Y = Double.MAX_VALUE;
	public static final String P_INDEX_SHIFT_Y = "indexShiftY";
	public static final int DEF_INDEX_SHIFT_Y = 0;
	public static final int MIN_INDEX_SHIFT_Y = 0;
	public static final int MAX_INDEX_SHIFT_Y = 100;
	//
	public static final String P_OVERLAY_SHOW_AREA = "overlayShowArea";
	public static final boolean DEF_OVERLAY_SHOW_AREA = false;
	public static final String P_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS = "overlayAutofocusProfileSettings";
	public static final boolean DEF_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS = true;
	public static final String P_OVERLAY_AUTOFOCUS_SHIFT_SETTINGS = "overlayAutofocusShiftSettings";
	public static final boolean DEF_OVERLAY_AUTOFOCUS_SHIFT_SETTINGS = true;
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
	public static final String P_COLOR_PEAK_DETECTOR_CHROMATOGRAM = "colorPeakDetectorChromatogram";
	public static final String DEF_COLOR_PEAK_DETECTOR_CHROMATOGRAM = "255,0,0";
	public static final String P_SHOW_PEAK_DETECTOR_CHROMATOGRAM_AREA = "showPeakDetectorChromatogramArea";
	public static final boolean DEF_SHOW_PEAK_DETECTOR_CHROMATOGRAM_AREA = false;
	public static final String P_PEAK_DETECTOR_SCAN_MARKER_SIZE = "showPeakDetectorScanMarkerSize";
	public static final int DEF_PEAK_DETECTOR_SCAN_MARKER_SIZE = 2;
	public static final String P_PEAK_DETECTOR_SCAN_MARKER_COLOR = "showPeakDetectorScanMarkerColor";
	public static final String DEF_PEAK_DETECTOR_SCAN_MARKER_COLOR = "255,0,0";
	public static final String P_PEAK_DETECTOR_SCAN_MARKER_TYPE = "showPeakDetectorScanMarkerType";
	public static final String DEF_PEAK_DETECTOR_SCAN_MARKER_TYPE = PlotSymbolType.NONE.toString();
	/*
	 * Targets
	 */
	public static final String P_USE_TARGET_LIST = "useTargetList";
	public static final boolean DEF_USE_TARGET_LIST = true;
	public static final String P_TARGET_LIST = "targetList";
	public static final String DEF_TARGET_LIST = "";
	public static final String P_PROPAGATE_TARGET_ON_UPDATE = "propagateTargetOnUpdate";
	public static final boolean DEF_PROPAGATE_TARGET_ON_UPDATE = false;
	public static final String P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER = "targetTemplateLibraryImportFolder";
	public static final String DEF_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER = "";
	/*
	 * Chromatogram
	 */
	public static final String P_LEGACY_UPDATE_CHROMATOGRAM_MODUS = "legacyUpdateChromatogramModus";
	public static final boolean DEF_LEGACY_UPDATE_CHROMATOGRAM_MODUS = true;
	public static final String P_LEGACY_UPDATE_PEAK_MODUS = "legacyUpdatePeakModus";
	public static final boolean DEF_LEGACY_UPDATE_PEAK_MODUS = true;
	//
	public static final String P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS = "showRetentionIndexWithoutDecimals";
	public static final boolean DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS = true;
	public static final String P_SHOW_AREA_WITHOUT_DECIMALS = "showAreaWithoutDecimals";
	public static final boolean DEF_SHOW_AREA_WITHOUT_DECIMALS = true;
	public static final String P_CHROMATOGRAM_CHART_COMPRESSION_TYPE = "chromatogramChartCompressionType";
	public static final String DEF_CHROMATOGRAM_CHART_COMPRESSION_TYPE = LineChart.COMPRESSION_MEDIUM;
	public static final String P_COLOR_CHROMATOGRAM = "colorChromatogram";
	public static final String DEF_COLOR_CHROMATOGRAM = "255,0,0";
	public static final String P_ENABLE_CHROMATOGRAM_AREA = "enableChromatogramArea";
	public static final boolean DEF_ENABLE_CHROMATOGRAM_AREA = true;
	public static final String P_COLOR_CHROMATOGRAM_SELECTED_PEAK = "colorChromatogramSelectedPeak";
	public static final String DEF_COLOR_CHROMATOGRAM_SELECTED_PEAK = "128,0,0";
	public static final String P_CHROMATOGRAM_SELECTED_PEAK_MARKER_SIZE = "showChromatogramSelectedPeakMarkerSize";
	public static final int DEF_CHROMATOGRAM_SELECTED_PEAK_MARKER_SIZE = 2;
	public static final String P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE = "showChromatogramSelectedPeakMarkerType";
	public static final String DEF_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE = PlotSymbolType.CIRCLE.toString();
	public static final String P_COLOR_CHROMATOGRAM_SELECTED_SCAN = "colorChromatogramSelectedScan";
	public static final String DEF_COLOR_CHROMATOGRAM_SELECTED_SCAN = "128,0,0";
	public static final String P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE = "showChromatogramSelectedScanMarkerSize";
	public static final int DEF_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE = 5;
	public static final String P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE = "showChromatogramSelectedScanMarkerType";
	public static final String DEF_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE = PlotSymbolType.CROSS.toString();
	public static final String P_SHOW_CHROMATOGRAM_PEAK_LABELS = "showChromatogramPeakLabels";
	public static final boolean DEF_SHOW_CHROMATOGRAM_PEAK_LABELS = true;
	public static final String P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME = "chromatogramPeakLabelFontName";
	public static final String DEF_CHROMATOGRAM_PEAK_LABEL_FONT_NAME = "Ubuntu";
	public static final String P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE = "chromatogramPeakLabelFontSize";
	public static final int MIN_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE = 1;
	public static final int MAX_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE = 72;
	public static final int DEF_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE = 11;
	public static final String P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE = "chromatogramPeakLabelFontStyle";
	public static final int DEF_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE = SWT.NORMAL;
	public static final String P_SHOW_CHROMATOGRAM_BASELINE = "showChromatogramBaseline";
	public static final boolean DEF_SHOW_CHROMATOGRAM_BASELINE = true;
	public static final String P_COLOR_CHROMATOGRAM_BASELINE = "colorChromatogramBaseline";
	public static final String DEF_COLOR_CHROMATOGRAM_BASELINE = "0,0,0";
	public static final String P_ENABLE_BASELINE_AREA = "enableBaselineArea";
	public static final boolean DEF_ENABLE_BASELINE_AREA = true;
	public static final String P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE = "chromatogramPeakLabelSymbolSize";
	public static final int MIN_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE = 1;
	public static final int MAX_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE = 72;
	public static final int DEF_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE = 5;
	public static final String P_SHOW_CHROMATOGRAM_SCAN_LABELS = "showChromatogramScanLabels";
	public static final boolean DEF_SHOW_CHROMATOGRAM_SCAN_LABELS = true;
	public static final String P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE = "chromatogramScanLabelSymbolSize";
	public static final int MIN_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE = 1;
	public static final int MAX_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE = 72;
	public static final int DEF_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE = 5;
	public static final String P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME = "chromatogramScanLabelFontName";
	public static final String DEF_CHROMATOGRAM_SCAN_LABEL_FONT_NAME = "Ubuntu";
	public static final String P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE = "chromatogramScanLabelFontSize";
	public static final int MIN_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE = 1;
	public static final int MAX_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE = 72;
	public static final int DEF_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE = 11;
	public static final String P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE = "chromatogramScanLabelFontStyle";
	public static final int DEF_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE = SWT.NORMAL;
	public static final String P_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED = "colorChromatogramSelectedScanIdentified";
	public static final String DEF_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED = "128,0,0";
	public static final String P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION = "moveRetentionTimeOnPeakSelection";
	public static final boolean DEF_MOVE_RETENTION_TIME_ON_PEAK_SELECTION = true;
	public static final String P_ALTERNATE_WINDOW_MOVE_DIRECTION = "useAlternateWindowMoveDirection";
	public static final boolean DEF_ALTERNATE_WINDOW_MOVE_DIRECTION = false;
	public static final String P_CONDENSE_CYCLE_NUMBER_SCANS = "condenseCycleNumberScans";
	public static final boolean DEF_CONDENSE_CYCLE_NUMBER_SCANS = true;
	public static final String P_SET_CHROMATOGRAM_INTENSITY_RANGE = "setChromatogramIntensityRange";
	public static final boolean DEF_SET_CHROMATOGRAM_INTENSITY_RANGE = false;
	public static final String P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME = "chromatogramTransferDeltaRetentionTime";
	public static final double MIN_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME = 0; // Minutes
	public static final double MAX_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME = Double.MAX_VALUE; // Minutes
	public static final double DEF_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME = 0.5d; // Minutes
	public static final String P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY = "chromatogramTransferBestTargetOnly";
	public static final boolean DEF_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY = true;
	public static final String P_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY = "stretchChromatogramMillisecondsScanDelay";
	public static final int MIN_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY = 0;
	public static final int MAX_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY = 6000000; // = 100.0 minutes
	public static final int DEF_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY = 0;
	public static final String P_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH = "stretchChromatogramMillisecondsLength";
	public static final int MIN_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH = 10;
	public static final int MAX_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH = 36000000; // = 600.0 minutes
	public static final int DEF_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH = 6000000; // = 100.0 minutes
	public static final String P_CHROMATOGRAM_EXTEND_X = "chromatogramExtendX";
	public static final double MIN_CHROMATOGRAM_EXTEND_X = 0.0d;
	public static final double MAX_CHROMATOGRAM_EXTEND_X = 10.0d;
	public static final double DEF_CHROMATOGRAM_EXTEND_X = 0.5d; // 50%
	public static final String P_CHROMATOGRAM_X_ZOOM_ONLY = "chromatogramXZoomOnly";
	public static final boolean DEF_CHROMATOGRAM_X_ZOOM_ONLY = false;
	public static final String P_CHROMATOGRAM_Y_ZOOM_ONLY = "chromatogramYZoomOnly";
	public static final boolean DEF_CHROMATOGRAM_Y_ZOOM_ONLY = false;
	//
	public static final String P_SHOW_X_AXIS_MILLISECONDS = "showXAxisMilliseconds";
	public static final boolean DEF_SHOW_X_AXIS_MILLISECONDS = false;
	public static final String P_POSITION_X_AXIS_MILLISECONDS = "positionXAxisMilliseconds";
	public static final String DEF_POSITION_X_AXIS_MILLISECONDS = Position.Secondary.toString();
	public static final String P_COLOR_X_AXIS_MILLISECONDS = "colorXAxisMilliseconds";
	public static final String DEF_COLOR_X_AXIS_MILLISECONDS = "0,0,0";
	public static final String P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS = "gridlineStyleXAxisMilliseconds";
	public static final String DEF_GRIDLINE_STYLE_X_AXIS_MILLISECONDS = LineStyle.NONE.toString();
	public static final String P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS = "gridlineColorXAxisMilliseconds";
	public static final String DEF_GRIDLINE_COLOR_X_AXIS_MILLISECONDS = "192,192,192";
	//
	public static final String P_SHOW_X_AXIS_SECONDS = "showXAxisSeconds";
	public static final boolean DEF_SHOW_X_AXIS_SECONDS = false;
	public static final String P_POSITION_X_AXIS_SECONDS = "positionXAxisSeconds";
	public static final String DEF_POSITION_X_AXIS_SECONDS = Position.Primary.toString();
	public static final String P_COLOR_X_AXIS_SECONDS = "colorXAxisSeconds";
	public static final String DEF_COLOR_X_AXIS_SECONDS = "0,0,0";
	public static final String P_GRIDLINE_STYLE_X_AXIS_SECONDS = "gridlineStyleXAxisSeconds";
	public static final String DEF_GRIDLINE_STYLE_X_AXIS_SECONDS = LineStyle.NONE.toString();
	public static final String P_GRIDLINE_COLOR_X_AXIS_SECONDS = "gridlineColorXAxisSeconds";
	public static final String DEF_GRIDLINE_COLOR_X_AXIS_SECONDS = "192,192,192";
	//
	public static final String P_SHOW_X_AXIS_MINUTES = "showXAxisMinutes";
	public static final boolean DEF_SHOW_X_AXIS_MINUTES = true;
	public static final String P_POSITION_X_AXIS_MINUTES = "positionXAxisMinutes";
	public static final String DEF_POSITION_X_AXIS_MINUTES = Position.Primary.toString();
	public static final String P_COLOR_X_AXIS_MINUTES = "colorXAxisMinutes";
	public static final String DEF_COLOR_X_AXIS_MINUTES = "0,0,0";
	public static final String P_GRIDLINE_STYLE_X_AXIS_MINUTES = "gridlineStyleXAxisMinutes";
	public static final String DEF_GRIDLINE_STYLE_X_AXIS_MINUTES = LineStyle.DOT.toString();
	public static final String P_GRIDLINE_COLOR_X_AXIS_MINUTES = "gridlineColorXAxisMinutes";
	public static final String DEF_GRIDLINE_COLOR_X_AXIS_MINUTES = "192,192,192";
	//
	public static final String P_SHOW_X_AXIS_SCANS = "showXAxisScans";
	public static final boolean DEF_SHOW_X_AXIS_SCANS = false;
	public static final String P_POSITION_X_AXIS_SCANS = "positionXAxisScans";
	public static final String DEF_POSITION_X_AXIS_SCANS = Position.Primary.toString();
	public static final String P_COLOR_X_AXIS_SCANS = "colorXAxisScans";
	public static final String DEF_COLOR_X_AXIS_SCANS = "0,0,0";
	public static final String P_GRIDLINE_STYLE_X_AXIS_SCANS = "gridlineStyleXAxisScans";
	public static final String DEF_GRIDLINE_STYLE_X_AXIS_SCANS = LineStyle.NONE.toString();
	public static final String P_GRIDLINE_COLOR_X_AXIS_SCANS = "gridlineColorXAxisScans";
	public static final String DEF_GRIDLINE_COLOR_X_AXIS_SCANS = "192,192,192";
	//
	public static final String P_SHOW_Y_AXIS_INTENSITY = "showYAxisIntensity";
	public static final boolean DEF_SHOW_Y_AXIS_INTENSITY = true;
	public static final String P_POSITION_Y_AXIS_INTENSITY = "positionYAxisIntensity";
	public static final String DEF_POSITION_Y_AXIS_INTENSITY = Position.Primary.toString();
	public static final String P_COLOR_Y_AXIS_INTENSITY = "colorYAxisIntensity";
	public static final String DEF_COLOR_Y_AXIS_INTENSITY = "0,0,0";
	public static final String P_GRIDLINE_STYLE_Y_AXIS_INTENSITY = "gridlineStyleYAxisIntensity";
	public static final String DEF_GRIDLINE_STYLE_Y_AXIS_INTENSITY = LineStyle.NONE.toString();
	public static final String P_GRIDLINE_COLOR_Y_AXIS_INTENSITY = "gridlineColorYAxisIntensity";
	public static final String DEF_GRIDLINE_COLOR_Y_AXIS_INTENSITY = "192,192,192";
	//
	public static final String P_SHOW_Y_AXIS_RELATIVE_INTENSITY = "showYAxisRelativeIntensity";
	public static final boolean DEF_SHOW_Y_AXIS_RELATIVE_INTENSITY = true;
	public static final String P_POSITION_Y_AXIS_RELATIVE_INTENSITY = "positionYAxisRelativeIntensity";
	public static final String DEF_POSITION_Y_AXIS_RELATIVE_INTENSITY = Position.Secondary.toString();
	public static final String P_COLOR_Y_AXIS_RELATIVE_INTENSITY = "colorYAxisRelativeIntensity";
	public static final String DEF_COLOR_Y_AXIS_RELATIVE_INTENSITY = "0,0,0";
	public static final String P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY = "gridlineStyleYAxisRelativeIntensity";
	public static final String DEF_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY = LineStyle.DOT.toString();
	public static final String P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY = "gridlineColorYAxisRelativeIntensity";
	public static final String DEF_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY = "192,192,192";
	//
	public static final String P_CHROMATOGRAM_SELECTED_ACTION_ID = "chromatogramSelectedActionId";
	public static final String DEF_CHROMATOGRAM_SELECTED_ACTION_ID = "";
	public static final String P_CHROMATOGRAM_SAVE_AS_FOLDER = "chromatogramSaveAsFolder";
	public static final String DEF_CHROMATOGRAM_SAVE_AS_FOLDER = "";
	public static final String P_CHROMATOGRAM_LOAD_PROCESS_METHOD = "chromatogramLoadProcessMethod";
	public static final String DEF_CHROMATOGRAM_LOAD_PROCESS_METHOD = "";
	/*
	 * File Explorer
	 */
	public static final String P_SHOW_DATA_MSD = "showDataMSD";
	public static final boolean DEF_SHOW_DATA_MSD = true;
	public static final String P_SHOW_DATA_CSD = "showDataCSD";
	public static final boolean DEF_SHOW_DATA_CSD = true;
	public static final String P_SHOW_DATA_WSD = "showDataWSD";
	public static final boolean DEF_SHOW_DATA_WSD = true;
	public static final String P_SHOW_LIBRARY_MSD = "showLibraryMSD";
	public static final boolean DEF_SHOW_LIBRARY_MSD = true;
	public static final String P_SHOW_SCANS_MSD = "showScansMSD";
	public static final boolean DEF_SHOW_SCANS_MSD = true;
	public static final String P_SHOW_DATA_XIR = "showDataXIR";
	public static final boolean DEF_SHOW_DATA_XIR = true;
	public static final String P_SHOW_DATA_NMR = "showDataNMR";
	public static final boolean DEF_SHOW_DATA_NMR = true;
	public static final String P_SHOW_DATA_CAL = "showDataCAL";
	public static final boolean DEF_SHOW_DATA_CAL = true;
	public static final String P_SHOW_DATA_PCR = "showDataPCR";
	public static final boolean DEF_SHOW_DATA_PCR = true;
	public static final String P_SHOW_DATA_SEQUENCE = "showDataSequence";
	public static final boolean DEF_SHOW_DATA_SEQUENCE = true;
	public static final String P_SHOW_DATA_METHOD = "showDataMethod";
	public static final boolean DEF_SHOW_DATA_METHOD = true;
	/*
	 * Lists
	 */
	public static final String P_SHOW_PEAKS_IN_LIST = "showPeaksInList";
	public static final boolean DEF_SHOW_PEAKS_IN_LIST = true;
	public static final String P_SHOW_PEAKS_IN_SELECTED_RANGE = "showPeaksInSelectedRange";
	public static final boolean DEF_SHOW_PEAKS_IN_SELECTED_RANGE = true;
	public static final String P_SHOW_SCANS_IN_LIST = "showScansInList";
	public static final boolean DEF_SHOW_SCANS_IN_LIST = true;
	public static final String P_SHOW_SCANS_IN_SELECTED_RANGE = "showScansInSelectedRange";
	public static final boolean DEF_SHOW_SCANS_IN_SELECTED_RANGE = true;
	public static final String P_COLUMN_ORDER_PEAK_SCAN_LIST = "columnOrderPeakScanList";
	public static final String DEF_COLUMN_ORDER_PEAK_SCAN_LIST = "";
	public static final String P_COLUMN_ORDER_TARGET_LIST = "columnOrderTargetList";
	public static final String DEF_COLUMN_ORDER_TARGET_LIST = "";
	/*
	 * Baseline
	 */
	public static final String P_BASELINE_CHART_COMPRESSION_TYPE = "baselineChartCompressionType";
	public static final String DEF_BASELINE_CHART_COMPRESSION_TYPE = LineChart.COMPRESSION_MEDIUM;
	public static final String P_COLOR_SCHEME_DISPLAY_BASELINE = "colorSchemeDisplayBaseline";
	public static final String DEF_COLOR_SCHEME_DISPLAY_BASELINE = Colors.COLOR_SCHEME_RED;
	/*
	 * Sequences
	 */
	public static final String P_SEQUENCE_EXPLORER_USE_SUBFOLDER = "sequenceExplorerUseSubfolder";
	public static final boolean DEF_SEQUENCE_EXPLORER_USE_SUBFOLDER = true;
	public static final String P_SEQUENCE_EXPLORER_SORT_DATA = "sequenceExplorerSortData";
	public static final boolean DEF_SEQUENCE_EXPLORER_SORT_DATA = false;
	public static final String P_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER = "sequenceExplorerPathRootFolder";
	public static final String DEF_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER = "";
	public static final String P_SEQUENCE_EXPLORER_PATH_PARENT_FOLDER = "sequenceExplorerPathParentFolder";
	public static final String DEF_SEQUENCE_EXPLORER_PATH_PARENT_FOLDER = "";
	public static final String P_SEQUENCE_EXPLORER_PATH_SUB_FOLDER = "sequenceExplorerPathSubFolder";
	public static final String DEF_SEQUENCE_EXPLORER_PATH_SUB_FOLDER = "";
	public static final String P_SEQUENCE_EXPLORER_PATH_DIALOG_FOLDER = "sequenceExplorerPathDialogFolder";
	public static final String DEF_SEQUENCE_EXPLORER_PATH_DIALOG_FOLDER = "";
	/*
	 * Methods
	 */
	public static final String P_METHOD_EXPLORER_PATH_ROOT_FOLDER = "methodExplorerPathRootFolder";
	public static final String DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER = "";
	public static final String P_SELECTED_METHOD_NAME = "selectedMethodName";
	public static final String DEF_SELECTED_METHOD_NAME = "";
	public static final String P_METHOD_PROCESSOR_SELECTION_CSD = "methodProcessorSelectionCSD";
	public static final boolean DEF_METHOD_PROCESSOR_SELECTION_CSD = true;
	public static final String P_METHOD_PROCESSOR_SELECTION_MSD = "methodProcessorSelectionMSD";
	public static final boolean DEF_METHOD_PROCESSOR_SELECTION_MSD = true;
	public static final String P_METHOD_PROCESSOR_SELECTION_WSD = "methodProcessorSelectionWSD";
	public static final boolean DEF_METHOD_PROCESSOR_SELECTION_WSD = true;
	/*
	 * Quanititation
	 */
	public static final String P_USE_QUANTITATION_REFERENCE_LIST = "useQuantitationReferenceList";
	public static final boolean DEF_USE_QUANTITATION_REFERENCE_LIST = true;
	public static final String P_QUANTITATION_REFERENCE_LIST = "quantitationReferenceList";
	public static final String DEF_QUANTITATION_REFERENCE_LIST = "";
	/*
	 * PCR
	 */
	public static final String P_PCR_DEFAULT_COLOR = "pcrDefaultColor";
	public static final String DEF_PCR_DEFAULT_COLOR = "192,192,192";
	public static final String P_PCR_COLOR_CODES = "pcrColorCodes";
	public static final String DEF_PCR_COLOR_CODES = "";
}
