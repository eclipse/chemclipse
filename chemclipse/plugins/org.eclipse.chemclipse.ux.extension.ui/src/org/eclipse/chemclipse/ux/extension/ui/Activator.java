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
package org.eclipse.chemclipse.ux.extension.ui;

import java.net.URL;
import java.util.MissingResourceException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String INFO_PERSPECTIVES = "Perspectives";
	public static final String PATH_PERSPECTIVES_INFO = "files/images/perspectives.png";
	// The shared instance
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
		initializeImageRegistry();
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
	public static Activator getDefault() {

		return plugin;
	}

	private void initializeImageRegistry() {

		try {
			URL fileLocation = getAbsolutePath(PATH_PERSPECTIVES_INFO);
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(fileLocation);
			ImageRegistry imageRegistry = getImageRegistry();
			if(imageRegistry != null) {
				getImageRegistry().put(INFO_PERSPECTIVES, imageDescriptor);
			}
		} catch(MissingResourceException e) {
			System.out.println(e);
		} catch(IllegalArgumentException e) {
			System.out.println(e);
		}
	}

	public static URL getAbsolutePath(String string) {

		Bundle bundle = Platform.getBundle(Activator.getDefault().getBundle().getSymbolicName());
		IPath path = new Path(string);
		URL url = FileLocator.find(bundle, path, null);
		return url;
	}
}
