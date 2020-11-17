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

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.fieldeditors.ColorCodesFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePagePCR extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePagePCR() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("PCR");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new ColorFieldEditor(PreferenceConstants.P_PCR_DEFAULT_COLOR, "Default Line Color:", getFieldEditorParent()));
		addField(new ColorCodesFieldEditor(PreferenceConstants.P_PCR_COLOR_CODES, "Color Codes", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
