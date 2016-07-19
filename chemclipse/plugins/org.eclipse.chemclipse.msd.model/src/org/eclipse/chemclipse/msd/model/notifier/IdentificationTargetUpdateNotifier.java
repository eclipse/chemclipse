/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.notifier;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("restriction")
public class IdentificationTargetUpdateNotifier {

	private static DynamicIdentificationTargetUpdateNotifier dynamicUpdateNotifier;

	/**
	 * This class should not be instantiated.
	 */
	private IdentificationTargetUpdateNotifier() {
	}

	/**
	 * Call this method to inform all broker listeners about a
	 * mass spectrum selection update.<br/>
	 * 
	 * @param massSpectrum
	 * @param forceReload
	 */
	public static void fireUpdateChange(IScanMSD massSpectrum, IIdentificationTarget identificationTarget) {

		/*
		 * Create the dynamic update notifier if it has been not created yet.
		 */
		if(dynamicUpdateNotifier == null) {
			Bundle bundle = FrameworkUtil.getBundle(IdentificationTargetUpdateNotifier.class);
			BundleContext bundleContext = bundle.getBundleContext();
			IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
			eclipseContext.set(Logger.class, null);
			dynamicUpdateNotifier = ContextInjectionFactory.make(DynamicIdentificationTargetUpdateNotifier.class, eclipseContext);
		}
		/*
		 * Send an event using the event broker mechanism of e4.
		 */
		dynamicUpdateNotifier.update(massSpectrum, identificationTarget);
	}
}
