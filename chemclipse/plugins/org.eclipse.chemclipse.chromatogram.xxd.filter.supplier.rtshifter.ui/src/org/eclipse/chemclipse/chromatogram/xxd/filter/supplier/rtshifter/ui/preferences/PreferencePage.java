/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.IntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Retention Time Filter");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new IntegerFieldEditor(PreferenceSupplier.P_MILLISECONDS_SHIFT, "Shift Retention Time [ms]", PreferenceSupplier.MIN_MILLISECONDS_SHIFT, PreferenceSupplier.MAX_MILLISECONDS_SHIFT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHIFT_ALL_SCANS, "Shift all scans", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_STRETCH_MILLISECONDS_SCAN_DELAY, "Stretch Scan Delay [ms]", PreferenceSupplier.STRETCH_MILLISECONDS_SCAN_DELAY_MIN, PreferenceSupplier.STRETCH_MILLISECONDS_SCAN_DELAY_MAX, getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_STRETCH_MILLISECONDS_LENGTH, "Stretch Length [ms]", PreferenceSupplier.STRETCH_MILLISECONDS_LENGTH_MIN, PreferenceSupplier.STRETCH_MILLISECONDS_LENGTH_MAX, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_LIMIT_FACTOR, "Limit Factor (Gap Filler):", PreferenceSupplier.MIN_LIMIT_FACTOR, PreferenceSupplier.MAX_LIMIT_FACTOR, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {

	}
}
