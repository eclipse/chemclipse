/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.ui.preferences;

import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.ui.Activator;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageCSD extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageCSD() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("CSD");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		String[][] options = new String[][]{{"&OFF", Threshold.OFF.toString()}, {"&LOW", Threshold.LOW.toString()}, {"&MEDIUM", Threshold.MEDIUM.toString()}, {"&HIGH", Threshold.HIGH.toString()}};
		addField(new RadioGroupFieldEditor(PreferenceSupplier.P_THRESHOLD_CSD, "Set a threshold level", 1, options, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_INCLUDE_BACKGROUND_CSD, "Selected: Use VV - Deselected: Use BV or VB", getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_SN_RATIO_CSD, "Minimum S/N ratio (0 = add all peaks)", 0.0f, Float.MAX_VALUE, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_MOVING_AVERAGE_WINDOW_SIZE_CSD, "Moving average window size", WindowSize.getElements(), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_NOISE_SEGMENTS_CSD, "Use Noise-Segments", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_OPTIMIZE_BASELINE_CSD, "Optimize Baseline (VV)", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
