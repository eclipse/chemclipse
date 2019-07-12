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

import java.util.List;

import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class SettingsPreferencesWizard extends Wizard {

	@Override
	public boolean performFinish() {

		return true;
	}

	public static boolean openWizard(Shell shell, List<InputValue> values, ProcessorPreferences preferences, ProcessorSupplier processorSupplier) {

		SettingsPreferencesWizard wizard = new SettingsPreferencesWizard();
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
		wizardDialog.create();
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
}
