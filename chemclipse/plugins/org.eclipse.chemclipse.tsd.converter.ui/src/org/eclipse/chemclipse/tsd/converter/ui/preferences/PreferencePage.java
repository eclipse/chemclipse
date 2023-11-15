/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.tsd.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.tsd.converter.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("TSD Converter");
		setDescription("");
	}

	@Override
	protected void createFieldEditors() {

		addField(new LabelFieldEditor("Adapter MSD", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_ADAPTER_MSD, "Use Adapter", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_ADAPTER_FIXED_RANGE_MSD, "Use Fixed Range (Traces)", getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_ADAPTER_MIN_TRACE_MSD, "Min Trace (m/z)", PreferenceSupplier.MIN_TRACE_MSD, PreferenceSupplier.MAX_TRACE_MSD, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_ADAPTER_MAX_TRACE_MSD, "Max Trace (m/z)", PreferenceSupplier.MIN_TRACE_MSD, PreferenceSupplier.MAX_TRACE_MSD, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Adapter WSD", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_ADAPTER_WSD, "Use Adapter", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_ADAPTER_FIXED_RANGE_WSD, "Use Fixed Range (Traces)", getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_ADAPTER_MIN_TRACE_WSD, "Min Trace (Wavelength)", PreferenceSupplier.MIN_TRACE_WSD, PreferenceSupplier.MAX_TRACE_WSD, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_ADAPTER_MAX_TRACE_WSD, "Max Trace (Wavelength)", PreferenceSupplier.MIN_TRACE_WSD, PreferenceSupplier.MAX_TRACE_WSD, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}