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
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageTaskCombined extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageTaskCombined() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Combined Scans");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART, "Combined Scan:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
