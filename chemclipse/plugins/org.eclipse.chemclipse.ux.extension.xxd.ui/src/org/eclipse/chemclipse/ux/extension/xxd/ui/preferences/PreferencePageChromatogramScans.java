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
		setDescription("Chromatogram Scans");
	}

	public void createFieldEditors() {

		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN, "Color Chromatogram Selected Scan:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE, "Chromatogram Selected Scan Marker Size:", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE, "Chromatogram Selected Scan Marker Type:", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, "Show Chromatogram Scan Labels", getFieldEditorParent()));
		addField(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE, "Chromatogram Scan Label Symbol Size:", PreferenceConstants.MIN_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE, PreferenceConstants.MAX_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME, "Chromatogram Scan Label Name:", getFieldEditorParent()));
		addField(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE, "Chromatogram Scan Label Font Size:", PreferenceConstants.MIN_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE, PreferenceConstants.MAX_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE, "Chromatogram Scan Label Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED, "Color Chromatogram Selected Scan Identified:", getFieldEditorParent()));
	}

	private void addField(String name, String labelText, int min, int max) {

		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(name, labelText, getFieldEditorParent());
		integerFieldEditor.setValidRange(min, max);
		addField(integerFieldEditor);
	}

	public void init(IWorkbench workbench) {

	}
}
