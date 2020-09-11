/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.swt.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageSystem extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageSystem() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("System Settings");
		setDescription("");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {

		/*
		 * Use this for the new UI and data analysis perspective instead of SWTPreferencePage.
		 */
		addField(new BooleanFieldEditor(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION, "Alternate window move direction.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS, "Condense Scans with Cycle Number", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, "Show retention index without decimals", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS, "Show area without decimals", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SORT_CASE_SENSITIVE, "Sort Case Sensitive", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SEARCH_CASE_SENSITIVE, "Search: Case sensitive", getFieldEditorParent()));
	}
}
