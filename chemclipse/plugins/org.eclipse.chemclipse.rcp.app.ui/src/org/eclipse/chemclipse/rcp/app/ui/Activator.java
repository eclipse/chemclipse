/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui;

import org.eclipse.chemclipse.rcp.app.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractActivatorUI {

	private static Activator plugin;

	public void start(BundleContext context) throws Exception {

		super.start(context);
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {

		saveWorkspace();
		plugin = null;
		super.stop(context);
	}

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