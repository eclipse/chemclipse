/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public abstract class AbstractFileWizard extends AbstractWizard {

	private static final Logger logger = Logger.getLogger(AbstractFileWizard.class);
	private ISelection selection;
	private String defaultFileName;
	private String fileExtension;

	public AbstractFileWizard(String defaultFileName, String fileExtension) {
		this(new WizardElements(), defaultFileName, fileExtension);
	}

	public AbstractFileWizard(IWizardElements wizardElements, String defaultFileName, String fileExtension) {
		super(wizardElements);
		this.defaultFileName = defaultFileName;
		this.fileExtension = fileExtension;
	}

	@Override
	public void addPages() {

		/*
		 * Select the project an report file.
		 */
		addPage(new SelectProjectWizardPage(selection, getWizardElements()));
		addPage(new SelectFileWizardPage(getWizardElements(), defaultFileName, fileExtension));
	}

	/**
	 * Creates/Opens the project and returns the report file instance.
	 * 
	 * @param monitor
	 * @throws CoreException
	 * @return IFile
	 */
	protected IFile prepareProject(IProgressMonitor monitor) throws CoreException {

		IContainer container = getWizardElements().getContainer();
		String fileName = getWizardElements().getFileName();
		/*
		 * Prepare
		 */
		monitor.subTask(SupportMessages.INSTANCE().getMessage(ISupportMessages.TASK_PREPARE_PROJECT));
		if(container instanceof IProject) {
			IProject project = (IProject)container;
			if(!project.exists()) {
				/*
				 * Creates and opens the project if necessary.
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
		 * Returns the report file.
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
		monitor.subTask(SupportMessages.INSTANCE().getMessage(ISupportMessages.TASK_OPEN_EDITOR));
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
	 * Refreshes the workspace of the new report file.
	 * 
	 * @param monitor
	 */
	protected void refreshWorkspace(IProgressMonitor monitor) throws CoreException {

		/*
		 * Refresh the local workspace.
		 */
		IContainer container = getWizardElements().getContainer();
		if(container != null) {
			container.refreshLocal(1, monitor); // IResource.DEPTH_INFINITE
		}
	}
}
