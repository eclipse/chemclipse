/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui;

import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractActivatorUI {

	// The shared instance
	private static Activator plugin;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {

		saveWorkspace();
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

	/**
	 * Avoid the message:
	 * !MESSAGE The workspace exited with unsaved changes in the previous session;
	 * refreshing workspace to recover changes.
	 */
	private void saveWorkspace() {

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProgressMonitor monitor = new NullProgressMonitor();
			workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			workspace.save(true, monitor);
		} catch(CoreException e) {
			// This shouldn't happen.
		}
	}
}
