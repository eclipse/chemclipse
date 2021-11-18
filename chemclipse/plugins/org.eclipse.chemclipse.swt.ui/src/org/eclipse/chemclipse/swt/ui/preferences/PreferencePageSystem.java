/*******************************************************************************
 * Copyright (c) 2017, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.math.IonRoundMethod;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.targets.LibraryField;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.swt.ui.Activator;
import org.eclipse.chemclipse.swt.ui.fieldeditors.ColumnMappingFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageSystem extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public PreferencePageSystem() {

		super(GRID);
		setPreferenceStore(preferenceStore);
		setTitle("System");
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
		addField(new BooleanFieldEditor(PreferenceSupplier.P_ALTERNATE_WINDOW_MOVE_DIRECTION, "Alternate window move direction.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CONDENSE_CYCLE_NUMBER_SCANS, "Condense Scans with Cycle Number", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, "Show retention index without decimals", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_AREA_WITHOUT_DECIMALS, "Show area without decimals", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SORT_CASE_SENSITIVE, "Sort: Case Sensitive", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SEARCH_CASE_SENSITIVE, "Search: Case sensitive", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_RETENTION_INDEX_QC, "QC: Use Retention Index", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_BEST_TARGET_LIBRARY_FIELD, "Best Target", LibraryField.getOptions(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_ION_ROUND_METHOD, "Ion Round Method", IonRoundMethod.getOptions(), getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_LIST_PATH_IMPORT, "Mappings Import Folder", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_LIST_PATH_EXPORT, "Mappings Export Folder", getFieldEditorParent()));
		addField(new ColumnMappingFieldEditor(PreferenceSupplier.P_SEPARATION_COLUMN_MAPPINGS, "Separation Column Mappings", getFieldEditorParent()));
		//
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {

				if(event.getProperty().equals(PreferenceSupplier.P_ION_ROUND_METHOD)) {
					System.out.println("CLEAR");
					PreferenceSupplier.clearCacheActiveIonRoundMethod();
				}
			}
		});
	}
}
