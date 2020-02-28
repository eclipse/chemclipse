/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class SubtractScanWizard extends Wizard {

	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 100;

	private SubtractScanWizard(String title) {
		setWindowTitle(title);
		setNeedsProgressMonitor(false);
	}

	@Override
	public boolean performFinish() {

		return true;
	}

	public static void openWizard(Shell shell) {

		SubtractScanWizard wizard = new SubtractScanWizard("Subtract Scan Options");
		SubtractScanPage page = new SubtractScanPage();
		page.setTitle("Subtract Scan Preferences");
		page.setDescription("The following options are available to subtract a scan.");
		wizard.addPage(page);
		//
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
		//
		wizardDialog.setMinimumPageSize(SubtractScanWizard.DEFAULT_WIDTH, SubtractScanWizard.DEFAULT_HEIGHT);
		wizardDialog.open();
	}
}
