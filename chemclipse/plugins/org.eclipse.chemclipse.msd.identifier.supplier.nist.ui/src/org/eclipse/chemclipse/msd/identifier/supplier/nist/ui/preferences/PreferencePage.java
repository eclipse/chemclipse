/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.preferences;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("NIST-DB Commandline Connector");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		/*
		 * Use only the GUI without storing the results.
		 */
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_GUI_DIRECT, "Use the GUI version (results will be not stored)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_GUI_DIALOG, "Show the GUI dialog", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_TIMEOUT_IN_MINUTES, "Timeout (Minutes)", getFieldEditorParent()));
		/*
		 * Set additional settings.
		 */
		StringBuilder builder = new StringBuilder();
		builder.append("Number of Targets (");
		builder.append(PreferenceSupplier.MIN_NUMBER_OF_TARGETS);
		builder.append(" - ");
		builder.append(PreferenceSupplier.MAX_NUMBER_OF_TARGETS);
		builder.append(")");
		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(PreferenceSupplier.P_NUMBER_OF_TARGETS, builder.toString(), getFieldEditorParent(), 3);
		integerFieldEditor.setValidRange(PreferenceSupplier.MIN_NUMBER_OF_TARGETS, PreferenceSupplier.MAX_NUMBER_OF_TARGETS);
		addField(integerFieldEditor);
		addField(new BooleanFieldEditor(PreferenceSupplier.P_STORE_TARGETS, "Store the hits (results) in the peaks or mass spectra.", getFieldEditorParent()));
		//
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_MATCH_FACTOR, "Min Match Factor", 0.0f, 100.0f, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_REVERSE_MATCH_FACTOR, "Min Reverse Match Factor", 0.0f, 100.0f, getFieldEditorParent()));
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