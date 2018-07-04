/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Florian Ernst - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.ui.preferences;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditorBounded;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Deconvolution peak detector");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		// String[][] options = new String[][]{{"&OFF", Sensitivity.OFF.toString()}, {"&LOW", Sensitivity.LOW.toString()}, {"&MEDIUM", Sensitivity.MEDIUM.toString()}, {"&HIGH", Sensitivity.HIGH.toString()}};
		// addField(new RadioGroupFieldEditor(PreferenceSupplier.P_SENSITIVITY, "Set a sensitivity", 1, options, getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_MIN_SNR, "Minimum signal noise ratio", 0.0d, Double.MAX_VALUE, getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_MIN_PEAKWIDTH, "Minimal peak width (Scans)", getFieldEditorParent()));
		addField(new SpinnerFieldEditorBounded(PreferenceSupplier.P_MIN_PEAKRISING, "Minimal peak rising (Scans)", 1, 5, getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Advanced Settings:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_NOISE_SEGMENTS, "Quantity of noise segments", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_SNIP_ITERATIONS, "Set baseline iterations (SNIP)", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_SENS_DECONVOLUTIONS, "Set Sensitivity of Deconv", getFieldEditorParent()));
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
