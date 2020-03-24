/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.preferences.PreferenceSupport;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageOverlay extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageOverlay() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Overlay");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE, "Compression Type:", PreferenceConstants.COMPRESSION_TYPES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_OVERLAY, "Display Color Scheme", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_OVERLAY, "Line Style:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS, "Show Referenced Chromatograms", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_OVERLAY_SHOW_AREA, "Overlay Show Area", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS, "Overlay Autofocus Profile Settings", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_OVERLAY_AUTOFOCUS_SHIFT_SETTINGS, "Overlay Autofocus Shift Settings", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceConstants.P_OVERLAY_SHIFT_X, "Overlay Shift X:", PreferenceConstants.MIN_OVERLAY_SHIFT_X, PreferenceConstants.MAX_OVERLAY_SHIFT_X, getFieldEditorParent()));
		addField(getIntegerFieldEditor(PreferenceConstants.P_INDEX_SHIFT_X, "Index Shift X", PreferenceConstants.MIN_INDEX_SHIFT_X, PreferenceConstants.MAX_INDEX_SHIFT_X, getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceConstants.P_OVERLAY_SHIFT_Y, "Overlay Shift Y:", PreferenceConstants.MIN_OVERLAY_SHIFT_Y, PreferenceConstants.MAX_OVERLAY_SHIFT_Y, getFieldEditorParent()));
		addField(getIntegerFieldEditor(PreferenceConstants.P_INDEX_SHIFT_Y, "Index Shift Y", PreferenceConstants.MIN_INDEX_SHIFT_Y, PreferenceConstants.MAX_INDEX_SHIFT_Y, getFieldEditorParent()));
	}

	private IntegerFieldEditor getIntegerFieldEditor(String name, String labelText, int min, int max, Composite parent) {

		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(name, labelText, parent);
		integerFieldEditor.setValidRange(min, max);
		return integerFieldEditor;
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
