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

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
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
		noDefaultAndApplyButton();
	}

	@Override
	public void createFieldEditors() {

		addField(createtMethodPathEditor());
		addField(new StringFieldEditor(PreferenceConstants.P_SELECTED_METHOD_NAME, "Method Name (Selected)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_CSD, "Show method processor items (CSD)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_MSD, "Show method processor items (MSD)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_WSD, "Show method processor items (WSD)", getFieldEditorParent()));
	}

	private DirectoryFieldEditor createtMethodPathEditor() {

		DirectoryFieldEditor methodDirectoryEditor = new DirectoryFieldEditor(org.eclipse.chemclipse.converter.preferences.PreferenceSupplier.P_METHOD_EXPLORER_PATH_ROOT_FOLDER, "Method Folder", getFieldEditorParent()) {

			private ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, org.eclipse.chemclipse.converter.preferences.PreferenceSupplier.INSTANCE().getPreferenceNode());

			@Override
			public void setPreferenceStore(IPreferenceStore store) {

				// because of Eclipse Bug #551642 it is currently not possible to use a store per field so we create a very special subclass here.
				if(store == null) {
					this.store = null;
					super.setPreferenceStore(null);
				} else {
					super.setPreferenceStore(this.store);
				}
			}
		};
		return methodDirectoryEditor;
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
