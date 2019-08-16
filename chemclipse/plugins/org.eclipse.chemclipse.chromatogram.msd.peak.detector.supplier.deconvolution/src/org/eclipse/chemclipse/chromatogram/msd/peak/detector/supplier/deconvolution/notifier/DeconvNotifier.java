/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.notifier;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArraysViewDeconv;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("restriction")
public class DeconvNotifier {

	private static DynamicDeconvNotifier dynamicUpdateNotifier;

	private DeconvNotifier() {
	}

	public static void fireUpdate(IArraysViewDeconv arraysViewDeconv) {

		if(dynamicUpdateNotifier == null) {
			Bundle bundle = FrameworkUtil.getBundle(DeconvNotifier.class);
			BundleContext bundleContext = bundle.getBundleContext();
			IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
			eclipseContext.set(Logger.class, null);
			dynamicUpdateNotifier = ContextInjectionFactory.make(DynamicDeconvNotifier.class, eclipseContext);
		}
		/*
		 * Send an event using the event broker mechanism of e4.
		 */
		dynamicUpdateNotifier.update(arraysViewDeconv);
	}
}
