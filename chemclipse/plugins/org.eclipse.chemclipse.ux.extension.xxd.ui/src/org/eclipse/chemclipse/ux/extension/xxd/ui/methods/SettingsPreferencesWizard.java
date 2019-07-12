/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class SettingsPreferencesWizard extends Wizard {

	@Override
	public boolean performFinish() {

		return true;
	}

	public static boolean openWizard(Shell shell, ProcessorPreferences preferences, ProcessorSupplier processorSupplier) {

		try {
			return openWizard(shell, InputValue.readJSON(processorSupplier.getSettingsClass(), preferences.getUserSettings()), preferences, processorSupplier);
		} catch(IOException e) {
			return false;
		}
	}

	public static boolean openWizard(Shell shell, List<InputValue> values, ProcessorPreferences preferences, ProcessorSupplier processorSupplier) {

		SettingsPreferencesWizard wizard = new SettingsPreferencesWizard();
		wizard.setWindowTitle("Edit Processor Options");
		SettingsPreferencesPage page = new SettingsPreferencesPage(values, preferences);
		if(processorSupplier == null) {
			page.setTitle("Edit preferences");
		} else {
			page.setTitle("Select options to use for " + processorSupplier.getName());
			page.setMessage(processorSupplier.getDescription());
		}
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
				preferences.setUserSettings(page.getJsonSettingsEdited());
			}
			return true;
		} else {
			return false;
		}
	}

	public static void openEditWizard(Shell shell, ProcessTypeSupport processTypeSupport) {

		SettingsPreferencesWizard wizard = new SettingsPreferencesWizard();
		wizard.setWindowTitle("Manage Processor Options");
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
}
