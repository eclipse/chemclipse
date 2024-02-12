/*******************************************************************************
 * Copyright (c) 2010, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.services.ILibraryInformationResolverService;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.support.settings.serialization.JSONSerialization;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(Activator.class);
	//
	private static Activator plugin;
	private IEclipseContext eclipseContext = null;
	private Bundle bundle;
	/*
	 * Services
	 */
	private ServiceTracker<ILibraryInformationResolverService, ILibraryInformationResolverService> libraryInformationResolverServiceTracker = null;

	/**
	 * The constructor
	 */
	public Activator() {

	}

	@Override
	public void start(BundleContext context) throws Exception {

		JSONSerialization.addMapping(IProcessMethod.class, ProcessMethod.class);
		JSONSerialization.addMapping(IProcessEntry.class, ProcessEntry.class);
		plugin = this;
		this.bundle = context.getBundle();
		startServices(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		JSONSerialization.removeMapping(IProcessMethod.class, ProcessMethod.class);
		JSONSerialization.removeMapping(IProcessEntry.class, ProcessEntry.class);
		stopServices();
		plugin = null;
	}

	/**
	 * Returns the bundle associated with this plug-in.
	 *
	 * @return the associated bundle
	 */
	public final Bundle getBundle() {

		return bundle;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {

		return plugin;
	}

	public IEventBroker getEventBroker() {

		IEclipseContext eclipseContext = getEclipseContext();
		return eclipseContext.get(IEventBroker.class);
	}

	public IEclipseContext getEclipseContext() {

		if(eclipseContext == null) {
			/*
			 * Create and initialize the context.
			 */
			eclipseContext = EclipseContextFactory.getServiceContext(bundle.getBundleContext());
			eclipseContext.set(Logger.class, logger);
		}
		//
		return eclipseContext;
	}

	public List<ILibraryInformationResolverService> getLibraryInformationResolverServices() {

		List<ILibraryInformationResolverService> services = new ArrayList<>();
		for(Object object : libraryInformationResolverServiceTracker.getServices()) {
			if(object instanceof ILibraryInformationResolverService service) {
				services.add(service);
			}
		}
		//
		return services;
	}

	private void startServices(BundleContext context) {

		libraryInformationResolverServiceTracker = new ServiceTracker<>(context, ILibraryInformationResolverService.class, null);
		libraryInformationResolverServiceTracker.open();
	}

	private void stopServices() {

		libraryInformationResolverServiceTracker.close();
	}
}
