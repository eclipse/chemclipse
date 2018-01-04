/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;

@SuppressWarnings("restriction")
public class IntegrationResultUpdateNotifier {

	private static DynamicIntegrationResultUpdateNotifier dynamicUpdateNotifier;

	/**
	 * This class should not be instantiated.
	 */
	private IntegrationResultUpdateNotifier() {
	}

	public static void fireUpdateChange(ICombinedIntegrationResult combinedIntegrationResult) {

		/*
		 * Send an event using the event broker mechanism of e4.
		 */
		validateDynamicUpdateNotifier();
		dynamicUpdateNotifier.update(combinedIntegrationResult);
	}

	public static void fireUpdateChange(IChromatogramIntegrationResults chromatogramIntegrationResults) {

		/*
		 * Send an event using the event broker mechanism of e4.
		 */
		validateDynamicUpdateNotifier();
		dynamicUpdateNotifier.update(chromatogramIntegrationResults);
	}

	public static void fireUpdateChange(IPeakIntegrationResults peakIntegrationResults) {

		/*
		 * Send an event using the event broker mechanism of e4.
		 */
		validateDynamicUpdateNotifier();
		dynamicUpdateNotifier.update(peakIntegrationResults);
	}

	private static void validateDynamicUpdateNotifier() {

		/*
		 * Create the dynamic update notifier if it has been not created yet.
		 */
		if(dynamicUpdateNotifier == null) {
			Bundle bundle = FrameworkUtil.getBundle(ChromatogramSelectionMSDUpdateNotifier.class);
			BundleContext bundleContext = bundle.getBundleContext();
			IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
			eclipseContext.set(Logger.class, null);
			dynamicUpdateNotifier = ContextInjectionFactory.make(DynamicIntegrationResultUpdateNotifier.class, eclipseContext);
		}
	}
}
