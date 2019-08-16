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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.db.ProteomsDB;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.core.resources.IProject;

public class ProjectWrapper {

	private static final Logger log = Logger.getLogger(ProjectWrapper.class);
	private IProject project;
	private ProteomsDB db;
	List<SpectrumMS> msListCache = null;

	public ProjectWrapper(IProject project) {
		this.project = project;
		Path path = RCPUtil.getProjectPath(project);
		db = new ProteomsDB(path);
	}

	public List<SpectrumMS> getMSlist() {

		if(msListCache != null) {
			return msListCache;
		}
		try {
			msListCache = db.getMSandMSMSwithoutPeaks();
			return msListCache;
		} catch(IOException e) {
			log.error("Error DB: " + e.getMessage(), e);
			return null;
		}
	}

	public IProject getProject() {

		return project;
	}

	public static void saveToDisk(IProject p, List<SpectrumMS> parsedMSlist) throws IOException {

		Path path = RCPUtil.getProjectPath(p);
		ProteomsDB db = new ProteomsDB(path);
		db.saveAllSpectrumsMS(parsedMSlist);
	}
}
