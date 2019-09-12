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
 * Christoph Läubrich - dynmic supplier support
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static final AtomicReference<ServiceTracker<IProcessTypeSupplier, IProcessTypeSupplier>> processTypeSupplierTracker = new AtomicReference<>();

	public static BundleContext getContext() {

		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {

		ServiceTracker<IProcessTypeSupplier, IProcessTypeSupplier> serviceTracker = new ServiceTracker<>(bundleContext, IProcessTypeSupplier.class, null);
		serviceTracker.open();
		ServiceTracker<IProcessTypeSupplier, IProcessTypeSupplier> tracker = processTypeSupplierTracker.getAndSet(serviceTracker);
		if(tracker != null) {
			tracker.close();
		}
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {

		ServiceTracker<IProcessTypeSupplier, IProcessTypeSupplier> tracker = processTypeSupplierTracker.getAndSet(null);
		if(tracker != null) {
			tracker.close();
		}
		Activator.context = null;
	}

	/**
	 * 
	 * @return an array holding all currently active {@link IProcessTypeSupplier}
	 */
	public static IProcessTypeSupplier[] geIProcessTypeSuppliers() {

		ServiceTracker<IProcessTypeSupplier, IProcessTypeSupplier> tracker = processTypeSupplierTracker.get();
		if(tracker != null) {
			return tracker.getServices(new IProcessTypeSupplier[0]);
		}
		return new IProcessTypeSupplier[0];
	}
}
