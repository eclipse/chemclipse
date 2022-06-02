/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.fieldeditors.NamedTracesFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageNamedTraces extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageNamedTraces() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Named Traces");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new NamedTracesFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES, "Named Traces", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
