/*******************************************************************************
 * Copyright (c) 2016, 2021 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.project.ProjectManager;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	private static final Logger log = Logger.getLogger(Activator.class);
	private static Activator plugin;
	private ProjectManager projectManager;

	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
	}

	@SuppressWarnings("unused")
	private void initProjectManagerx() {

		try {
			IWorkspace w = ResourcesPlugin.getWorkspace();
			IProject project = w.getRoot().getProject(ProjectManager.DEFAULT_PROJECT_NAME);
			if(!project.exists()) {
				IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
				project.create(projectDescription, new NullProgressMonitor());
				addProjectNature(project);
				project.open(new NullProgressMonitor());
			}
			projectManager = new ProjectManager(project);
		} catch(CoreException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public ProjectManager getProjectManager() {

		return projectManager;
	}

	private void addProjectNature(IProject project) {

		try {
			project.open(new NullProgressMonitor());
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = ProteomsProjectNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, new NullProgressMonitor());
		} catch(CoreException e) {
			// Something went wrong
			log.error("Project nature error: " + e.getMessage(), e);
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {

		return plugin;
	}
}
