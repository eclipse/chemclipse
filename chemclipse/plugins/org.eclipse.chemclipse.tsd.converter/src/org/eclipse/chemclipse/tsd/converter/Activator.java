/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter;

import org.eclipse.chemclipse.tsd.converter.service.IConverterServiceTSD;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static Activator plugin;
	private static BundleContext context;
	//
	private ServiceTracker<IConverterServiceTSD, IConverterServiceTSD> converterServiceTracker = null;

	public static BundleContext getContext() {

		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {

		plugin = this;
		Activator.context = bundleContext;
		//
		converterServiceTracker = new ServiceTracker<>(context, IConverterServiceTSD.class, null);
		converterServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {

		converterServiceTracker.close();
		//
		plugin = null;
		Activator.context = null;
	}

	public static Activator getDefault() {

		return plugin;
	}

	public Object[] getConverterServices() {

		return converterServiceTracker.getServices();
	}
}