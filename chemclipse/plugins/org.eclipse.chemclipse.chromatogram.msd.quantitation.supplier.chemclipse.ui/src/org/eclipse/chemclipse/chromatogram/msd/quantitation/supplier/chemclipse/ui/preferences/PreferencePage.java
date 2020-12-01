/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.preferences;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Quantitation Support");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_QUANTITATION_DATABASE_EDITOR, "Use Editor Quantitation DB", getFieldEditorParent()));
		addField(new FileFieldEditor(PreferenceSupplier.P_SELECTED_QUANTITATION_DATABASE, "Selected Quantitation DB (*.ocq)", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_FILTER_PATH_NEW_QUANT_DB, "Path new Quantitation DBs", getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_RETENTION_TIME_NEGATIVE_DEVIATION, "Retention Time Deviation (-)", PreferenceSupplier.MIN_RETENTION_TIME, PreferenceSupplier.MAX_RETENTION_TIME, getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_RETENTION_TIME_POSITIVE_DEVIATION, "Retention Time Deviation (+)", PreferenceSupplier.MIN_RETENTION_TIME, PreferenceSupplier.MAX_RETENTION_TIME, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_RETENTION_INDEX_NEGATIVE_DEVIATION, "Retention Index Deviation (-)", PreferenceSupplier.MIN_RETENTION_INDEX, PreferenceSupplier.MAX_RETENTION_INDEX, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_RETENTION_INDEX_POSITIVE_DEVIATION, "Retention Index Deviation (+)", PreferenceSupplier.MIN_RETENTION_INDEX, PreferenceSupplier.MAX_RETENTION_INDEX, getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(PreferenceSupplier.P_QUANTITATION_STRATEGY, "Quantitation Strategy", 1, PreferenceSupplier.QUANTITATION_STRATEGY_OPTIONS, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
