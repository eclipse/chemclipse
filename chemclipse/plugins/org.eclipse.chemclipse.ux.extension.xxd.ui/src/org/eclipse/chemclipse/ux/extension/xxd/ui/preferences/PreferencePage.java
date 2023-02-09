/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.services.EditorServicesSupport;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("User Interface Settings");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new DirectoryFieldEditor(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER, ExtensionMessages.timeRangePath, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_EDITOR_TSD, ExtensionMessages.editorTSD, EditorServicesSupport.getAvailableEditors(ISupplierFileIdentifier.TYPE_TSD), getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
