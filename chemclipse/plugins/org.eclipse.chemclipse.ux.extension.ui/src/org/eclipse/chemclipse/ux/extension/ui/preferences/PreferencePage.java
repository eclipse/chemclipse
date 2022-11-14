/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Drive Settings");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_SELECTED_DRIVE_PATH, "Selected Drive Path", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_SELECTED_HOME_PATH, "Selected Home Path", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_SELECTED_WORKSPACE_PATH, "Selected Workspace Path", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_SELECTED_USER_LOCATION_PATH, "Selected User Location Path", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_USER_LOCATION_PATH, "User Location", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_OPEN_FIRST_DATA_MATCH_ONLY, "Open First Data Match Only", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Microsoft Windows", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_WINDOWS_LIST_DRIVES_BY_TYPE, "List Drives By Type", getFieldEditorParent()));
		addField(new DriveTypeFieldEditor(PreferenceConstants.P_WINDOWS_DRIVE_TYPE, "Drive Type", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}