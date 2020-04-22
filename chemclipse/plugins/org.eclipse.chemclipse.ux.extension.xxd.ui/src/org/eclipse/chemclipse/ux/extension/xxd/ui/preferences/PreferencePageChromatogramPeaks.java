/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support setting the preference store
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swtchart.extensions.preferences.PreferenceSupport;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageChromatogramPeaks extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String FIELD_MISCELLANEOUS = "Miscellaneous";
	private static final String FIELD_PEAKS = "Peaks";
	private static final String FIELD_PEAK_LABELS = "Peak Labels";
	private static final String FIELD_SELECTED_PEAK_SCAN_MARKER = "Selected Peak (Scan Marker)";

	public PreferencePageChromatogramPeaks() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Chromatogram Peaks");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new LabelFieldEditor(FIELD_SELECTED_PEAK_SCAN_MARKER, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_PEAK, "Color:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_SIZE, "Marker Size:", PreferenceConstants.MIN_SYMBOL_SIZE, PreferenceConstants.MAX_SYMBOL_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_TYPE, "Marker Type:", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor(FIELD_PEAK_LABELS, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS, "Show Labels", getFieldEditorParent()));
		addField(new BooleanFieldEditor(ExtendedChromatogramUI.PREFERENCE_SHOW_LABELS_AT_TIC, "Show Labels always above TIC", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME, "Font Name:", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE, "Font Size:", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor(FIELD_PEAKS, getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE, "Symbol Size:", PreferenceConstants.MIN_SYMBOL_SIZE, PreferenceConstants.MAX_SYMBOL_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE, "Marker Type (Selected Peak):", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL_MARKER_TYPE, "Marker Type (Active Normal):", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_NORMAL_MARKER_TYPE, "Marker Type (Inactive Normal):", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_ISTD_MARKER_TYPE, "Marker Type (Active ISTD):", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_ISTD_MARKER_TYPE, "Marker Type (Inactive ISTD):", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor(FIELD_MISCELLANEOUS, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION, "Move retention time on peak selection", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
