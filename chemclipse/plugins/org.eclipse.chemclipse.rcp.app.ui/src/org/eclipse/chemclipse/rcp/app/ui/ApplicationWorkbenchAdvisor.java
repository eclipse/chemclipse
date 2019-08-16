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
package org.eclipse.chemclipse.rcp.app.ui;

import org.eclipse.chemclipse.support.ui.workbench.WorkbenchAdvisorSupport;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	/*
	 * This is the default perspective.
	 */
	private static final String PERSPECTIVE_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.perspective.main";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {

		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public void initialize(IWorkbenchConfigurer configurer) {

		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
		WorkbenchAdvisorSupport.declareProjectExplorerImages(configurer);
	}

	public String getInitialWindowPerspectiveId() {

		return PERSPECTIVE_ID;
	}

	@Override
	public IAdaptable getDefaultPageInput() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}
}
