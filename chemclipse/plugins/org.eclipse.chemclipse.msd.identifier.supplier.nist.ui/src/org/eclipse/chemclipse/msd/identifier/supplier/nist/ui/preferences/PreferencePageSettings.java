/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ByteFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageSettings extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageSettings() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("NIST (extern) - Settings");
		setDescription("");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new FloatFieldEditor(PreferenceSupplier.P_LIMIT_MATCH_FACTOR, "Limit Match Factor", IIdentifierSettings.MIN_LIMIT_MATCH_FACTOR, IIdentifierSettings.MAX_LIMIT_MATCH_FACTOR, getFieldEditorParent()));
		ByteFieldEditor numberOfTargetsEditor = new ByteFieldEditor(PreferenceSupplier.P_NUMBER_OF_TARGETS, "Number of Targets", getFieldEditorParent());
		numberOfTargetsEditor.setValidRange(PreferenceSupplier.MIN_NUMBER_OF_TARGETS, PreferenceSupplier.MAX_NUMBER_OF_TARGETS);
		addField(numberOfTargetsEditor);
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_MATCH_FACTOR, "Min Match Factor", 0.0f, 100.0f, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_REVERSE_MATCH_FACTOR, "Min Reverse Match Factor", 0.0f, 100.0f, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_TIMEOUT_IN_MINUTES, "Timeout [min]", 0, 60, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_WAIT_IN_SECONDS, "Wait [s]", 0, 360, getFieldEditorParent()));
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
