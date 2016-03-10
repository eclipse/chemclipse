/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.preferences;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.Activator;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverterSupport;
import org.eclipse.chemclipse.support.ui.preferences.editors.FileListEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final Logger logger = Logger.getLogger(PreferencePage.class);

	public PreferencePage() {
		super(FLAT);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("File Identifier Settings.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		/*
		 * Display all available import converter.
		 */
		FileListEditor fileListEditor = new FileListEditor(PreferenceSupplier.P_MASS_SPECTRA_FILES, "Load mass spectrum libraries", getFieldEditorParent());
		MassSpectrumConverterSupport massSpectrumConverterSupport = MassSpectrumConverter.getMassSpectrumConverterSupport();
		try {
			String[] extensions = massSpectrumConverterSupport.getFilterExtensions();
			String[] names = massSpectrumConverterSupport.getFilterNames();
			if(extensions.length == names.length) {
				String[] filterExtensions = new String[extensions.length + 1];
				String[] filterNames = new String[extensions.length + 1];
				//
				filterExtensions[0] = "*.*";
				filterNames[0] = "All files";
				//
				for(int i = 0; i < extensions.length; i++) {
					filterExtensions[i + 1] = extensions[i].replace(".", "*.");
					filterNames[i + 1] = names[i];
				}
				fileListEditor.setFilterExtensionsAndNames(filterExtensions, filterNames);
			}
		} catch(NoConverterAvailableException e) {
			logger.warn(e);
		}
		addField(fileListEditor);
		//
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_PRE_OPTIMIZATION, "Use search pre-optimization", getFieldEditorParent()));
		String labelTextThreshold = getDescription("Threshold pre-optimization", PreferenceSupplier.MIN_THRESHOLD_PRE_OPTIMIZATION, PreferenceSupplier.MAX_THRESHOLD_PRE_OPTIMIZATION);
		addField(new DoubleFieldEditor(PreferenceSupplier.P_THRESHOLD_PRE_OPTIMIZATION, labelTextThreshold, PreferenceSupplier.MIN_THRESHOLD_PRE_OPTIMIZATION, PreferenceSupplier.MAX_THRESHOLD_PRE_OPTIMIZATION, getFieldEditorParent()));
		//
		addField(new ComboFieldEditor(PreferenceSupplier.P_MASS_SPECTRUM_COMPARATOR_ID, "Mass Spectrum Comparator Id", MassSpectrumComparator.getAvailableComparatorIds(), getFieldEditorParent()));
		String labelTextNumberOfTargets = getDescription("Number of Targets", PreferenceSupplier.MIN_NUMBER_OF_TARGETS, PreferenceSupplier.MAX_NUMBER_OF_TARGETS);
		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(PreferenceSupplier.P_NUMBER_OF_TARGETS, labelTextNumberOfTargets, getFieldEditorParent(), 3);
		integerFieldEditor.setValidRange(PreferenceSupplier.MIN_NUMBER_OF_TARGETS, PreferenceSupplier.MAX_NUMBER_OF_TARGETS);
		addField(integerFieldEditor);
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_MATCH_FACTOR, "Min Match Factor", 0.0f, 100.0f, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MIN_REVERSE_MATCH_FACTOR, "Min Reverse Match Factor", 0.0f, 100.0f, getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_ADD_UNKNOWN_MZ_LIST_TARGET, "Add m/z list of unknown if no match is available", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
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
