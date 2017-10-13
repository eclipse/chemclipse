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

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.eavp.service.swtchart.preferences.PreferenceSupport;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("UI Settings");
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_OVERLAY_NORMAL, "Overlay Color Scheme Normal", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_OVERLAY_SIC, "Overlay Color Scheme SIC", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		//
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_OVERLAY_TIC, "Line Style TIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_OVERLAY_BPC, "Line Style BPC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_OVERLAY_XIC, "Line Style XIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_OVERLAY_SIC, "Line Style SIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_OVERLAY_TSC, "Line Style TSC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_OVERLAY_DEFAULT, "Line Style Default:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
