/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.chemclipse.support.ui.p2.UpdateSiteSupport;

public class AdditionalUpdateSitesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public AdditionalUpdateSitesPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Additional update sites are required for example to install further plug-ins. A default set of update sites is still added. Sometimes the extended set is needed. But note, that it will slown down the update process.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		/*
		 * Add additional update sites.
		 */
		Button addUpdateSites = new Button(getFieldEditorParent(), SWT.NONE);
		addUpdateSites.setText("Add additional update sites");
		addUpdateSites.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Delete all settings, all installed plug-ins, everything.
				 */
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO | SWT.CANCEL);
				messageBox.setText("Additional update sites");
				messageBox.setMessage("Do you really want to add additional update sites?");
				if(messageBox.open() == SWT.YES) {
					UpdateSiteSupport updateSiteSupport = new UpdateSiteSupport();
					updateSiteSupport.addDefaultProvisioningRepositories();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
