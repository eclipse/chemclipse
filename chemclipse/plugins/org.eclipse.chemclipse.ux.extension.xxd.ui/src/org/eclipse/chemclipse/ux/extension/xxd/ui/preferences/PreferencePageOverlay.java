/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageOverlay extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageOverlay() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Overlay");
	}

	public void createFieldEditors() {

		addField(new DoubleFieldEditor(PreferenceConstants.P_MINUTES_SHIFT_X, "Shift X (Minutes):", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceConstants.P_ABSOLUTE_SHIFT_Y, "Shift Y (Absolute):", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_ION_USERS_CHOICE, "Overlay Ions User Choice:", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
