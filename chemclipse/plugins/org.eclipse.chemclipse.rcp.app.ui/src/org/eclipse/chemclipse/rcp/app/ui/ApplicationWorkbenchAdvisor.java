/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
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

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

@SuppressWarnings({"restriction"})
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	/*
	 * This is the default perspective.
	 */
	private static final String PERSPECTIVE_ID = "org.eclipse.chemclipse.chromatogram.msd.perspective.ui.perspective.main";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {

		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public void initialize(IWorkbenchConfigurer configurer) {

		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
		/*
		 * It's an internal class, but it must be used, otherwise the workspace
		 * view wouldn't be visible.
		 */
		IDE.registerAdapters();
		/*
		 * Images for the project explorer.
		 */
		final String ICONS_PATH = "icons/full/";
		final String PATH_OBJECT = ICONS_PATH + "obj16/";
		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);
		declareWorkbenchImage(configurer, ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT, PATH_OBJECT + "prj_obj.gif", true);
		declareWorkbenchImage(configurer, ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, PATH_OBJECT + "cprj_obj.gif", true);
	}

	public String getInitialWindowPerspectiveId() {

		return PERSPECTIVE_ID;
	}

	@Override
	public IAdaptable getDefaultPageInput() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}

	/**
	 * Declares the needed workbench image.
	 * 
	 * @param configurer
	 * @param ideBundle
	 * @param symbolicName
	 * @param path
	 * @param shared
	 */
	private void declareWorkbenchImage(IWorkbenchConfigurer configurer, Bundle ideBundle, String symbolicName, String path, boolean shared) {

		URL url = ideBundle.getEntry(path);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		configurer.declareImage(symbolicName, desc, shared);
	}
}
