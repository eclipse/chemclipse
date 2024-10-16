/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Lorenz Gerber - data type specific settings
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditorBounded;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditorOddNumber;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageMSD extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageMSD() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Savitzky-Golay Smoothing");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new SpinnerFieldEditorBounded(PreferenceSupplier.P_ORDER, "Order", PreferenceSupplier.MIN_ORDER, PreferenceSupplier.MAX_ORDER, getFieldEditorParent()));
		addField(new SpinnerFieldEditorOddNumber(PreferenceSupplier.P_WIDTH, "Width", PreferenceSupplier.MIN_WIDTH, PreferenceSupplier.MAX_WIDTH, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_PER_ION_CALCULATION, "Calculate Filter per Ion Channel.", getFieldEditorParent()));
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