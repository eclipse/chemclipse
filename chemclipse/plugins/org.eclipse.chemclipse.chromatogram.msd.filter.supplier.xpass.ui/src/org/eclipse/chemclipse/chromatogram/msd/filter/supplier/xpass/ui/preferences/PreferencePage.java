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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.ui.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("XPass Filter");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		IntegerFieldEditor numberHighestFieldEditor = new IntegerFieldEditor(PreferenceSupplier.P_NUMBER_HIGHEST, "Number Highest", getFieldEditorParent());
		numberHighestFieldEditor.setValidRange(PreferenceSupplier.MIN_NUMBER_HIGHEST, PreferenceSupplier.MAX_NUMBER_HIGHEST);
		addField(numberHighestFieldEditor);
		IntegerFieldEditor numberLowestFieldEditor = new IntegerFieldEditor(PreferenceSupplier.P_NUMBER_LOWEST, "Number Lowest", getFieldEditorParent());
		numberLowestFieldEditor.setValidRange(PreferenceSupplier.MIN_NUMBER_LOWEST, PreferenceSupplier.MAX_NUMBER_LOWEST);
		addField(numberLowestFieldEditor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
