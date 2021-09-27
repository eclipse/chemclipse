/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - register IApplicationImageProvider
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	//
	public static final String IMAGE_EMPTY = "icons/empty.png";
	// The shared instance
	private static Activator plugin;
	private ApplicationImage applicationImage;

	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		applicationImage = new ApplicationImage(context);
		applicationImage.start();
		applicationImage.getImage(IApplicationImage.IMAGE_DECORATOR_ACTIVE, IApplicationImage.SIZE_7x7); // HACK: force init
		context.registerService(IApplicationImageProvider.class, applicationImage, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		applicationImage.stop();
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
	 * The icon string is e.g.: "icons/empty.png".
	 * The method returns the image as an input stream from
	 * the current bundle.
	 * 
	 * @param icon
	 * @return InputStream
	 * @throws IOException
	 */
	public InputStream getIconInputStream(String icon) throws IOException {

		return FileLocator.find(getBundle(), new Path(icon), null).openStream();
	}

	public ApplicationImage getApplicationImage() {

		return applicationImage;
	}
}
