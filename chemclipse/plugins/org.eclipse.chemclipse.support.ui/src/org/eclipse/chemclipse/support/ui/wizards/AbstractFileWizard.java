/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import org.eclipse.chemclipse.logging.core.Logger;

public abstract class AbstractFileWizard extends Wizard implements IFileWizard {

	private static final Logger logger = Logger.getLogger(AbstractFileWizard.class);
	private ISelection selection;
	private IWizardElements wizardElements;
	private SelectProjectWizardPage selectProjectWizardPage;
	private SelectFileWizardPage selectFileWizardPage;
	private String defaultFileName;
	private String fileExtension;

	public AbstractFileWizard(String defaultFileName, String fileExtension) {

		this(new WizardElements(), defaultFileName, fileExtension);
	}

	public AbstractFileWizard(IWizardElements wizardElements, String defaultFileName, String fileExtension) {

		super();
		setNeedsProgressMonitor(true);
		this.wizardElements = wizardElements;
		this.defaultFileName = defaultFileName;
		this.fileExtension = fileExtension;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		this.selection = selection;
	}

	@Override
	public void addPages() {

		/*
		 * Select the project
		 */
		selectProjectWizardPage = new SelectProjectWizardPage(selection, wizardElements);
		addPage(selectProjectWizardPage);
		/*
		 * Select the file name
		 */
		selectFileWizardPage = new SelectFileWizardPage(wizardElements, defaultFileName, fileExtension);
		addPage(selectFileWizardPage);
	}

	@Override
	public boolean canFinish() {

		/*
		 * Project name.
		 */
		IContainer container = wizardElements.getContainer();
		if(container == null) {
			return false;
		}
		/*
		 * File name.
		 */
		String fileName = wizardElements.getFileName();
		if(fileName == null || fileName.equals("")) {
			return false;
		}
		/*
		 * All validations are passed successfully.
		 */
		return true;
	}

	/**
	 * Returns the wizard elements.
	 * 
	 * @return {@link IWizardElements}
	 */
	public IWizardElements getWizardElements() {

		return wizardElements;
	}

	@Override
	public boolean performFinish() {

		/*
		 * Creates the runnable.
		 */
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					doFinish(monitor);
				} catch(CoreException e) {
					logger.warn(e);
				}
			}
		};
		/*
		 * Run
		 */
		try {
			getContainer().run(true, false, runnable);
		} catch(InterruptedException e) {
			MessageDialog.openError(getShell(), "Error", "The process was interrupted.");
			return false;
		} catch(InvocationTargetException e) {
			MessageDialog.openError(getShell(), "Error", "Something has gone wrong.");
			return false;
		}
		return true;
	}

	/**
	 * Creates/Opens the project and returns the file instance.
	 * 
	 * @param monitor
	 * @throws CoreException
	 * @return IFile
	 */
	protected IFile prepareProject(IProgressMonitor monitor) throws CoreException {

		IContainer container = wizardElements.getContainer();
		String fileName = wizardElements.getFileName();
		/*
		 * Prepare
		 */
		monitor.setTaskName("Prepare project and file...");
		if(container instanceof IProject) {
			IProject project = (IProject)container;
			if(!project.exists()) {
				/*
				 * Creates and opens the project if neccessary.
				 */
				project.create(monitor);
				project.open(monitor);
			}
			/*
			 * Opens the project.
			 */
			if(!project.isOpen()) {
				project.open(monitor);
			}
		}
		/*
		 * Returns the file.
		 */
		return container.getFile(new Path(fileName));
	}

	/**
	 * Opens the associated file editor.
	 * 
	 * @param file
	 * @param monitor
	 */
	protected void runOpenEditor(final IFile file, IProgressMonitor monitor) {

		/*
		 * Open the editor
		 */
		monitor.setTaskName("Open the editor");
		getShell().getDisplay().asyncExec(new Runnable() {

			public void run() {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch(PartInitException e) {
					logger.warn(e);
				}
			}
		});
	}

	/**
	 * Refreshes the workspace of the new file.
	 * 
	 * @param monitor
	 */
	protected void refreshWorkspace(IProgressMonitor monitor) throws CoreException {

		/*
		 * Refresh the local workspace.
		 */
		IContainer container = wizardElements.getContainer();
		if(container != null) {
			container.refreshLocal(1, monitor);
		}
	}
}
