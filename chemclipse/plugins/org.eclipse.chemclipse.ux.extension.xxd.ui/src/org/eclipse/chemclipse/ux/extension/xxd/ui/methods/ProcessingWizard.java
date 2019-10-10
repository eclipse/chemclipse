/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for different datatype sets
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.Collections;
import java.util.Map;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ProcessingWizard extends Wizard {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;
	public static final String PROCESSING_SECTION = "JsonSection";
	public static final String PROCESSING_SETTINGS = "JsonSettings";

	private ProcessingWizard() {
		setWindowTitle("Settings");
		setDialogSettings(new DialogSettings(PROCESSING_SECTION));
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {

		return true;
	}

	@Deprecated
	public static IProcessEntry open(Shell shell, ProcessTypeSupport processingSupport, DataType[] datatypes) {

		return open(shell, Collections.singletonMap(processingSupport, "global"), datatypes).get(processingSupport);
	}

	public static Map<ProcessSupplierContext, IProcessEntry> open(Shell shell, Map<ProcessSupplierContext, String> contexts, DataType[] datatypes) {

		ProcessingWizard wizard = new ProcessingWizard();
		ProcessingWizardPage wizardPage = new ProcessingWizardPage(contexts, datatypes);
		wizard.addPage(wizardPage);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(ProcessingWizard.DEFAULT_WIDTH, ProcessingWizard.DEFAULT_HEIGHT);
		wizardDialog.create();
		//
		if(wizardDialog.open() == WizardDialog.OK) {
			return Collections.singletonMap(wizardPage.getProcessSupplierContext(), wizardPage.getProcessEntry());
		}
		return null;
	}
}
