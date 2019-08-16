/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.notifier;

import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("restriction")
public class ChromatogramSelectionWSDUpdateNotifier {

	private static DynamicChromatogramSelectionUpdateNotifier dynamicUpdateNotifier;

	private ChromatogramSelectionWSDUpdateNotifier() {
	}

	public static void fireUpdateChange(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		/*
		 * Create the dynamic update notifier if it has been not created yet.
		 */
		if(dynamicUpdateNotifier == null) {
			Bundle bundle = FrameworkUtil.getBundle(ChromatogramSelectionWSDUpdateNotifier.class);
			BundleContext bundleContext = bundle.getBundleContext();
			IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
			eclipseContext.set(Logger.class, null);
			dynamicUpdateNotifier = ContextInjectionFactory.make(DynamicChromatogramSelectionUpdateNotifier.class, eclipseContext);
		}
		/*
		 * Send an event using the event broker mechanism of e4.
		 */
		dynamicUpdateNotifier.update(chromatogramSelection, forceReload);
	}
}
