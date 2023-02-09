/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui;

import org.eclipse.chemclipse.support.preferences.SupportPreferences;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	public static final String ICON_FOLDER_OPENED = "ICON_FOLDER_OPENED"; // $NON-NLS-1$
	public static final String ICON_FOLDER_CLOSED = "ICON_FOLDER_CLOSED"; // $NON-NLS-1$
	public static final String ICON_FILE = "ICON_FILE"; // $NON-NLS-1$
	/*
	 * Instance
	 */
	private static Activator plugin;
	private static BundleContext context;

	public static BundleContext getContext() {

		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		Activator.context = context;
		plugin = this;
		initializePreferenceStore(SupportPreferences.INSTANCE());
		/*
		 * Don't initialize the image registry.
		 * The plug-in crashed when running the unit tests.
		 * To be honest, I don't understand that.
		 */
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
	public static AbstractActivatorUI getDefault() {

		return plugin;
	}
}
