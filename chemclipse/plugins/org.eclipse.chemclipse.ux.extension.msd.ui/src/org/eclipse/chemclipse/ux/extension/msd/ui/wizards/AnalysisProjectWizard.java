/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class AnalysisProjectWizard extends Wizard implements INewWizard {

	private static final Logger logger = Logger.getLogger(AnalysisProjectWizard.class);
	private WizardNewProjectCreationPage projectCreationPage;

	public AnalysisProjectWizard() {
	}

	@Override
	public boolean performFinish() {

		try {
			getContainer().run(false, true, new WorkspaceModifyOperation() {

				@Override
				protected void execute(IProgressMonitor monitor) {

					createProject(monitor != null ? monitor : new NullProgressMonitor());
				}
			});
		} catch(InvocationTargetException e) {
			return false;
		} catch(InterruptedException e) {
			return false;
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {

		super.addPages();
		projectCreationPage = new WizardNewProjectCreationPage("MS Analysis Project");
		projectCreationPage.setTitle("New MS Analysis Project");
		projectCreationPage.setDescription("Create a new MS Analysis Project");
		// projectCreationPage.setImageDescriptor(ImageDescriptor.createFromFile(getClass(),
		// "/icons/chromatogram.gif"));
		addPage(projectCreationPage);
	}

	/**
	 * This is the actual implementation for project creation.
	 * 
	 * @param monitor
	 *            reports progress on this object
	 */
	protected void createProject(IProgressMonitor monitor) {

		try {
			monitor.beginTask("Creating MS Analysis Project", 50);
			/*
			 * Workspace root.
			 */
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			monitor.subTask("Create Directories");
			IProject project = workspaceRoot.getProject(projectCreationPage.getProjectName());
			IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
			if(!Platform.getLocation().equals(projectCreationPage.getLocationPath())) {
				projectDescription.setLocation(projectCreationPage.getLocationPath());
			}
			/*
			 * Try to create the project.
			 */
			try {
				project.create(projectDescription, monitor);
				monitor.worked(10);
				project.open(monitor);
				IPath projectPath = project.getFullPath();
				/*
				 * Chromatogram data files.
				 */
				IPath dataFiles = projectPath.append("DataFiles");
				IFolder dataFolder = workspaceRoot.getFolder(dataFiles);
				dataFolder.create(false, true, monitor);
				/*
				 * Report files.
				 */
				IPath reportFiles = projectPath.append("ReportFiles");
				IFolder reportFolder = workspaceRoot.getFolder(reportFiles);
				reportFolder.create(false, true, monitor);
				IPath report = reportFiles.append("analysisReport.txt");
				IFile reportFile = workspaceRoot.getFile(report);
				reportFile.create(null, false, monitor);
			} catch(CoreException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}
}
