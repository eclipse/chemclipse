/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.ui.Activator;

public class FilterPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public FilterPreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the Backfolding filter settings.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		/*
		 * Backfolding runs.
		 */
		IntegerFieldEditor backfoldingRunsEditor = new IntegerFieldEditor(PreferenceSupplier.P_BACKFOLDING_RUNS, "Backfolding Runs", getFieldEditorParent());
		backfoldingRunsEditor.setValidRange(PreferenceSupplier.MIN_BACKFOLDING_RUNS, PreferenceSupplier.MAX_BACKFOLDING_RUNS);
		addField(backfoldingRunsEditor);
		/*
		 * Retention time shift.
		 */
		IntegerFieldEditor maxRetentionTimeShiftEditor = new IntegerFieldEditor(PreferenceSupplier.P_MAX_RETENTION_TIME_SHIFT, "Max Retention Time Shift (Milliseconds)", getFieldEditorParent());
		maxRetentionTimeShiftEditor.setValidRange(PreferenceSupplier.MIN_RETENTION_TIME_SHIFT, PreferenceSupplier.MAX_RETENTION_TIME_SHIFT);
		addField(maxRetentionTimeShiftEditor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
