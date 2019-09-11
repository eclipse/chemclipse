/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - move method to open wizard, refactor for new settings
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CancellationException;

import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;
import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.support.settings.parser.SettingsClassParser;
import org.eclipse.chemclipse.support.settings.parser.SettingsParser;
import org.eclipse.chemclipse.xxd.process.support.IProcessSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class SettingsWizard extends Wizard {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;

	private SettingsWizard(String title) {
		setWindowTitle(title);
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {

		return true;
	}

	/**
	 * Open a wizard to edit the given {@link InputValue}s and returns the result
	 * 
	 * @param shell
	 * @param inputValues
	 * @return the edited value in JSON format or <code>null</code> if edit was canceled
	 * @throws IOException
	 */
	public static Map<InputValue, Object> executeWizard(Shell shell, Map<InputValue, ?> inputValues) throws IOException {

		SettingsWizard wizard = new SettingsWizard("Settings");
		SettingsWizardPage page = new SettingsWizardPage(inputValues);
		wizard.addPage(page);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(SettingsWizard.DEFAULT_WIDTH, SettingsWizard.DEFAULT_HEIGHT);
		wizardDialog.create();
		if(wizardDialog.open() == WizardDialog.OK) {
			return page.getSettingsUI().getSettings();
		} else {
			return null;
		}
	}

	public static <T> boolean openWizard(Shell shell, IProcessSupplier<T> processorSupplier) {

		SettingsClassParser settings = new SettingsClassParser(processorSupplier.getSettingsClass());
		return openWizard(shell, settings, processorSupplier);
	}

	public static <T> boolean openWizard(Shell shell, SettingsParser settings, IProcessSupplier<T> processorSupplier) {

		ProcessorPreferences<T> preferences = processorSupplier.getPreferences();
		SettingsWizard wizard = new SettingsWizard("Edit Processor Options");
		SettingsPreferencesPage<T> page = new SettingsPreferencesPage<>(settings, preferences);
		page.setTitle("Select options to use for " + processorSupplier.getName());
		page.setMessage(processorSupplier.getDescription());
		wizard.addPage(page);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(SettingsWizard.DEFAULT_WIDTH, SettingsWizard.DEFAULT_HEIGHT);
		if(wizardDialog.open() == WizardDialog.OK) {
			preferences.setAskForSettings(!page.getIsDontAskAgainEdited());
			boolean useSystem = page.getIsUseSystemDefaultsEdited();
			if(useSystem) {
				preferences.setUseSystemDefaults(true);
			} else {
				preferences.setUseSystemDefaults(false);
				try {
					preferences.setUserSettings(preferences.getSerialization().toString(page.getJsonSettingsEdited()));
				} catch(IOException e) {
					throw new RuntimeException("writing settings failed", e);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static void openEditWizard(Shell shell, ProcessTypeSupport processTypeSupport) {

		SettingsWizard wizard = new SettingsWizard("Manage Processor Options");
		SettingsPreferencesEditPage page = new SettingsPreferencesEditPage(processTypeSupport);
		page.setTitle("Manage Preferences");
		page.setDescription("Below you find all currently stored processor Options, select one to manage or remove the stored state");
		wizard.addPage(page);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard) {

			@Override
			protected void createButtonsForButtonBar(Composite parent) {

				createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
			}

			@Override
			protected void buttonPressed(int buttonId) {

				cancelPressed();
			}

			@Override
			public void updateButtons() {

			}
		};
		wizardDialog.setMinimumPageSize(SettingsWizard.DEFAULT_WIDTH, SettingsWizard.DEFAULT_HEIGHT);
		wizardDialog.open();
	}

	/**
	 * Obtain the settings from the user, maybe asking for input
	 * 
	 * @param shell
	 * @param processorSupplier
	 * @return
	 * @throws IOException
	 */
	public static <T> T getSettings(Shell shell, IProcessSupplier<T> processorSupplier) throws IOException {

		Class<T> settingsClass = processorSupplier.getSettingsClass();
		if(settingsClass == null) {
			return null;
		} else {
			SettingsParser parser = new SettingsClassParser(settingsClass);
			ProcessorPreferences<T> preferences = processorSupplier.getPreferences();
			if(preferences.isAskForSettings()) {
				if(!parser.getInputValues().isEmpty()) {
					if(!openWizard(shell, parser, processorSupplier)) {
						throw new CancellationException("user has canceled the wizard");
					}
				}
			}
			if(preferences.isUseSystemDefaults()) {
				if(parser.getSystemSettingsStrategy() == SystemSettingsStrategy.NEW_INSTANCE) {
					try {
						return settingsClass.newInstance();
					} catch(InstantiationException | IllegalAccessException e) {
						throw new IOException("can't create new instance", e);
					}
				} else {
					return null;
				}
			} else {
				return preferences.getUserSettings();
			}
		}
	}
}
