/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse;

import org.eclipse.chemclipse.logging.core.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(Activator.class);
	private static BundleContext context;

	public static BundleContext getContext() {

		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {

		Activator.context = bundleContext;
		//
		System.out.println("TODO Additions: v1400");
		System.out.println("\tPeak Quantitation References");
		System.out.println("\tBaseline Model Changes");
		//
		logger.info("-------------------------------------------------");
		logger.info("Ensure backward and forward compatibility!");
		logger.info("-------------------------------------------------");
		logger.info("*.ocb - measurement data container");
		logger.info("*.ocm - process method container");
		logger.info("*.ocq - quanititation table container");
		logger.info("*.ocs - sequence data container");
		logger.info("-------------------------------------------------");
		logger.info("--- CHANGES MUST BE APPROVED BY PHILIP WENIG ---");
		logger.info("-------------------------------------------------");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {

		Activator.context = null;
	}
}