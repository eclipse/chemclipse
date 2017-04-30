/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageEvaluation extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageEvaluation() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Single Chromatogram Evaluation");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_MSD_FILTER, "Chromatogram Filter MSD:", PreferenceSupplier.getChromatogramFilterMSD(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_FILTER, "Chromatogram Filter:", PreferenceSupplier.getChromatogramFilter(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_EVALUATION_BASELINE_DETECTOR, "Baseline Detector:", PreferenceSupplier.getBaselineDetectors(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_EVALUATION_PEAK_DETECTOR, "Peak Detector:", PreferenceSupplier.getPeakDetectors(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_EVALUATION_PEAK_INTEGRATOR, "Peak Integrator:", PreferenceSupplier.getPeakIntegrators(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_CALCULATOR, "Chromatogram Calculator:", PreferenceSupplier.getChromatogramCalculators(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_EVALUATION_PEAK_IDENTIFIER, "Peak Identifier:", PreferenceSupplier.getPeakIdentifier(), getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
