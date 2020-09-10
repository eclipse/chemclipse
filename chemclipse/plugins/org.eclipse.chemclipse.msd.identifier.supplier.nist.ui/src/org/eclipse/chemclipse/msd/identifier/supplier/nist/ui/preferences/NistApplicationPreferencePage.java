/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.preferences;

import java.io.File;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class NistApplicationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private DirectoryFieldEditor pathEditor;

	public NistApplicationPreferencePage() {

		super(GRID);
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, PreferenceSupplier.INSTANCE().getPreferenceNode()));
		setTitle("NIST (extern)");
		setDescription("Here you can configure the location of the NIST-Database Application that should be used by NIST integration modules");
		noDefaultButton();
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		pathEditor = new DirectoryFieldEditor(PreferenceSupplier.P_NIST_APPLICATION, "Nist installation path", getFieldEditorParent()) {

			@Override
			protected boolean doCheckState() {

				String value = pathEditor.getStringValue();
				if(!value.isEmpty()) {
					File location = new File(value);
					IStatus status = PreferenceSupplier.validateLocation(location);
					if(!status.isOK()) {
						setErrorMessage(status.getMessage());
						return false;
					}
				}
				return super.doCheckState();
			}
		};
		pathEditor.setEmptyStringAllowed(true);
		addField(pathEditor);
		/*
		 * MAC OS X - try to define the Wine binary
		 */
		if(OperatingSystemUtils.isMac()) {
			addField(new StringFieldEditor(PreferenceSupplier.P_MAC_WINE_BINARY, "Wine binary (/Applications/Wine.app)", getFieldEditorParent()));
		}
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