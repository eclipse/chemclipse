/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.preferences;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.app.profiles.Profiles;
import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final Logger logger = Logger.getLogger(PreferencePage.class);
	private Label selectedProfileFieldEditor;
	private Combo availableProfilesCombo;
	private Text newProfileNameText;

	public PreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		/*
		 * Change perspectives dialog
		 */
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_PERSPECTIVE_DIALOG, "Show the perspective dialog.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHANGE_PERSPECTIVE_AUTOMATICALLY, "Change perspectives and views automatically.", getFieldEditorParent()));
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		/*
		 * Profiles
		 */
		Composite composite = new Composite(getFieldEditorParent(), SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		/*
		 * Name
		 */
		Label label = new Label(composite, SWT.NONE);
		label.setText("Selected Profile:");
		//
		selectedProfileFieldEditor = new Label(composite, SWT.NONE);
		selectedProfileFieldEditor.setText(getPreferenceStore().getString(PreferenceSupplier.P_SELECTED_PROFILE));
		selectedProfileFieldEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * Delete selected profile
		 */
		Button deleteProfile = new Button(composite, SWT.NONE);
		deleteProfile.setText("Delete selected profile");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		deleteProfile.setLayoutData(gridData);
		deleteProfile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String name = selectedProfileFieldEditor.getText().trim();
				if(name == null || name.equals("")) {
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
					messageBox.setText("Delete profile");
					messageBox.setMessage("There is no profile selected.");
					messageBox.open();
				} else {
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
					messageBox.setText("Delete profile?");
					messageBox.setMessage("Do you really want to delete the profile: " + name);
					if(messageBox.open() == SWT.YES) {
						/*
						 * Delete
						 */
						Profiles.deleteProfile(name);
						setProfileName("");
					}
				}
			}
		});
		/*
		 * Load Profile
		 */
		availableProfilesCombo = EnhancedCombo.create(composite, SWT.NONE);
		availableProfilesCombo.setItems(getProfileNames());
		availableProfilesCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button loadProfile = new Button(composite, SWT.NONE);
		loadProfile.setText("Load profile");
		loadProfile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		loadProfile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String name = availableProfilesCombo.getText().trim();
				if(name == null || name.equals("")) {
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
					messageBox.setText("Load profile");
					messageBox.setMessage("Please select a profile to load.");
					messageBox.open();
				} else {
					try {
						/*
						 * Load the profile.
						 */
						Profiles.loadProfile(name);
						setProfileName(name);
						IPreferencePageContainer preferencePageContainer = getContainer();
						if(preferencePageContainer instanceof PreferenceDialog preferenceDialog) {
							/*
							 * Refresh all preference pages.
							 * Close the current dialog and re-open it to load all
							 * values correctly.
							 */
							preferenceDialog.close();
							preferenceDialog = PreferencesUtil.createPreferenceDialogOn(null, null, null, null);
							preferenceDialog.open();
						}
					} catch(Exception e1) {
						logger.warn(e1);
					}
				}
			}
		});
		/*
		 * Save selected settings as profile
		 */
		newProfileNameText = new Text(composite, SWT.BORDER);
		newProfileNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button saveProfile = new Button(composite, SWT.NONE);
		saveProfile.setText("Save settings as profile");
		saveProfile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		saveProfile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String name = newProfileNameText.getText().trim();
				if(name == null || name.equals("")) {
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
					messageBox.setText("Save profile");
					messageBox.setMessage("Please type in a profile name.");
					messageBox.open();
				} else {
					if(profileExists(name)) {
						MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
						messageBox.setText("Overwrite profile?");
						messageBox.setMessage("Do you really want to overwrite the profile: " + name);
						if(messageBox.open() == SWT.YES) {
							createProfile(name);
						}
					} else {
						createProfile(name);
					}
				}
			}
		});
	}

	private void setProfileName(String name) {

		/*
		 * Set the preference
		 */
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceSupplier.P_SELECTED_PROFILE, name);
		selectedProfileFieldEditor.setText(store.getString(PreferenceSupplier.P_SELECTED_PROFILE));
		/*
		 * Fill the combo list.
		 * Clear the new profile text field.
		 */
		availableProfilesCombo.setItems(getProfileNames());
		newProfileNameText.setText("");
	}

	private String[] getProfileNames() {

		File[] files = Profiles.getAvailableProfiles();
		String[] profileNames = new String[files.length];
		for(int i = 0; i < files.length; i++) {
			profileNames[i] = files[i].getName();
		}
		return profileNames;
	}

	private boolean profileExists(String name) {

		String[] profiles = getProfileNames();
		for(String profile : profiles) {
			if(profile.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private void createProfile(String name) {

		try {
			Profiles.createProfile(name, true);
			setProfileName(name);
		} catch(Exception e1) {
			logger.warn(e1);
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
