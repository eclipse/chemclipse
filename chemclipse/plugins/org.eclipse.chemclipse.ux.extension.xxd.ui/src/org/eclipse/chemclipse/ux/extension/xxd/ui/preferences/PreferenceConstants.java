/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.swt.SWT;
import org.swtchart.LineStyle;

public class PreferenceConstants {

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
	//
	public static String[][] PART_STACKS = new String[][]{//
			{"--", PartSupport.PARTSTACK_NONE}, //
			{"Left", PartSupport.PARTSTACK_BOTTOM_LEFT}, //
			{"Center", PartSupport.PARTSTACK_BOTTOM_CENTER}, //
			{"Right", PartSupport.PARTSTACK_BOTTOM_RIGHT}//
	};
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
	public static final String P_SCAN_LABEL_HIGHEST_INTENSITIES = "scanLabelHighestIntensities";
	public static final int MIN_SCAN_LABEL_HIGHEST_INTENSITIES = 1;
	public static final int MAX_SCAN_LABEL_HIGHEST_INTENSITIES = 32;
	public static final int DEF_SCAN_LABEL_HIGHEST_INTENSITIES = 5;
	//
	public static String[][] FONT_STYLES = new String[][]{//
			{"Normal", Integer.toString(SWT.NORMAL)}, //
			{"Bold", Integer.toString(SWT.BOLD)}, //
			{"Italic", Integer.toString(SWT.ITALIC)}//
	};
	/*
	 * Peaks
	 */
	/*
	 * Targets
	 */
	public static final String P_USE_TARGET_LIST = "useTargetList";
	public static final boolean DEF_USE_TARGET_LIST = true;
	public static final String P_TARGET_LIST = "targetList";
	public static final String DEF_TARGET_LIST = "";
}
