/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.Activator;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.ImportDataSelection;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.ProjectWrapper;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.RCPUtil;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata.ImportParameters.FILE_FORMAT;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class ImportWizard extends Wizard implements IImportWizard {

	private static final Logger log = Logger.getLogger(ImportWizard.class);
	private ParametersWizardPage parametersPage;
	private MGFimportWizardPage mgfImportPage;
	private ImportParameters parameters;

	public ImportWizard() {
		super();
	}

	@Override
	public IDialogSettings getDialogSettings() {

		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection("proteoms_importwizard");
		if(section == null) {
			section = settings.addNewSection("proteoms_importwizard");
		}
		return section;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		setWindowTitle("MS/MS files Import Wizard"); // NON-NLS-1
		setNeedsProgressMonitor(true);
		parameters = new ImportParameters();
		parametersPage = new ParametersWizardPage(parameters, selection); // NON-NLS-1
		mgfImportPage = new MGFimportWizardPage(parameters, selection);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	@Override
	public void addPages() {

		addPage(parametersPage);
		addPage(mgfImportPage);
	}

	@Override
	public boolean performFinish() {

		if(parameters.getFileFormat() == FILE_FORMAT.MFG_APPLIED_BIOSYSTEM) {
			try {
				getContainer().run(true, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						monitor.setTaskName("Save to disk");
						monitor.beginTask("Start saveing...", 3);
						List<SpectrumMS> parsedMSlist = mgfImportPage.getParsedMSlist();
						try {
							ProjectWrapper.saveToDisk(parameters.getProject(), parsedMSlist);
							monitor.worked(1);
						} catch(IOException e) {
							log.error("Import error. " + e.getMessage(), e);
							monitor.done();
							RCPUtil.showWarningMessageDialog("Import error ", getShell());
							return;
						}
						if(monitor.isCanceled()) {
							// TODO: implement this
						}
						getShell().getDisplay().asyncExec(() -> {
							ESelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ESelectionService.class);
							selectionService.setSelection(new ImportDataSelection(parsedMSlist, parameters));
						});
						monitor.worked(1);
						monitor.done();
					}
				});
			} catch(InvocationTargetException | InterruptedException e) {
				log.error("Import error. " + e.getMessage(), e);
				e.printStackTrace();
				RCPUtil.showWarningMessageDialog("Import error. " + e.getMessage(), getShell());
			}
		}
		return true;
	}
}
