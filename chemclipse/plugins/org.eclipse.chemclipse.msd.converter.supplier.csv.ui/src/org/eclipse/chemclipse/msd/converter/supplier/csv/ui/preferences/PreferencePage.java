/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.ui.preferences;

import org.eclipse.chemclipse.model.settings.Delimiter;
import org.eclipse.chemclipse.msd.converter.supplier.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.converter.supplier.csv.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("CSV Converter");
		setDescription("");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {

		addField(new LabelFieldEditor("Import", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_IMPORT_DELIMITER, "Delimiter", Delimiter.getOptions(), getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_IMPORT_ZERO_MARKER, "Zero Marker", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Export", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_EXPORT_USE_TIC, "Export only TIC values.", getFieldEditorParent()));
	}
}
