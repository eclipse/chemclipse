/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.ui.preferences;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.settings.ShiftDirection;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FilterPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public FilterPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the RTSifter filter settings.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHIFT_ALL_SCANS, "Shift all scans", getFieldEditorParent()));
		//
		addField(new IntegerFieldEditor(PreferenceSupplier.P_MILLISECONDS_BACKWARD, "Milliseconds Backward", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_MILLISECONDS_FAST_BACKWARD, "Milliseconds Fast Backward", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_MILLISECONDS_FORWARD, "Milliseconds Forward", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_MILLISECONDS_FAST_FORWARD, "Milliseconds Fast Forward", getFieldEditorParent()));
		/*
		 * Default Shift Direction (in case of batch processing.
		 */
		addField(new ComboFieldEditor(PreferenceSupplier.P_DEFAULT_SHIFT_DIRECTION, "Default Shift Direction", ShiftDirection.getElements(), getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
