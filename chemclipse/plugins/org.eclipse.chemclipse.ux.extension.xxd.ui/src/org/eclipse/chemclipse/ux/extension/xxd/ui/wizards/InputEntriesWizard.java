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

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.jface.wizard.Wizard;

public class InputEntriesWizard extends Wizard {

	public enum TreeSelection {
		DRIVES, //
		HOME, //
		USER_LOCATION, //
		NONE;
	}

	private InputWizardSettings inputWizardSettings;
	private InputEntriesWizardPage inputEntriesPage;

	public InputEntriesWizard(InputWizardSettings inputWizardSettings) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Select data");
		this.inputWizardSettings = inputWizardSettings;
	}

	@Override
	public void addPages() {

		inputEntriesPage = new InputEntriesWizardPage(inputWizardSettings);
		addPage(inputEntriesPage);
	}

	public IChromatogramWizardElements getChromatogramWizardElements() {

		return inputEntriesPage.getChromatogramWizardElements();
	}

	@Override
	public boolean performFinish() {

		// FIXME inputWizardSettings.saveSelectedPath(inputEntriesPage.getTreeSelection());
		return true;
	}
}
