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

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageScans extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageScans() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Scans");
	}

	public void createFieldEditors() {

		addField(new StringFieldEditor(PreferenceConstants.P_SCAN_LABEL_FONT_NAME, "Font Name:", getFieldEditorParent()));
		//
		IntegerFieldEditor fontSizeEditor = new IntegerFieldEditor(PreferenceConstants.P_SCAN_LABEL_FONT_SIZE, "Font Size:", getFieldEditorParent());
		fontSizeEditor.setValidRange(PreferenceConstants.MIN_SCAN_LABEL_FONT_SIZE, PreferenceConstants.MAX_SCAN_LABEL_FONT_SIZE);
		addField(fontSizeEditor);
		//
		addField(new ComboFieldEditor(PreferenceConstants.P_SCAN_LABEL_FONT_STYLE, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		//
		IntegerFieldEditor highestIntensitiesEditor = new IntegerFieldEditor(PreferenceConstants.P_SCAN_LABEL_HIGHEST_INTENSITIES, "Label Intensities:", getFieldEditorParent());
		highestIntensitiesEditor.setValidRange(PreferenceConstants.MIN_SCAN_LABEL_HIGHEST_INTENSITIES, PreferenceConstants.MAX_SCAN_LABEL_HIGHEST_INTENSITIES);
		addField(highestIntensitiesEditor);
	}

	public void init(IWorkbench workbench) {

	}
}
