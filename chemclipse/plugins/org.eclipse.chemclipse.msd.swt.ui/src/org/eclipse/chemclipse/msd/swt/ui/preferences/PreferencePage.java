/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.preferences;

import org.eclipse.chemclipse.msd.swt.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Settings (MSD)");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("The underlying mass spectrum will be kept untouched.", getFieldEditorParent()));
		addField(new LabelFieldEditor("It's only how many m/z values are display in the views and lists.", getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_FILTER_MASS_SPECTRUM, "Filter Mass Spectrum View", getFieldEditorParent()));
		IntegerFieldEditor filterLimitIonsEditor = new IntegerFieldEditor(PreferenceSupplier.P_FILTER_LIMIT_IONS, "Number of Ions in View <=", getFieldEditorParent());
		filterLimitIonsEditor.setValidRange(PreferenceSupplier.MIN_FILTER_LIMIT_IONS, PreferenceSupplier.MAX_FILTER_LIMIT_IONS);
		addField(filterLimitIonsEditor);
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		//
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_PATH_MASS_SPECTRUM_LIBRARIES, "Path mass spectrum libraries.", getFieldEditorParent()));
		IntegerFieldEditor libraryMSDLimitSortingEditor = new IntegerFieldEditor(PreferenceSupplier.P_LIBRARY_MSD_LIMIT_SORTING, "Disable sorting above number of entries:", getFieldEditorParent());
		libraryMSDLimitSortingEditor.setValidRange(PreferenceSupplier.MIN_LIBRARY_MSD_LIMIT_SORTING, PreferenceSupplier.MAX_LIBRARY_MSD_LIMIT_SORTING);
		addField(libraryMSDLimitSortingEditor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
