/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.ui.preferences;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("AMDIS Settings.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplier.P_SPLIT_LIBRARY, "Split library to several output files (<= 65535 mass spectra)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_EXCLUDE_UNCERTAIN_IONS, "Exclude uncertain ions from ELU file conversion", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_UNIT_MASS_RESOLUTION, "Use unit mass resolution", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_REMOVE_INTENSITIES_LOWER_THAN_ONE, "Remove intesities < 1.0", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_NORMALIZE_INTENSITIES, "Normalize intensities", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_EXPORT_INTENSITIES_AS_INTEGER, "Export intensities as Integer", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
