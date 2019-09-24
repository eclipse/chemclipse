/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;

import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences;
import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences.DialogBehavior;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class SettingsPreferencesPage<T> extends WizardPage {

	private boolean isDontAskAgain;
	private boolean isUseSystemDefaults;
	private String jsonSettings;
	private ProcessorPreferences<T> preferences;

	public SettingsPreferencesPage(ProcessorPreferences<T> preferences) {
		super(SettingsPreferencesPage.class.getName());
		this.preferences = preferences;
	}

	@Override
	public void createControl(Composite parent) {

		boolean requiresUserSettings = preferences.getSupplier().getSettingsParser().requiresUserSettings();
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Button buttonDefault = new Button(composite, SWT.RADIO);
		buttonDefault.setText("Use System Options");
		if(requiresUserSettings) {
			buttonDefault.setEnabled(false);
			buttonDefault.setToolTipText("This processor does not offer System options or they are not applicable at the moment");
		}
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button buttonUser = new Button(composite, SWT.RADIO);
		buttonUser.setText("Use Specific Options");
		SettingsUI<?> settingsUI;
		try {
			settingsUI = new SettingsUI<>(composite, preferences);
		} catch(IOException e1) {
			throw new RuntimeException("reading settings failed", e1);
		}
		settingsUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		Listener validationListener = new Listener() {

			@Override
			public void handleEvent(Event event) {

				jsonSettings = null;
				if(buttonUser.getSelection()) {
					IStatus validate = settingsUI.getControl().validate();
					if(validate.isOK()) {
						setErrorMessage(null);
						setPageComplete(true);
					} else {
						setErrorMessage(validate.getMessage());
						setPageComplete(false);
					}
				} else {
					setErrorMessage(null);
					setPageComplete(true);
				}
				try {
					jsonSettings = settingsUI.getControl().getSettings();
				} catch(Exception e) {
					setErrorMessage(e.toString());
					setPageComplete(false);
				}
			}
		};
		SelectionListener radioButtonListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				settingsUI.setEnabled(buttonUser.getSelection());
				validationListener.handleEvent(null);
				isUseSystemDefaults = buttonDefault.getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		};
		buttonDefault.addSelectionListener(radioButtonListener);
		buttonUser.addSelectionListener(radioButtonListener);
		if(preferences.getDialogBehaviour() == DialogBehavior.NONE) {
			isDontAskAgain = false;
		} else {
			Button buttonDontAskAgain = new Button(composite, SWT.CHECK);
			buttonDontAskAgain.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true));
			buttonDontAskAgain.setText("Remeber my decision and don't ask again");
			buttonDontAskAgain.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					isDontAskAgain = buttonDontAskAgain.getSelection();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			buttonDontAskAgain.setSelection(isDontAskAgain = !(preferences.getDialogBehaviour() == DialogBehavior.SHOW));
		}
		if(preferences.isUseSystemDefaults() && !requiresUserSettings) {
			buttonDefault.setSelection(true);
		} else {
			buttonUser.setSelection(true);
		}
		radioButtonListener.widgetSelected(null);
		settingsUI.getControl().addChangeListener(validationListener);
		setControl(composite);
	}

	public boolean getIsDontAskAgainEdited() {

		return isDontAskAgain;
	}

	public String getSettingsEdited() throws IOException {

		return jsonSettings;
	}

	public boolean getIsUseSystemDefaultsEdited() {

		return isUseSystemDefaults;
	}
}
