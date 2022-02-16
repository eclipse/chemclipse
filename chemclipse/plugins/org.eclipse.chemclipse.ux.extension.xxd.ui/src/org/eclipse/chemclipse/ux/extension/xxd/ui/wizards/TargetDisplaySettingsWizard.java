/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring target label support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.util.Collection;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class TargetDisplaySettingsWizard {

	public static final int DEFAULT_WIDTH = 1000;
	public static final int DEFAULT_HEIGHT = 600;

	public static boolean openWizard(Shell shell, Collection<? extends ITargetReference> targetReferences, ITargetDisplaySettings targetDisplaySettings, TargetDisplaySettingsWizardListener settingsWizardListener) {

		TargetDisplaySettingsPage targetDisplaySettingsPage = new TargetDisplaySettingsPage(targetReferences, targetDisplaySettings, settingsWizardListener);
		SinglePageWizard wizard = new SinglePageWizard("Target Label Settings", false, targetDisplaySettingsPage);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard) {

			@Override
			protected void createButtonsForButtonBar(Composite parent) {

				super.createButtonsForButtonBar(parent);
				getButton(CANCEL).setEnabled(false);
				getButton(IDialogConstants.FINISH_ID).setText(IDialogConstants.OK_LABEL);
			}
		};
		/*
		 * Initial width and height.
		 */
		wizardDialog.setPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		return (wizardDialog.open() == Window.OK);
	}

	private TargetDisplaySettingsWizard() {
		// static access only

	}
}
