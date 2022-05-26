/*******************************************************************************
 * Copyright (c) 2014, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.ui.editors.IdentifierTableEditor;
import org.eclipse.chemclipse.model.identifier.DeltaCalculation;
import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculation;
import org.eclipse.chemclipse.msd.model.support.CalculationType;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageFile extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageFile() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("File Identifier");
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
		addField(createDatabaseFieldEditor(getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_LIMIT_MATCH_FACTOR_FILE, "Limit Match Factor", PreferenceSupplier.MIN_FACTOR, PreferenceSupplier.MAX_FACTOR, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_PRE_OPTIMIZATION, "Use search pre-optimization", getFieldEditorParent()));
		String labelTextThreshold = getDescription("Threshold pre-optimization", PreferenceSupplier.MIN_THRESHOLD_PRE_OPTIMIZATION, PreferenceSupplier.MAX_THRESHOLD_PRE_OPTIMIZATION);
		addField(new DoubleFieldEditor(PreferenceSupplier.P_THRESHOLD_PRE_OPTIMIZATION, labelTextThreshold, PreferenceSupplier.MIN_THRESHOLD_PRE_OPTIMIZATION, PreferenceSupplier.MAX_THRESHOLD_PRE_OPTIMIZATION, getFieldEditorParent()));
		//
		addField(new ComboFieldEditor(PreferenceSupplier.P_MASS_SPECTRUM_COMPARATOR_ID, "Mass Spectrum Comparator", MassSpectrumComparator.getAvailableComparatorIds(), getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_NUMBER_OF_TARGETS, "Number of Targets", PreferenceSupplier.MIN_NUMBER_OF_TARGETS, PreferenceSupplier.MAX_NUMBER_OF_TARGETS, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_MATCH_FACTOR, "Min Match Factor", PreferenceSupplier.MIN_FACTOR, PreferenceSupplier.MAX_FACTOR, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_REVERSE_MATCH_FACTOR, "Min Reverse Match Factor", PreferenceSupplier.MIN_FACTOR, PreferenceSupplier.MAX_FACTOR, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_DELTA_CALCULATION, "Delta Calculation", DeltaCalculation.getOptions(), getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_DELTA_WINDOW, "Delta Window", IIdentifierSettings.MIN_DELTA_WINDOW, IIdentifierSettings.MAX_DELTA_WINDOW, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_PENALTY_CALCULATION, "Penalty Calculation", PenaltyCalculation.getOptions(), getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_PENALTY_WINDOW, "Penalty Window", IIdentifierSettings.MIN_PENALTY_WINDOW, IIdentifierSettings.MAX_PENALTY_WINDOW, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_PENALTY_LEVEL_FACTOR, "Penalty Level Factor", IIdentifierSettings.MIN_PENALTY_LEVEL_FACTOR, IIdentifierSettings.MAX_PENALTY_LEVEL_FACTOR, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MAX_PENALTY, "Max Penalty", IIdentifierSettings.MIN_PENALTY_MATCH_FACTOR, IIdentifierSettings.MAX_PENALTY_MATCH_FACTOR, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_NORMALIZED_SCAN, "Use normalized scan", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_CALCULATION_TYPE, "Calculation Type", CalculationType.getOptions(), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_PEAKS_INSTEAD_OF_SCANS, "Use peaks instead of scans", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Used locations for library files", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_FILTER_PATH_IDENTIFIER_FILES, "Path Identifier Files:", getFieldEditorParent()));
	}

	private IdentifierTableEditor createDatabaseFieldEditor(Composite parent) {

		return new IdentifierTableEditor(PreferenceSupplier.P_MASS_SPECTRA_FILES, "Mass Spectrum Libraries", parent);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {

	}

	private String getDescription(String info, double minValue, double maxValue) {

		StringBuilder builder = new StringBuilder();
		builder.append(info);
		builder.append(" (");
		builder.append((int)minValue);
		builder.append(" - ");
		builder.append((int)maxValue);
		builder.append(")");
		return builder.toString();
	}
}