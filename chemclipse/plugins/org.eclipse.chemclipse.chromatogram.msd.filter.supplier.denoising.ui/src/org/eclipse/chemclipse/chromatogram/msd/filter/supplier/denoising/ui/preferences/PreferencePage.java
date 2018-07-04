/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.preferences;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.editors.IonTableEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditorBounded;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditorOddNumber;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the denoising filter settings.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new IonTableEditor(PreferenceSupplier.P_IONS_TO_REMOVE, "Ions to remove in all cases", getFieldEditorParent()));
		addField(new IonTableEditor(PreferenceSupplier.P_IONS_TO_PRESERVE, "Ions to preserve in all cases", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_ADJUST_THRESHOLD_TRANSITIONS, "Adjust threshold transitions.", getFieldEditorParent()));
		addField(new SpinnerFieldEditorBounded(PreferenceSupplier.P_NUMBER_OF_USE_IONS_FOR_COEFFICIENT, "Number of use ions for coefficient", PreferenceSupplier.NUMBER_OF_USE_IONS_FOR_COEFFICIENT_MIN, PreferenceSupplier.NUMBER_OF_USE_IONS_FOR_COEFFICIENT_MAX, getFieldEditorParent()));
		addField(new SpinnerFieldEditorOddNumber(PreferenceSupplier.P_SEGMENT_WIDTH, "Segment width to determine noise.", PreferenceSupplier.SEGMENT_WIDTH_MIN, PreferenceSupplier.SEGMENT_WIDTH_MAX, getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
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
