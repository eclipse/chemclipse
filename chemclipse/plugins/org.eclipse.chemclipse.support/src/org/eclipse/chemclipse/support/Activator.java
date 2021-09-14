/*******************************************************************************
 * Copyright (c) 2012, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support;

import org.eclipse.chemclipse.support.settings.serialization.ISerializationService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static Activator plugin;
	private static BundleContext context;
	private ServiceTracker<ISerializationService, ISerializationService> serializationServiceTracker = null;

	public static BundleContext getContext() {

		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {

		Activator.context = bundleContext;
		plugin = this;
		serializationServiceTracker = new ServiceTracker<>(context, ISerializationService.class, null);
		serializationServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {

		Activator.context = null;
	}

	public static Activator getDefault() {

		return plugin;
	}

	public Object[] getSerializationServices() {

		return serializationServiceTracker.getServices();
	}
}