/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.msd.model.support.CalculationType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageMergePeaks extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageMergePeaks() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Merge Peaks");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_MERGE_PEAKS_CALCULATION_TYPE, "Calculation Type", CalculationType.getCalculationTypes(), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_MERGE_PEAKS_IDENTIFICATION_TARGETS, "Merge Identification Targets", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_MERGE_PEAKS_DELETE_ORIGINS, "Delete Origins", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
