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

import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePageMethods extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageMethods() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Methods");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		IPreferenceSupplier methodSupplier = org.eclipse.chemclipse.converter.preferences.PreferenceSupplier.INSTANCE();
		DirectoryFieldEditor editor = new DirectoryFieldEditor(org.eclipse.chemclipse.converter.preferences.PreferenceSupplier.P_METHOD_EXPLORER_PATH_ROOT_FOLDER, "Method Folder", getFieldEditorParent());
		editor.setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, methodSupplier.getPreferenceNode()));
		addField(editor);
		addField(new StringFieldEditor(PreferenceConstants.P_SELECTED_METHOD_NAME, "Method Name (Selected)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_CSD, "Show method processor items (CSD)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_MSD, "Show method processor items (MSD)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_WSD, "Show method processor items (WSD)", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
