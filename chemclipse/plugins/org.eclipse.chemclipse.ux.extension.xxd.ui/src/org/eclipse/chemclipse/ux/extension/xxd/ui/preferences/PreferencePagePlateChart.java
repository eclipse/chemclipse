/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePagePlateChart extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePagePlateChart() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle(ExtensionMessages.plateChart);
		setDescription(""); //$NON-NLS-1$
	}

	@Override
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_PCR_REFERENCE_LABEL, ExtensionMessages.referenceLabel, LabelSetting.getOptions(), getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_PCR_DEFAULT_COLOR, ExtensionMessages.defaultLineColor, getFieldEditorParent()));
		addField(new ColorCodesFieldEditor(PreferenceConstants.P_PCR_PLATE_COLOR_CODES, ExtensionMessages.colorCodes, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
