/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ScanWebIdentifierUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.TargetWebIdentifierUI;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageTargets extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageTargets() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Targets");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_TARGET_LIST, "Use Target List", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_PROPAGATE_TARGET_ON_UPDATE, "Propagate Target on Update", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_TARGETS_TABLE_SORTABLE, "Table Sortable", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_TARGETS_TABLE_SHOW_DEVIATION_RT, "Show Deviation Retention Time", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_TARGETS_TABLE_SHOW_DEVIATION_RI, "Show Deviation Retention Index", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER, "Library Path", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_TARGET_IDENTIFER, "Target Identifier:", TargetWebIdentifierUI.getTargetIdentifier(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_SCAN_IDENTIFER, "Scan Identifier:", ScanWebIdentifierUI.getScanIdentifier(), getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_ABSOLUTE_DEVIATION_RETENTION_TIME, "Retention Time: Use absolute deviation", getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_RETENTION_TIME_DEVIATION_REL_OK, "Allowed Deviation [%]", PreferenceSupplier.MIN_DEVIATION_RELATIVE, PreferenceSupplier.MAX_DEVIATION_RELATIVE, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_RETENTION_TIME_DEVIATION_REL_WARN, "Warn Deviation [%]", PreferenceSupplier.MIN_DEVIATION_RELATIVE, PreferenceSupplier.MAX_DEVIATION_RELATIVE, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_RETENTION_TIME_DEVIATION_ABS_OK, "Allowed Deviation [ms]", PreferenceSupplier.MIN_DEVIATION_RETENTION_TIME, PreferenceSupplier.MAX_DEVIATION_RETENTION_TIME, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_RETENTION_TIME_DEVIATION_ABS_WARN, "Warn Deviation [ms]", PreferenceSupplier.MIN_DEVIATION_RETENTION_TIME, PreferenceSupplier.MAX_DEVIATION_RETENTION_TIME, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_ABSOLUTE_DEVIATION_RETENTION_INDEX, "Retention Index: Use absolute deviation", getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_RETENTION_INDEX_DEVIATION_REL_OK, "Allowed Deviation [%]", PreferenceSupplier.MIN_DEVIATION_RELATIVE, PreferenceSupplier.MAX_DEVIATION_RELATIVE, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_RETENTION_INDEX_DEVIATION_REL_WARN, "Warn Deviation [%]", PreferenceSupplier.MIN_DEVIATION_RELATIVE, PreferenceSupplier.MAX_DEVIATION_RELATIVE, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_RETENTION_INDEX_DEVIATION_ABS_OK, "Allowed Deviation [abs]", PreferenceSupplier.MIN_DEVIATION_RETENTION_INDEX, PreferenceSupplier.MAX_DEVIATION_RETENTION_INDEX, getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_RETENTION_INDEX_DEVIATION_ABS_WARN, "Warn Deviation [abs]", PreferenceSupplier.MIN_DEVIATION_RETENTION_INDEX, PreferenceSupplier.MAX_DEVIATION_RETENTION_INDEX, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_ADD_UNKNOWN_AFTER_DELETE_TARGETS_ALL, "Delete All (Unknown Target Add)", getFieldEditorParent()));
		addField(new FloatFieldEditor(PreferenceSupplier.P_MATCH_QUALITY_UNKNOWN_TARGET, "Match Quality (Unknown Target)", PreferenceSupplier.MIN_MATCH_QUALITY, PreferenceSupplier.MAX_MATCH_QUALITY, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_UNKNOWN_TARGET_ADD_RETENTION_INDEX, "Add Retention Index (Unknown Target)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_VERIFY_UNKNOWN_TARGET, "Verify (Unknown Target)", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_ACTIVATE_TARGET_DND_WINDOWS, "Activate Target Drag and Drop for Windows (experimental)", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
