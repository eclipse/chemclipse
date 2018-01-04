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
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class ProteomsProjectNature implements IProjectNature {

	private IProject project;
	public final static String NATURE_ID = "org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.proteoms_project_nature";

	@Override
	public void configure() throws CoreException {

		// Add nature-specific information
		// for the project, such as adding a builder
		// to a project's build spec.
		// project.ad
	}

	@Override
	public void deconfigure() throws CoreException {

	}

	@Override
	public IProject getProject() {

		return project;
	}

	@Override
	public void setProject(IProject project) {

		this.project = project;
	}
}
