/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageChromatogram extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageChromatogram() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Chromatogram");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceConstants.P_LEGACY_UPDATE_CHROMATOGRAM_MODUS, "Legacy Chromatogram Update Modus", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_LEGACY_UPDATE_CHROMATOGRAM_MODUS, "Legacy Peak Update Modus", getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, "Display RI without decimals", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS, "Display Area without decimals", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE, "Compression Type:", PreferenceConstants.COMPRESSION_TYPES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM, "Color Chromatogram:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA, "Enable Chromatogram Area", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_BASELINE, "Show Chromatogram Baseline", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_BASELINE, "Color Chromatogram Baseline:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ENABLE_BASELINE_AREA, "Enable Baseline Area", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION, "Use alternate window move direction", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS, "Condense cycle number scans", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SET_CHROMATOGRAM_INTENSITY_RANGE, "Set chromatogram intensity range", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, "Transfer delta retention time (Minutes)", PreferenceConstants.MIN_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, PreferenceConstants.MAX_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY, "Transfer best target only", getFieldEditorParent()));
		addIntegerField(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY, "Stretch Chromatogram Scan Delay (Milliseconds):", PreferenceConstants.MIN_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY, PreferenceConstants.MAX_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY);
		addIntegerField(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH, "Stretch Chromatogram Length (Milliseconds):", PreferenceConstants.MIN_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH, PreferenceConstants.MAX_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH);
		addField(new DoubleFieldEditor(PreferenceConstants.P_CHROMATOGRAM_EXTEND_X, "Extend X (1.0 = 100%)", PreferenceConstants.MIN_CHROMATOGRAM_EXTEND_X, PreferenceConstants.MAX_CHROMATOGRAM_EXTEND_X, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_CHROMATOGRAM_X_ZOOM_ONLY, "X Zoom Only", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_CHROMATOGRAM_Y_ZOOM_ONLY, "Y Zoom Only", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS, "Show X Axis (Milliseconds)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS, "Position X Axis (Milliseconds):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS, "Color X Axis (Milliseconds):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS, "GridLine Style X Axis (Milliseconds):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS, "GridLine Color X Axis (Milliseconds):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_SECONDS, "Show X Axis (Seconds)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_SECONDS, "Position X Axis (Seconds):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_SECONDS, "Color X Axis (Seconds):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS, "GridLine Style X Axis (Seconds):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS, "GridLine Color X Axis (Seconds):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_MINUTES, "Show X Axis (Minutes)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_MINUTES, "Position X Axis (Minutes):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_MINUTES, "Color X Axis (Minutes):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES, "GridLine Style X Axis (Minutes):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES, "GridLine Color X Axis (Minutes):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_SCANS, "Show X Axis (Scans)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_SCANS, "Position X Axis (Scans):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_SCANS, "Color X Axis (Scans):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS, "GridLine Style X Axis (Scans):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS, "GridLine Color X Axis (Scans):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY, "Show Y Axis (Intensity)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY, "Position Y Axis (Intensity):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY, "Color Y Axis (Intensity):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY, "GridLine Style Y Axis (Intensity):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY, "GridLine Color Y Axis (Intensity):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY, "Show Y Axis (Intensity %)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY, "Position Y Axis (Intensity %):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY, "Color Y Axis (Intensity %):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY, "GridLine Style Y Axis (Intensity %):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY, "GridLine Color Y Axis (Intensity %):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_ACTION_ID, "Chromatogram Selected Action Id:", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SAVE_AS_FOLDER, "Save As... Folder", getFieldEditorParent()));
	}

	private void addIntegerField(String name, String labelText, int min, int max) {

		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(name, labelText, getFieldEditorParent());
		integerFieldEditor.setValidRange(min, max);
		addField(integerFieldEditor);
	}

	public void init(IWorkbench workbench) {

	}
}
