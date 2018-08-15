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

import org.eclipse.chemclipse.support.ui.preferences.editors.QuantReferencesListEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageQuantitation extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageQuantitation() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Quantitation");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceConstants.P_USE_QUANTITATION_REFERENCE_LIST, "Use Quantitation Reference List", getFieldEditorParent()));
		addField(new QuantReferencesListEditor(PreferenceConstants.P_QUANTITATION_REFERENCE_LIST, "Quantitation References", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
