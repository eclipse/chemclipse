/*******************************************************************************
 * Copyright (c) 2015, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.ui.preferences;

import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.ui.Activator;
import org.eclipse.chemclipse.model.math.IonRoundMethod;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FilterPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public FilterPreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Filter");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Chromatogram Selection Filter", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_START_RETENTION_TIME_MINUTES, "Start Retention Time (Minutes)", PreferenceSupplier.MIN_RETENTION_TIME_MINUTES, PreferenceSupplier.MAX_RETENTION_TIME_MINUTES, getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_STOP_RETENTION_TIME_MINUTES, "Stop Retention Time (Minutes)", PreferenceSupplier.MIN_RETENTION_TIME_MINUTES, PreferenceSupplier.MAX_RETENTION_TIME_MINUTES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Scan Targets -> Peak Transfer", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_STTP_TRANSFER_CLOSEST_SCAN, "Transfer Closest Scan", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_STTP_USE_BEST_TARGET_ONLY, "Use Best Target Only", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Peak Targets -> Reference Chromatograms Transfer", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_PTTR_USE_BEST_TARGET_ONLY, "Use Best Target Only", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_DELTA_RETENTION_TIME_MINUTES, "Delta Retention Time (Minutes)", PreferenceSupplier.MIN_RETENTION_TIME_MINUTES, PreferenceSupplier.MAX_RETENTION_TIME_MINUTES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Scan Targets -> Reference Chromatograms Transfer", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_STTR_USE_BEST_TARGET_ONLY, "Use Best Target Only", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_RETENTION_INDEX_QC, "QC: Use Retention Index (Processor)", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_ION_ROUND_METHOD, "Ion Round Method", IonRoundMethod.getOptions(), getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Scan Maxima Detector UI", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_MAX_DETECTOR_TARGET_NAME, "Target Name", getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MAX_DETECTOR_MATCH_FACTOR, "Match Factor", PreferenceSupplier.MIN_FACTOR, PreferenceSupplier.MAX_FACTOR, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_MAX_DETECTOR_MINIMA, "Detect Minima", getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_MAX_DETECTOR_COUNT, "Count (0 = All)", PreferenceSupplier.MIN_COUNT_MARKER, PreferenceSupplier.MAX_COUNT_MARKER, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
