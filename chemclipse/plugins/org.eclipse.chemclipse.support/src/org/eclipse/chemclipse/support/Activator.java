/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.serialization.ISerializationService;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static Activator plugin;
	private static final Logger logger = Logger.getLogger(Activator.class);
	private static BundleContext context;
	private IEclipseContext eclipseContext = null;
	private ServiceTracker<ISerializationService, ISerializationService> serializationServiceTracker = null;

	public static BundleContext getContext() {

		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
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
	@Override
	public void stop(BundleContext bundleContext) throws Exception {

		Activator.context = null;
	}

	public static Activator getDefault() {

		return plugin;
	}

	public Object[] getSerializationServices() {

		return serializationServiceTracker.getServices();
	}

	public IEclipseContext getEclipseContext() {

		if(eclipseContext == null) {
			/*
			 * Create and initialize the context.
			 */
			eclipseContext = EclipseContextFactory.getServiceContext(context);
			eclipseContext.set(Logger.class, logger);
		}
		//
		return eclipseContext;
	}
}