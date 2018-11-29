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
import org.eclipse.chemclipse.ux.extension.xxd.ui.fieldeditors.TargetFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageTargets extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageTargets() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Targets");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceConstants.P_USE_TARGET_LIST, "Use Target List", getFieldEditorParent()));
		addField(new TargetFieldEditor(PreferenceConstants.P_TARGET_LIST, "Targets", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_PROPAGATE_TARGET_ON_UPDATE, "Propagate Target on Update", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
