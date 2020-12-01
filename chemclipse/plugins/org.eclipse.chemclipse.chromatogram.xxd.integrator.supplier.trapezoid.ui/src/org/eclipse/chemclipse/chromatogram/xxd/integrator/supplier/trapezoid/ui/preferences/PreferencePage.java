/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.editors.IonTableEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.RetentionTimeMinutesFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Trapezoid Integrator");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new IonTableEditor(PreferenceSupplier.P_SELECTED_IONS, "Selected ions (default empty list: 0 = TIC)", getFieldEditorParent()));
		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_MINIMUM_PEAK_WIDTH, "Minimum peak width (minutes)", PreferenceSupplier.MIN_RETENTION_TIME, PreferenceSupplier.MAX_RETENTION_TIME, getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_MINIMUM_SIGNAL_TO_NOISE_RATIO, "Minimum S/N ratio.", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_MINIMUM_PEAK_AREA, "Minimum peak area.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_PEAK_AREA_INCLUDE_BACKGROUND, "Include peak background in area calculation.", getFieldEditorParent()));
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
