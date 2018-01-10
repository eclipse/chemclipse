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
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePagePeaks extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePagePeaks() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Peaks");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceConstants.P_INCLUDE_PEAK_BACKGROUND, "Include Background", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_PEAK, "Show Peak", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_PEAK_BASELINE, "Show Peak Baseline", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_PEAK_TANGENTS, "Show Peak Tangents", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_PEAK_WIDTH_0, "Show Peak Width 0% Height", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_PEAK_WIDTH_50, "Show Peak Width 50% Height", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_PEAK_WIDTH_CONDAL_BOSH, "Show Peak Width 15% and 85% Height (Condal-Bosh)", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_1, "Color Peak 1:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_2, "Color Peak 2:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_BASELINE, "Color Peak Baseline:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_TANGENTS, "Color Peak Tangents:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_WIDTH_0, "Color Peak Width 0% Height:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_WIDTH_50, "Color Peak Width 50% Height:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_PEAK_WIDTH_CONDAL_BOSH, "Color Peak Width 15% and 85% Height (Condal-Bosh):", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
