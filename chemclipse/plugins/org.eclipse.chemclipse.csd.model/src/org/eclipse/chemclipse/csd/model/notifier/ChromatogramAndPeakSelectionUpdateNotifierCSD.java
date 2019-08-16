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
package org.eclipse.chemclipse.csd.model.notifier;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;

@SuppressWarnings("restriction")
public class ChromatogramAndPeakSelectionUpdateNotifierCSD {

	private static DynamicChromatogramAndPeakSelectionUpdateNotifier dynamicUpdateNotifier;

	/**
	 * This class should not be instantiated.
	 */
	private ChromatogramAndPeakSelectionUpdateNotifierCSD() {
	}

	/**
	 * Call this method to inform all broker listeners about a
	 * selection update.<br/>
	 * See also method update() in {@link ChromatogramSelectionMSD}.<br/>
	 * See also {@link IChromatogramSelectionMSDUpdateNotifier} to reveal the effect of
	 * forceReload.
	 * 
	 * @param chromatogramSelection
	 * @param forceReload
	 */
	public static void fireUpdateChange(IChromatogramSelectionCSD chromatogramSelection, IChromatogramPeakCSD chromatogramPeak, boolean forceReload) {

		/*
		 * Create the dynamic update notifier if it has been not created yet.
		 */
		if(dynamicUpdateNotifier == null) {
			Bundle bundle = FrameworkUtil.getBundle(ChromatogramAndPeakSelectionUpdateNotifierCSD.class);
			BundleContext bundleContext = bundle.getBundleContext();
			IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
			eclipseContext.set(Logger.class, null);
			dynamicUpdateNotifier = ContextInjectionFactory.make(DynamicChromatogramAndPeakSelectionUpdateNotifier.class, eclipseContext);
		}
		/*
		 * Send an event using the event broker mechanism of e4.
		 */
		dynamicUpdateNotifier.update(chromatogramSelection, chromatogramPeak, forceReload);
	}
}
