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
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swtchart.extensions.preferences.PreferenceSupport;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageChromatogramScans extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageChromatogramScans() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Chromatogram Scans");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new LabelFieldEditor("Selected Scan (Chromatogram)", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN, "Color:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE, "Marker Size:", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE, "Marker Type:", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, "Show Labels", getFieldEditorParent()));
		addField(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE, "Label Symbol Size:", PreferenceConstants.MIN_SYMBOL_SIZE, PreferenceConstants.MAX_SYMBOL_SIZE);
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME, "Label Font Name:", getFieldEditorParent()));
		addField(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE, "Label Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE);
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE, "Label Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED, "Color Identified:", getFieldEditorParent()));
	}

	private void addField(String name, String labelText, int min, int max) {

		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(name, labelText, getFieldEditorParent());
		integerFieldEditor.setValidRange(min, max);
		addField(integerFieldEditor);
	}

	public void init(IWorkbench workbench) {

	}
}
