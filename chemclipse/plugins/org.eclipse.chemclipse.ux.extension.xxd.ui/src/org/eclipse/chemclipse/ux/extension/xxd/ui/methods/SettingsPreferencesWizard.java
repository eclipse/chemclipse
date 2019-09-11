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

public class SettingsPreferencesWizard extends Wizard {

	@Override
	public boolean performFinish() {

		return true;
	}

	public static <T> boolean openWizard(Shell shell, IProcessSupplier<T> processorSupplier) {

		SettingsClassParser settings = new SettingsClassParser(processorSupplier.getSettingsClass());
		return openWizard(shell, settings, processorSupplier);
	}

	public static <T> boolean openWizard(Shell shell, SettingsParser settings, IProcessSupplier<T> processorSupplier) {

		ProcessorPreferences<T> preferences = processorSupplier.getPreferences();
		SettingsPreferencesWizard wizard = new SettingsPreferencesWizard();
		wizard.setWindowTitle("Edit Processor Options");
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
