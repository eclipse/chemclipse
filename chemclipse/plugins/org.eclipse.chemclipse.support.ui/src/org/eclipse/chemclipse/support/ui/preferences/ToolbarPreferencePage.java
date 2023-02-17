/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences;

import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ToolbarPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private String keyShowText;

	public ToolbarPreferencePage(IPreferenceStore preferenceStore, String keyShowText) {
		super(GRID);
		setTitle(SupportMessages.toolbar);
		this.keyShowText = keyShowText;
		setPreferenceStore(preferenceStore);
		noDefaultAndApplyButton();
	}

	@Override
	public void createFieldEditors() {

		addField(new BooleanFieldEditor(keyShowText, SupportMessages.showLabelsInToolbar, getFieldEditorParent()));
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
