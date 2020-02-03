/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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

public class PreferencePageTaskPeaks extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageTaskPeaks() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Peaks");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_PEAK_CHART, "Peak Chart:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS, "Peak Details:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR, "Peak Detector:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_PEAK_TRACES, "Peak Traces:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
