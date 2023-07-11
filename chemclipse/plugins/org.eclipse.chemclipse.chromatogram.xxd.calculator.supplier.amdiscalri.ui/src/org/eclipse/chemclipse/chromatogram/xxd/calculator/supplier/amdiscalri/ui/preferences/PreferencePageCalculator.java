/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.editors.CalibrationTableEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageCalculator extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageCalculator() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Calculator");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		CalibrationTableEditor calibrationTableEditor = new CalibrationTableEditor(PreferenceSupplier.P_RETENTION_INDEX_FILES, "Retention Index Files:", getFieldEditorParent());
		calibrationTableEditor.setFilterExtensionsAndNames(new String[]{"*.cal;*.CAL"}, new String[]{"AMDIS Calibration (*.cal)"});
		addField(calibrationTableEditor);
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Calculator Options", getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(PreferenceSupplier.P_CALCULATOR_STRATEGY, "Calculator Strategy", 1, PreferenceSupplier.CALCULATOR_OPTIONS, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_DEFAULT_COLUMN, "Use Default Column (in case of no match)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_PROCESS_REFERENCED_CHROMATOGRAMS, "Process Referenced Chromatograms", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}