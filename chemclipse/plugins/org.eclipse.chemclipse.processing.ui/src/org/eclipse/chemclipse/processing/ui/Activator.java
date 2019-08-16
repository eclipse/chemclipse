/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	public static final String ICON_ERROR = "ICON_ERROR"; // $NON-NLS-1$
	public static final String ICON_WARN = "ICON_WARN"; // $NON-NLS-1$
	public static final String ICON_VALID = "INFO_VALID"; // $NON-NLS-1$
	public static final String ICON_INFO = "ICON_INFO"; // $NON-NLS-1$
	public static final String ICON_UNKNOWN = "ICON_UNKNOWN"; // $NON-NLS-1$
	//
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
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
		imageHashMap.put(ICON_INFO, "icons/16x16/info.gif");
		imageHashMap.put(ICON_VALID, "icons/16x16/valid.gif");
		imageHashMap.put(ICON_WARN, "icons/16x16/warn.gif");
		imageHashMap.put(ICON_ERROR, "icons/16x16/error.gif");
		imageHashMap.put(ICON_UNKNOWN, "icons/16x16/unknown.gif");
		//
		return imageHashMap;
	}
}
