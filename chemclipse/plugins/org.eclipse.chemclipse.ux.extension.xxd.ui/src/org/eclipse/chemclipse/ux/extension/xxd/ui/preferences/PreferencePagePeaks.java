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

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePagePeaks extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePagePeaks() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Peaks");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceConstants.P_PEAK_INCLUDE_BACKGROUND, "Include Background", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_1, "Color Peak 1:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_2, "Color Peak 2:", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
