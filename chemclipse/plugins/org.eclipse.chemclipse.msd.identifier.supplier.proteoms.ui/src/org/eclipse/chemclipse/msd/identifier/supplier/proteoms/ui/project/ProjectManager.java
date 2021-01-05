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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.model.ProteomsProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public class ProjectManager {

	public static final String DEFAULT_PROJECT_NAME = "Proteoms project";
	private Path projectsDir;
	@SuppressWarnings("unused")
	private IProject project;

	public ProjectManager(IProject project) {

		this.project = project;
		IPath fullPath = project.getFullPath();
		projectsDir = fullPath.toFile().toPath();
	}

	public ProjectManager(Path dir) {

		projectsDir = dir;
	}

	public List<ProteomsProject> findProjects() throws IOException {

		return Files.list(projectsDir).filter(t -> {
			return t.toFile().isDirectory();
		}).map(t -> new ProteomsProject(t)).collect(Collectors.toList());
	}
}
