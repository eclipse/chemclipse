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

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePagePeaksAxes extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePagePeaksAxes() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Peak(s) Axes");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_PEAKS, "Display Color Scheme", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_AREA_DISPLAY_PEAKS, "Show Area", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Retention Time", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_RETENTION_TIME_PEAKS, "Show X Axis (Retention Time)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_RETENTION_TIME_PEAKS, "Position X Axis (Retention Time):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_RETENTION_TIME_PEAKS, "Color X Axis (Retention Time):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_RETENTION_TIME_PEAKS, "GridLine Style X Axis (Retention Time):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_RETENTION_TIME_PEAKS, "GridLine Color X Axis (Retention Time):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Intensity", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY_PEAKS, "Show Y Axis (Intensity)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY_PEAKS, "Position Y Axis (Intensity):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY_PEAKS, "Color Y Axis (Intensity):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY_PEAKS, "GridLine Style Y Axis (Intensity):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY_PEAKS, "GridLine Color Y Axis (Intensity):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Intensity%", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY_PEAKS, "Show Y Axis (Intensity %)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY_PEAKS, "Position Y Axis (Intensity %):", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY_PEAKS, "Color Y Axis (Intensity %):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY_PEAKS, "GridLine Style Y Axis (Intensity %):", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY_PEAKS, "GridLine Color Y Axis (Intensity %):", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
