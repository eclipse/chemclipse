/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageChromatogramAxes extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageChromatogramAxes(IPreferenceStore preferenceStore) {
		super(GRID);
		setPreferenceStore(preferenceStore);
		setTitle("Chromatogram Axes");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Milliseconds (X Axis)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS, "Show ", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS, "Position:", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS, "Color:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FONT_NAME_X_AXIS_MILLISECONDS, "Font Name:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_FONT_SIZE_X_AXIS_MILLISECONDS, "Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_FONT_STYLE_X_AXIS_MILLISECONDS, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS, "GridLine Style:", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS, "GridLine Color:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MILLISECONDS, "Show Axis Title", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Seconds (X Axis)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_SECONDS, "Show", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_SECONDS, "Position:", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_SECONDS, "Color:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FONT_NAME_X_AXIS_SECONDS, "Font Name:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_FONT_SIZE_X_AXIS_SECONDS, "Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_FONT_STYLE_X_AXIS_SECONDS, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS, "GridLine Style:", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS, "GridLine Color:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SECONDS, "Show Axis Title", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Minutes (X Axis)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_MINUTES, "Show", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_MINUTES, "Position:", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_MINUTES, "Color:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FONT_NAME_X_AXIS_MINUTES, "Font Name:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_FONT_SIZE_X_AXIS_MINUTES, "Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_FONT_STYLE_X_AXIS_MINUTES, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES, "GridLine Style:", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES, "GridLine Color:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MINUTES, "Show Axis Title", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Scans (X Axis)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_SCANS, "Show", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_SCANS, "Position:", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_SCANS, "Color:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FONT_NAME_X_AXIS_SCANS, "Font Name:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_FONT_SIZE_X_AXIS_SCANS, "Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_FONT_STYLE_X_AXIS_SCANS, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS, "GridLine Style:", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS, "GridLine Color:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SCANS, "Show Axis Title", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Intensity (Y Axis)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY, "Show", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY, "Position:", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY, "Color:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FONT_NAME_Y_AXIS_INTENSITY, "Font Name:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_FONT_SIZE_Y_AXIS_INTENSITY, "Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_FONT_STYLE_Y_AXIS_INTENSITY, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY, "GridLine Style:", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY, "GridLine Color:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_INTENSITY, "Show Axis Title", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Intensity% (Y Axis)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY, "Show", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY, "Position:", PreferenceConstants.POSITIONS, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY, "Color:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FONT_NAME_Y_AXIS_RELATIVE_INTENSITY, "Font Name:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_FONT_SIZE_Y_AXIS_RELATIVE_INTENSITY, "Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_FONT_STYLE_Y_AXIS_RELATIVE_INTENSITY, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY, "GridLine Style:", PreferenceConstants.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY, "GridLine Color:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY, "Show Axis Title", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
