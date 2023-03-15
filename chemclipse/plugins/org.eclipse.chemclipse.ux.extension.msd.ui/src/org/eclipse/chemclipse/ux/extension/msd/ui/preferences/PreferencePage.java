/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.msd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Mass Selective Detector (MSD)");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceSupplier.P_SELECTED_ORGANIC_COMPOUND, "Organic Compound:", PreferenceSupplier.getOrganicCompoundPresets(), getFieldEditorParent()));
		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(PreferenceSupplier.P_MAGNIFICATION_FACTOR, "Magnification Factor", getFieldEditorParent());
		integerFieldEditor.setValidRange(PreferenceSupplier.DEF_MAGNIFICATION_FACTOR_MIN, PreferenceSupplier.DEF_MAGNIFICATION_FACTOR_MAX);
		addField(integerFieldEditor);
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_PROFILE_MASS_SPECTRUM_VIEW, "Use profile mass spectrum view.", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_PATH_OPEN_CHROMATOGRAMS, "Path Chromatograms", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}