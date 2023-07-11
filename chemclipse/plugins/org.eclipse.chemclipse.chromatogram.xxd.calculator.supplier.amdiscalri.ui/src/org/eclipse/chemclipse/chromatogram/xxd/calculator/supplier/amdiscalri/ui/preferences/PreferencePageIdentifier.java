/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageIdentifier extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageIdentifier() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Alkanes");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_NUMBER_OF_TARGETS, "Number of Targets", PreferenceSupplier.MIN_NUMBER_OF_TARGETS, PreferenceSupplier.MAX_NUMBER_OF_TARGETS, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_MATCH_FACTOR, "Min Match Factor", PreferenceSupplier.MIN_MIN_MATCH_FACTOR, PreferenceSupplier.MAX_MIN_MATCH_FACTOR, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_REVERSE_MATCH_FACTOR, "Min Reverse Match Factor", PreferenceSupplier.MIN_MIN_REVERSE_MATCH_FACTOR, PreferenceSupplier.MAX_MIN_REVERSE_MATCH_FACTOR, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}