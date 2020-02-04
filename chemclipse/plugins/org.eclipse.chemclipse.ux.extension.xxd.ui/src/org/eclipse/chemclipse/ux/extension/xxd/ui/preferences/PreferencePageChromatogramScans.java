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
import org.eclipse.swtchart.extensions.preferences.PreferenceSupport;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageChromatogramScans extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageChromatogramScans(IPreferenceStore preferenceStore) {
		super(GRID);
		setPreferenceStore(preferenceStore);
		setTitle("Chromatogram Scans");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new LabelFieldEditor("Selected Scan", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN, "Color:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE, "Marker Size:", PreferenceConstants.MIN_SYMBOL_SIZE, PreferenceConstants.MAX_SYMBOL_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE, "Marker Type:", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Scan Labels", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, "Show Labels", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME, "Font Name:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE, "Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Scan", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE, "Marker Size:", PreferenceConstants.MIN_SYMBOL_SIZE, PreferenceConstants.MAX_SYMBOL_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_MARKER_TYPE, "Marker Type (Normal):", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_IDENTIFIED_SCAN, "Color (Identified):", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_IDENTIFIED_SCAN_MARKER_TYPE, "Marker Type (Identified):", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
