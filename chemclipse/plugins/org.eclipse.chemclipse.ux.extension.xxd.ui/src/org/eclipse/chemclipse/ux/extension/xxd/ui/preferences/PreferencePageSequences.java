/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageSequences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageSequences() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Sequences");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplier.P_SEQUENCE_EXPLORER_USE_SUBFOLDER, "Use Subfolder", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SEQUENCE_EXPLORER_SORT_DATA, "Sort Data", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER, "Root Folder", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_SEQUENCE_EXPLORER_PATH_PARENT_FOLDER, "Parent Folder", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_SEQUENCE_EXPLORER_PATH_SUB_FOLDER, "Sub Folder", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_SEQUENCE_EXPLORER_PATH_DIALOG_FOLDER, "Dialog Folder", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
