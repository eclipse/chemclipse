/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.ui.preferences;

import org.eclipse.chemclipse.process.supplier.batchprocess.ui.Activator;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Batch Process");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new DirectoryFieldEditor(PreferenceSupplier.P_FILTER_PATH_IMPORT_RECORDS, "Path Import Records", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
