/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for tracking TileDefinitions
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui;

import java.net.URL;
import java.util.MissingResourceException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	public static final String INFO_PERSPECTIVES = "Perspectives";
	public static final String PATH_PERSPECTIVES_INFO = "files/images/perspectives.png";
	//
	private static Activator plugin;
	private static final Logger logger = Logger.getLogger(Activator.class);
	private ServiceTracker<TileDefinition, TileDefinition> tileServiceTracker;

	public Activator() {

	}

	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
		initializeImageRegistry();
		tileServiceTracker = new ServiceTracker<>(context, TileDefinition.class, null);
		tileServiceTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
		tileServiceTracker.close();
		tileServiceTracker = null;
	}

	public static Activator getDefault() {

		return plugin;
	}

	public TileDefinition[] getTileDefinitions() {

		TileDefinition[] array = new TileDefinition[0];
		if(tileServiceTracker == null) {
			return array;
		}
		return tileServiceTracker.getServices(array);
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
			logger.warn(e);
		} catch(IllegalArgumentException e) {
			logger.warn(e);
		}
	}

	public static URL getAbsolutePath(String string) {

		Bundle bundle = Platform.getBundle(Activator.getDefault().getBundle().getSymbolicName());
		IPath path = new Path(string);
		URL url = FileLocator.find(bundle, path, null);
		return url;
	}
}