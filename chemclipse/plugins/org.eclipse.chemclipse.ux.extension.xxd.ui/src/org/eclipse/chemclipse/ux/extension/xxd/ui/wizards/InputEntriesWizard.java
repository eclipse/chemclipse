/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class InputEntriesWizard extends Wizard {

	private final InputWizardSettings inputWizardSettings;
	private InputEntriesWizardPage inputEntriesPage;

	private InputEntriesWizard(InputWizardSettings inputWizardSettings) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Select data");
		this.inputWizardSettings = inputWizardSettings;
	}

	@Override
	public void addPages() {

		inputEntriesPage = new InputEntriesWizardPage(inputWizardSettings);
		addPage(inputEntriesPage);
	}

	@Override
	public boolean performFinish() {

		inputEntriesPage.savePath();
		return true;
	}

	/**
	 * 
	 * @param shell
	 * @param inputWizardSettings
	 * @return a mapping between selected files and responsible {@link ISupplierFileIdentifier} or an empty map if user canceled the wizard
	 */
	public static Map<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> openWizard(Shell shell, InputWizardSettings inputWizardSettings) {

		InputEntriesWizard inputWizard = new InputEntriesWizard(inputWizardSettings);
		WizardDialog wizardDialog = new WizardDialog(shell, inputWizard);
		wizardDialog.setPageSize(InputWizardSettings.DEFAULT_WIDTH, InputWizardSettings.DEFAULT_HEIGHT);
		wizardDialog.create();
		if(wizardDialog.open() == WizardDialog.OK) {
			return inputWizard.inputEntriesPage.getSelectedItems();
		} else {
			return Collections.emptyMap();
		}
	}
}
