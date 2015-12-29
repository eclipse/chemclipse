/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("File Identifier Settings.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new FileFieldEditor(PreferenceSupplier.P_MASS_SPECTRA_FILE, "Select a *.msl file.", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_MASS_SPECTRUM_COMPARATOR_ID, "Mass Spectrum Comparator Id", MassSpectrumComparator.getAvailableComparatorIds(), getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_NUMBER_OF_TARGETS, "Number of Targets", getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_MATCH_FACTOR, "Min Match Factor", 0.0f, 100.0f, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_REVERSE_MATCH_FACTOR, "Min Reverse Match Factor", 0.0f, 100.0f, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
