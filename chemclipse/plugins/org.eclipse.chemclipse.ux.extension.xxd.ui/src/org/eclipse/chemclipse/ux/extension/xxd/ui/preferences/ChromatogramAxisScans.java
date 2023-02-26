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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditor;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swtchart.extensions.charts.ChartOptions;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ChromatogramAxisScans extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ChromatogramAxisScans() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle(ExtensionMessages.chromatogramScansXAxis);
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new StringFieldEditor(PreferenceConstants.P_TITLE_X_AXIS_SCANS, ExtensionMessages.title + ":", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FORMAT_X_AXIS_SCANS, ExtensionMessages.format + ":", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_SCANS, ExtensionMessages.show, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_POSITION_X_AXIS_SCANS, ExtensionMessages.position + ":", ChartOptions.POSITIONS, getFieldEditorParent()));
		if(PreferencesSupport.isDarkTheme()) {
			addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_SCANS_DARKTHEME, ExtensionMessages.color + ":", getFieldEditorParent()));
		} else {
			addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_X_AXIS_SCANS, ExtensionMessages.color + ":", getFieldEditorParent()));
		}
		addField(new StringFieldEditor(PreferenceConstants.P_FONT_NAME_X_AXIS_SCANS, ExtensionMessages.fontName + ":", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceConstants.P_FONT_SIZE_X_AXIS_SCANS, ExtensionMessages.fontSize + ":", PreferenceConstants.MIN_FONT_SIZE, PreferenceConstants.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_FONT_STYLE_X_AXIS_SCANS, ExtensionMessages.fontStyle + ":", ChartOptions.FONT_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS, ExtensionMessages.gridLineStyle + ":", ChartOptions.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS, ExtensionMessages.gridLineColor + ":", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SCANS, ExtensionMessages.showAxisTitle, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
