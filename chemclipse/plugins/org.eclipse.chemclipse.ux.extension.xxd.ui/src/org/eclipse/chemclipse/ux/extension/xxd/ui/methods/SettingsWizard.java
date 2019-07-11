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
 * Christoph Läubrich - move method to open wizard
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.List;

import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class SettingsWizard extends Wizard {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;
	public static final String JSON_SECTION = "JsonSection";
	public static final String JSON_SETTINGS = "JsonSettings";
	//
	private SettingsWizardPage wizardPage;

	public SettingsWizard(List<InputValue> inputValues) {
		setWindowTitle("Settings");
		setDialogSettings(new DialogSettings(JSON_SECTION));
		setNeedsProgressMonitor(true);
		wizardPage = new SettingsWizardPage(inputValues);
	}

	@Override
	public void addPages() {

		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {

		return wizardPage.isPageComplete();
	}

	/**
	 * Open a wizard to edit the given {@link InputValue}s and returns the result
	 * 
	 * @param shell
	 * @param inputValues
	 * @return the edited value in JSON format or <code>null</code> if edit was canceled
	 */
	public static String executeWizard(Shell shell, List<InputValue> inputValues) {

		SettingsWizard wizard = new SettingsWizard(inputValues);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(SettingsWizard.DEFAULT_WIDTH, SettingsWizard.DEFAULT_HEIGHT);
		wizardDialog.create();
		if(wizardDialog.open() == WizardDialog.OK) {
			return wizard.getDialogSettings().get(SettingsWizard.JSON_SETTINGS);
		} else {
			return null;
		}
	}
}
