/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.ui.preferences;

import org.eclipse.chemclipse.model.settings.Delimiter;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.ui.Activator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.DecimalSeparator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.ChannelMappingFieldEditor;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.VirtualChannelFieldEditor;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.editors.WellMappingFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Exports plates into *.csv reports according to these rules.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new ChannelMappingFieldEditor(PreferenceSupplier.P_CHANNEL_MAPPING, "Channel Mappings:", getFieldEditorParent()));
		addField(new WellMappingFieldEditor(PreferenceSupplier.P_WELL_MAPPING, "Well Mappings:", getFieldEditorParent()));
		addField(new VirtualChannelFieldEditor(PreferenceSupplier.P_VIRTUAL_CHANNELS, "Virtual Channels:", getFieldEditorParent()));
		addField(new LabelFieldEditor("*.csv Options", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_DELIMITER, "Delimiter Character: ", Delimiter.getOptions(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_DECIMAL_SEPARATOR, "Decimal Separator: ", DecimalSeparator.getOptions(), getFieldEditorParent()));
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
