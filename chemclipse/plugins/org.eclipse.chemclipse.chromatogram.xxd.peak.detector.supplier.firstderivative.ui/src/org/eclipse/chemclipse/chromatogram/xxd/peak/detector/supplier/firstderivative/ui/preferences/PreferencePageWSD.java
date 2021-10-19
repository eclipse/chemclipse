/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.WindowSizeFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageWSD extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageWSD() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("WSD");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceSupplier.P_THRESHOLD_MSD, "Threshold", Threshold.getOptions(), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_INCLUDE_BACKGROUND_WSD, "Include Background (VV: true, BV|VB: false)", getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_SN_RATIO_WSD, "Min S/N Ratio", 0.0f, Float.MAX_VALUE, getFieldEditorParent()));
		addField(new WindowSizeFieldEditor(PreferenceSupplier.P_MOVING_AVERAGE_WINDOW_SIZE_WSD, "Window Size", getFieldEditorParent()));
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
