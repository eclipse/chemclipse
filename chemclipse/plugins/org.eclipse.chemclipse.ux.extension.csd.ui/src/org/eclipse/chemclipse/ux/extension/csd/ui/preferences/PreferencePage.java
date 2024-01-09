/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.RetentionTimeMinutesFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.csd.ui.Activator;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Current Selective Detector (MSD)");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new RetentionTimeMinutesFieldEditor(PreferenceSupplier.P_OVERLAY_X_OFFSET, "Retention time offset (minutes):", PreferenceSupplier.MIN_X_OFFSET, PreferenceSupplier.MAX_X_OFFSET, getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceSupplier.P_OVERLAY_Y_OFFSET, "Abundance offset:", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_PATH_OPEN_CHROMATOGRAMS, "Path Chromatograms", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
