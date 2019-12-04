/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageUnknown extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageUnknown() {
		super(FLAT);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Unknown Identifier Settings.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		/*
		 * Display all available import converter.
		 */
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_MATCH_FACTOR_UNKNOWN, "Min Match Factor", PreferenceSupplier.MIN_FACTOR, PreferenceSupplier.MAX_FACTOR, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_REVERSE_MATCH_FACTOR_UNKNOWN, "Min Reverse Match Factor", PreferenceSupplier.MIN_FACTOR, PreferenceSupplier.MAX_FACTOR, getFieldEditorParent()));
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
