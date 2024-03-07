/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageFileExplorer extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageFileExplorer() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("File Explorer");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_MSD, "MSD (Quadrupole, IonTrap, ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_LIBRARY_MSD, "MSD (Libraries, ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_SCANS_MSD, "MSD (MALDI-TOF, ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_CSD, "CSD (GC-FID, GC-ECD, ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_WSD, "WSD (HPLC-DAD, HPLC-UV/Vis ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_VSD, "VSD (HPLC-Raman, ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_TSD, "TSD (IMS, ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_SCAN_VSD, "VSD (FTIR, NIR, Raman, ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_SCAN_WSD, "WSD (UV/Vis, ...)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_NMR, "NMR", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_CAL, "CAL", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_PCR, "PCR", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_SEQUENCE, "SEQ (Sequences)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_METHOD, "MTH (Methods)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_DATA_QUANT_DB, "QDB (Quantitation Databases)", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
