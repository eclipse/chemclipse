/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.TextFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.preferences.PreferenceSupplierModelMSD;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageSubtract extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageSubtract() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStoreSubtract());
		setTitle("Subtract/Combined");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplierModelMSD.P_USE_NOMINAL_MZ, "Use nominal m/z", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplierModelMSD.P_USE_NORMALIZED_SCAN, "Use normalized scan", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplierModelMSD.P_CALCULATION_TYPE, "Calculation Type", CalculationType.getOptions(), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplierModelMSD.P_USE_PEAKS_INSTEAD_OF_SCANS, "Use peaks instead of scans", getFieldEditorParent()));
		addField(new TextFieldEditor(PreferenceSupplierModelMSD.P_SUBTRACT_MASS_SPECTRUM, "Subtract mass spectrum", getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceSupplierModelMSD.P_COPY_TRACES_CLIPBOARD, "Copy Traces", PreferenceSupplierModelMSD.MIN_TRACES, PreferenceSupplierModelMSD.MAX_TRACES, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
