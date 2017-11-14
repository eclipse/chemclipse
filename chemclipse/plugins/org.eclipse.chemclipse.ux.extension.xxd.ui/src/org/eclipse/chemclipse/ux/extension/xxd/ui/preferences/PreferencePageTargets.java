/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.preferences.editors.TargetListEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
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
		addField(new TargetListEditor(PreferenceConstants.P_TARGET_LIST, "Targets", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
