/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageUnknownWSD extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String POSTFIX = PreferenceSupplier.POSTFIX_WSD;

	public PreferencePageUnknownWSD() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Unknown Identifier");
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		/*
		 * Display all available import converter.
		 */
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_LIMIT_MATCH_FACTOR_UNKNOWN + POSTFIX, "Limit Match Factor", PreferenceSupplier.MIN_FACTOR, PreferenceSupplier.MAX_FACTOR, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_TARGET_NAME_UNKNOWN + POSTFIX, "Target Name", getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MATCH_QUALITY_UNKNOWN + POSTFIX, "Match Quality", PreferenceSupplier.MIN_FACTOR, PreferenceSupplier.MAX_FACTOR, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_NUMBER_OF_WAVELENGTH_UNKNOWN + POSTFIX, "Number of wavelength", PreferenceSupplier.MIN_NUMBER_OF_WAVELENGTH, PreferenceSupplier.MAX_NUMBER_OF_WAVELENGTH, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_INCLUDE_INTENSITY_PERCENT_UNKNOWN + POSTFIX, "Include Intensity [%]", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_MARKER_START_UNKNOWN + POSTFIX, "Marker Start", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_MARKER_STOP_UNKNOWN + POSTFIX, "Marker Stop", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_INCLUDE_RETENTION_TIME_UNKNOWN + POSTFIX, "Include Retention Time", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_INCLUDE_RETENTION_INDEX_UNKNOWN + POSTFIX, "Include Retention Index", getFieldEditorParent()));
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