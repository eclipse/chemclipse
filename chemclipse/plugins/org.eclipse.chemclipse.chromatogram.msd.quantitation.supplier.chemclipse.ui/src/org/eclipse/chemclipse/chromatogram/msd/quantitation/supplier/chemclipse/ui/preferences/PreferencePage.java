/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.preferences;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.Activator;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.preferences.AbstractCustomQuantitationPreferencePage;
import org.eclipse.chemclipse.database.model.IDatabases;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;
import org.eclipse.chemclipse.database.ui.fieldeditors.DatabaseFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.PasswordFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

public class PreferencePage extends AbstractCustomQuantitationPreferencePage {

	public PreferencePage() {
		super(Activator.getDefault().getPreferenceStore());
		setDescription("Quantitation Support.");
	}

	@Override
	public void createSettingPages() {

		/*
		 * Overview
		 */
		addField(new LabelFieldEditor("The quantitation support allows to add / use different quantitation tables.", getFieldEditorOverview()));
		addField(new LabelFieldEditor("Please edit the tables carefully.", getFieldEditorOverview()));
		/*
		 * Database Selection
		 */
		IDatabases databases = new QuantDatabases();
		PreferenceSupplier peakQuantifierPreferences = new PreferenceSupplier();
		addField(new RadioGroupFieldEditor(PreferenceSupplier.P_USE_DATABASE_CONNECTION, "Use Database", 1, DatabasePathHelper.DATABASE_CONNECTIONS, getFieldEditorDatabaseSelection()));
		addField(new SpacerFieldEditor(getFieldEditorDatabaseSelection()));
		addField(new SpacerFieldEditor(getFieldEditorDatabaseSelection()));
		addField(new DatabaseFieldEditor(PreferenceSupplier.P_SELECTED_DATABASE, "Selected Database", databases, peakQuantifierPreferences, "Quantitation", getFieldEditorDatabaseSelection()));
		addField(new SpacerFieldEditor(getFieldEditorDatabaseSelection()));
		addField(new SpacerFieldEditor(getFieldEditorDatabaseSelection()));
		addField(new StringFieldEditor(PreferenceSupplier.P_SELECTED_SERVER_REMOTE, "Remote Server", getFieldEditorDatabaseSelection()));
		addField(new StringFieldEditor(PreferenceSupplier.P_SELECTED_USER_REMOTE, "Remote User", getFieldEditorDatabaseSelection()));
		addField(new PasswordFieldEditor(PreferenceSupplier.P_SELECTED_PASSWORD_REMOTE, "Remote Password", getFieldEditorDatabaseSelection()));
		/*
		 * Identifier Settings
		 * getFieldEditorIdentificationSettings()
		 */
	}
}
