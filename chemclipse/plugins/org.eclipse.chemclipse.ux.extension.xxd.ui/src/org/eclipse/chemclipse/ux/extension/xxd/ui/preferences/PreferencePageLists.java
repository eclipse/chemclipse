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
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageLists extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageLists() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Lists Column Order (do not edit)");
	}

	public void createFieldEditors() {

		addField(new StringFieldEditor(PreferenceConstants.P_COLUMN_ORDER_PEAK_LIST, "Peak List", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_COLUMN_ORDER_SCAN_LIST, "Scan List", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_COLUMN_ORDER_PEAK_SCAN_LIST, "Peak/Scan List", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_COLUMN_ORDER_TARGET_LIST, "Target List", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
