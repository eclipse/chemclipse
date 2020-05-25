/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.ui.Activator;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("SNIP Detector/Filter");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		IntegerFieldEditor iterationsFieldEditor = new IntegerFieldEditor(PreferenceSupplier.P_ITERATIONS, "Iterations", getFieldEditorParent());
		iterationsFieldEditor.setValidRange(PreferenceSupplier.MIN_ITERATIONS, PreferenceSupplier.MAX_ITERATIONS);
		addField(iterationsFieldEditor);
		addField(new ComboFieldEditor(PreferenceSupplier.P_WINDOW_SIZE, "Window size (Detector)", WindowSize.getElements(), getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_MAGNIFICATION_FACTOR, "Magnification factor (Filter)", PreferenceSupplier.MIN_MAGNIFICATION_FACTOR, PreferenceSupplier.MAX_MAGNIFICATION_FACTOR, getFieldEditorParent()));
		IntegerFieldEditor transitionsFieldEditor = new IntegerFieldEditor(PreferenceSupplier.P_TRANSITIONS, "Transitions (Filter)", getFieldEditorParent());
		transitionsFieldEditor.setValidRange(PreferenceSupplier.MIN_TRANSITIONS, PreferenceSupplier.MAX_TRANSITIONS);
		addField(transitionsFieldEditor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
