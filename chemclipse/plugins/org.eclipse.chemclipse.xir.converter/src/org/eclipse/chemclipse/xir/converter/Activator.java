/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.converter;

import org.eclipse.chemclipse.xir.converter.service.IConverterServiceISD;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static Activator plugin;
	private static BundleContext context;
	private ServiceTracker<IConverterServiceISD, IConverterServiceISD> converterServiceTracker = null;

	public static BundleContext getContext() {

		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {

		Activator.context = bundleContext;
		plugin = this;
		converterServiceTracker = new ServiceTracker<>(context, IConverterServiceISD.class, null);
		converterServiceTracker.open();
	}

	public void stop(BundleContext bundleContext) throws Exception {

		converterServiceTracker.close();
		Activator.context = null;
	}

	public static Activator getDefault() {

		return plugin;
	}

	public Object[] getConverterServices() {

		return converterServiceTracker.getServices();
	}
}