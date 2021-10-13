/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageMolecule extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageMolecule() {

		super(FLAT);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Molecule");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new DirectoryFieldEditor(PreferenceConstants.P_MOLECULE_PATH_EXPORT, "Molecule Path Export", getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceConstants.P_LENGTH_MOLECULE_NAME_EXPORT, "Molecule Name Length", PreferenceConstants.MIN_LENGTH_NAME_EXPORT, PreferenceConstants.MAX_LENGTH_NAME_EXPORT, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
