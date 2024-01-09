/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
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

public class ChromatogramAxisIntensity extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ChromatogramAxisIntensity() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle(ExtensionMessages.chromatogramIntensityYAxis);
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new StringFieldEditor(PreferenceSupplier.P_TITLE_Y_AXIS_INTENSITY, ExtensionMessages.title + ":", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_FORMAT_Y_AXIS_INTENSITY, ExtensionMessages.format + ":", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_Y_AXIS_INTENSITY, ExtensionMessages.show, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_POSITION_Y_AXIS_INTENSITY, ExtensionMessages.position + ":", ChartOptions.POSITIONS, getFieldEditorParent()));
		if(PreferencesSupport.isDarkTheme()) {
			addField(new ColorFieldEditor(PreferenceSupplier.P_COLOR_Y_AXIS_INTENSITY_DARKTHEME, ExtensionMessages.color + ":", getFieldEditorParent()));
		} else {
			addField(new ColorFieldEditor(PreferenceSupplier.P_COLOR_Y_AXIS_INTENSITY, ExtensionMessages.color + ":", getFieldEditorParent()));
		}
		addField(new StringFieldEditor(PreferenceSupplier.P_FONT_NAME_Y_AXIS_INTENSITY, ExtensionMessages.fontName + ":", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceSupplier.P_FONT_SIZE_Y_AXIS_INTENSITY, ExtensionMessages.fontSize + ":", PreferenceSupplier.MIN_FONT_SIZE, PreferenceSupplier.MAX_FONT_SIZE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_FONT_STYLE_Y_AXIS_INTENSITY, ExtensionMessages.fontStyle + ":", ChartOptions.FONT_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY, ExtensionMessages.gridLineStyle + ":", ChartOptions.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceSupplier.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY, ExtensionMessages.gridLineColor + ":", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_Y_AXIS_TITLE_INTENSITY, ExtensionMessages.showAxisTitle, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
