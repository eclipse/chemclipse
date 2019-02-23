/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.activator;

import java.net.URL;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public abstract class AbstractActivatorUI extends AbstractUIPlugin {

	/*
	 * This preference store uses the model store instead of the GUI store
	 * achieve a clean separation of concerns.
	 */
	private ScopedPreferenceStore preferenceStore;
	/*
	 * The event broker is initialized on first use.
	 */
	private IEventBroker eventBroker = null;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {

		preferenceStore = null;
		super.stop(context);
	}

	@Override
	public IPreferenceStore getPreferenceStore() {

		if(preferenceStore == null) {
			return super.getPreferenceStore();
		}
		return preferenceStore;
	}

	/**
	 * Returns the image given by the key.
	 * Please initialize the image registry before using this function.
	 * Use the method e.g.:
	 * 
	 * getImage(ICON_WARN)
	 * 
	 * @param key
	 * @return
	 */
	public Image getImage(String key) {

		return getImageRegistry().get(key);
	}

	public IEventBroker getEventBroker() {

		if(eventBroker == null) {
			IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(getBundle().getBundleContext());
			eclipseContext.set(Logger.class, null);
			eventBroker = eclipseContext.get(IEventBroker.class);
		}
		return eventBroker;
	}

	/**
	 * Initialize the preference store.
	 * 
	 * @param preferenceSupplier
	 */
	protected void initializePreferenceStore(IPreferenceSupplier preferenceSupplier) {

		if(preferenceSupplier != null) {
			/*
			 * Set the default values.
			 */
			preferenceStore = new ScopedPreferenceStore(preferenceSupplier.getScopeContext(), preferenceSupplier.getPreferenceNode());
			Map<String, String> initializationEntries = preferenceSupplier.getDefaultValues();
			for(Map.Entry<String, String> entry : initializationEntries.entrySet()) {
				preferenceStore.setDefault(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Initialize the image registry.
	 * Please supply a HashMap of used images/icons, e.g.:
	 * 
	 * public static final String ICON_WARN = "ICON_WARN";
	 * 
	 * Map<String, String> imageHashMap = new HashMap<String, String>();
	 * imageHashMap.put(ICON_WARN, "icons/16x16/warn.gif");
	 * 
	 * The icon path is set relative to the calling plugin.
	 */
	protected void initializeImageRegistry(Map<String, String> imageHashMap) {

		ImageRegistry imageRegistry = getImageRegistry();
		if(imageHashMap != null && imageRegistry != null) {
			/*
			 * Set the image/icon values.
			 */
			for(Map.Entry<String, String> entry : imageHashMap.entrySet()) {
				imageRegistry.put(entry.getKey(), createImageDescriptor(getBundle(), entry.getValue()));
			}
		}
	}

	/**
	 * Helps to create an image descriptor.
	 * 
	 * @param bundle
	 * @param string
	 * @return ImageDescriptor
	 */
	private ImageDescriptor createImageDescriptor(Bundle bundle, String string) {

		ImageDescriptor imageDescriptor = null;
		IPath path = new Path(string);
		URL url = FileLocator.find(bundle, path, null);
		imageDescriptor = ImageDescriptor.createFromURL(url);
		return imageDescriptor;
	}
}
