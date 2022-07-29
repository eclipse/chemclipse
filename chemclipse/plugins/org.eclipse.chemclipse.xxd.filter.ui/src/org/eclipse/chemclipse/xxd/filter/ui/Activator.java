/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.ui;

import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.numeric.services.IMaximaDetectorService;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	private static Activator plugin;
	private ServiceTracker<IMaximaDetectorService, IMaximaDetectorService> maximaDetectorServiceTracker = null;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
		maximaDetectorServiceTracker = new ServiceTracker<>(context, IMaximaDetectorService.class, null);
		maximaDetectorServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		maximaDetectorServiceTracker.close();
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

	public Object[] getMaximaDetectorServices() {

		return maximaDetectorServiceTracker.getServices();
	}
}
