/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.preferences;

import java.io.File;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;

public class SettingsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public SettingsPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {

		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Settings");
	}

	@Override
	protected void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		Button resetSettings = new Button(getFieldEditorParent(), SWT.NONE);
		resetSettings.setText("Reset Application Settings");
		resetSettings.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Delete all settings, all installed plug-ins, everything.
				 */
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
				messageBox.setText("Reset?");
				messageBox.setMessage("Do you really want to reset the application? Please backup your files, databases ... they will be deleted using this option.");
				if(messageBox.open() == SWT.YES) {
					File settingsDirectory = ApplicationSettings.getSettingsDirectory();
					deleteFiles(new File[]{settingsDirectory});
					PlatformUI.getWorkbench().restart();
				}
			}
		});
	}

	/**
	 * Delete all files.
	 * 
	 * @param files
	 */
	private void deleteFiles(File[] files) {

		/*
		 * Iterate through all files.
		 */
		for(File file : files) {
			/*
			 * If the file is a directory, delete its content and afterwards the directory itself.
			 */
			if(file.isDirectory()) {
				deleteFiles(file.listFiles());
				file.delete();
			} else {
				file.delete();
			}
		}
	}
}
