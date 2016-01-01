/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.chemclipse.msd.swt.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Mass Spectrum View Settings");
	}

	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("The underlying mass spectrum will be kept untouched.", getFieldEditorParent()));
		addField(new LabelFieldEditor("It's only how many m/z values are display in the views and lists.", getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_FILTER_MASS_SPECTRUM, "Filter Mass Spectrum View", getFieldEditorParent()));
		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(PreferenceSupplier.P_FILTER_LIMIT_IONS, "Number of Ions in View <=", getFieldEditorParent());
		integerFieldEditor.setValidRange(PreferenceSupplier.MIN_FILTER_LIMIT_IONS, PreferenceSupplier.MAX_FILTER_LIMIT_IONS);
		addField(integerFieldEditor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
