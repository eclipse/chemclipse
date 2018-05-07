/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.editors.FileListEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Retention Index Filter Settings.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		FileListEditor fileListEditor = new FileListEditor(PreferenceSupplier.P_RETENTION_INDEX_FILES, "Retention Index (*.cal) Files", getFieldEditorParent());
		String[] filterExtensions = new String[]{"*.cal", "*.CAL"};
		String[] filterNames = new String[]{"AMDIS Calibration *.cal", "AMDIS Calibration *.CAL"};
		fileListEditor.setFilterExtensionsAndNames(filterExtensions, filterNames);
		addField(fileListEditor);
		//
		addField(new DirectoryFieldEditor(PreferenceSupplier.DEF_FILTER_PATH_MODELS_MSD, "Path MSD Files:", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.DEF_FILTER_PATH_MODELS_CSD, "Path CSD Files:", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
