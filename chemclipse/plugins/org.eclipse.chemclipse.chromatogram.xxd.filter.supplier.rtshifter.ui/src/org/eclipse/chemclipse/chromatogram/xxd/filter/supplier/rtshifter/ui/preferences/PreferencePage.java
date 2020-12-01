/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.ShiftDirection;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.RetentionTimeMinutesFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Retention Time Shifter/Offset");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHIFT_ALL_SCANS, "Shift all scans", getFieldEditorParent()));
		//
		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_MILLISECONDS_BACKWARD, "Offset Backward (minutes)", PreferenceSupplier.MIN_RETENTION_TIME, PreferenceSupplier.MAX_RETENTION_TIME, getFieldEditorParent()));
		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_MILLISECONDS_FAST_BACKWARD, "Offset Fast Backward (minutes)", PreferenceSupplier.MIN_RETENTION_TIME, PreferenceSupplier.MAX_RETENTION_TIME, getFieldEditorParent()));
		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_MILLISECONDS_FORWARD, "Offset Forward (minutes)", PreferenceSupplier.MIN_RETENTION_TIME, PreferenceSupplier.MAX_RETENTION_TIME, getFieldEditorParent()));
		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_MILLISECONDS_FAST_FORWARD, "Offset Fast Forward (minutes)", PreferenceSupplier.MIN_RETENTION_TIME, PreferenceSupplier.MAX_RETENTION_TIME, getFieldEditorParent()));
		/*
		 * Default Shift Direction (in case of batch processing.
		 */
		addField(new ComboFieldEditor(PreferenceSupplier.P_DEFAULT_SHIFT_DIRECTION, "Default Shift Direction", ShiftDirection.getElements(), getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_OVERLAY_X_OFFSET, "Retention time offset (minutes):", PreferenceSupplier.MIN_X_OFFSET, PreferenceSupplier.MAX_RETENTION_TIME, getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_OVERLAY_Y_OFFSET, "Abundance offset:", getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_OFFSET_STEP_UP, "Offset step up (abundance):", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_OFFSET_STEP_DOWN, "Offset step down (abundance):", getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_IS_LOCK_OFFSET, "Lock Offset", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_STRETCH_MILLISECONDS_SCAN_DELAY, "Stretch Scan Delay (minutes)", PreferenceSupplier.STRETCH_MILLISECONDS_SCAN_DELAY_MIN, PreferenceSupplier.STRETCH_MILLISECONDS_SCAN_DELAY_MAX, getFieldEditorParent()));
		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_STRETCH_MILLISECONDS_LENGTH, "Stretch Length (minutes)", PreferenceSupplier.STRETCH_MILLISECONDS_LENGTH_MIN, PreferenceSupplier.STRETCH_MILLISECONDS_LENGTH_MAX, getFieldEditorParent()));
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
