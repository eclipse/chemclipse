/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add a reference label setting
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.pcr.model.core.support.LabelSetting;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.fieldeditors.ColorCodesFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePagePlateChart extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePagePlateChart() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Plate Chart");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_PCR_REFERENCE_LABEL, "Reference Label:", LabelSetting.getOptions(), getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_PCR_DEFAULT_COLOR, "Default Line Color:", getFieldEditorParent()));
		addField(new ColorCodesFieldEditor(PreferenceConstants.P_PCR_PLATE_COLOR_CODES, "Color Codes", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
