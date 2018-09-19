/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.Date;

import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.wizard.Wizard;

public class ProcessingWizard extends Wizard {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;
	public static final String PROCESSING_SECTION = "JsonSection";
	public static final String PROCESSING_SETTINGS = "JsonSettings";
	//
	private ProcessingWizardPage wizardPage;

	public ProcessingWizard() {
		setWindowTitle("Settings");
		setDialogSettings(new DialogSettings(PROCESSING_SECTION));
		setNeedsProgressMonitor(true);
		wizardPage = new ProcessingWizardPage();
	}

	@Override
	public void addPages() {

		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {

		return wizardPage.isPageComplete();
	}

	public IProcessMethod getProcessMethod() {

		return new ProcessMethod("my.id", "Test " + new Date().toString(), "Hello Description", "{}", "MSD");
	}
}
