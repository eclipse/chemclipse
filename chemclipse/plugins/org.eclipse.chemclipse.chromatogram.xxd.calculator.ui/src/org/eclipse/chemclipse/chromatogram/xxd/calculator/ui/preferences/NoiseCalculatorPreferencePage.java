/*******************************************************************************
 * Copyright (c) 2014, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - removed enums
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SegmentSizeFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class NoiseCalculatorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public NoiseCalculatorPreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("S/N Calculator Settings");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_NOISE_CALCULATOR_ID, "Calculator Method", NoiseCalculator.getAvailableCalculatorIds(), getFieldEditorParent()));
		addField(new SegmentSizeFieldEditor(PreferenceSupplier.P_SEGMENT_WIDTH, "Segment width to determine noise", getFieldEditorParent()));
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
