/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditor;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.ui.Activator;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.versions.QuantDatabaseVersion;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageOCQ extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageOCQ() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Quantitation Data (*.ocq)");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceSupplier.P_QUANTITATION_DB_VERSION_SAVE, "Save (*.ocq) as version:", QuantDatabaseVersion.getOptions(), getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceSupplier.P_QUANTITATION_DB_COMPRESSION_LEVEL, "Compression 0 = off, 9 = best", PreferenceSupplier.MIN_COMPRESSION_LEVEL, PreferenceSupplier.MAX_COMPRESSION_LEVEL, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}