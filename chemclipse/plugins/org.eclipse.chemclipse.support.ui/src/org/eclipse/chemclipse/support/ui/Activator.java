/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui;

import java.util.HashMap;
import java.util.Map;

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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(SupportPreferences.INSTANCE());
		initializeImageRegistry(getImageHashMap());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
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

	private Map<String, String> getImageHashMap() {

		Map<String, String> imageHashMap = new HashMap<String, String>();
		//
		imageHashMap.put(ICON_FOLDER_OPENED, "icons/16x16/folder_opened.gif"); // $NON-NLS-1$
		imageHashMap.put(ICON_FOLDER_CLOSED, "icons/16x16/folder_closed.gif"); // $NON-NLS-1$
		imageHashMap.put(ICON_FILE, "icons/16x16/file.gif"); // $NON-NLS-1$
		//
		return imageHashMap;
	}
}
