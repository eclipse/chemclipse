/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences;

import org.eclipse.chemclipse.support.preferences.PreferenceSupplierSupport;
import org.eclipse.chemclipse.support.ui.Activator;
import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(SupportMessages.supportSettings);
	}

	@Override
	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplierSupport.P_CLIPBOARD_TABLE_DEFAULT_SORTING, SupportMessages.clipboardTableDefaultSorting, getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplierSupport.P_UNDO_LIMIT, SupportMessages.maximumAllowedUndoSteps, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}