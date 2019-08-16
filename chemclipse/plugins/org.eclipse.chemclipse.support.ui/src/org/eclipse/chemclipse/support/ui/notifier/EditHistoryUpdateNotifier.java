/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.notifier;

import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.ui.Activator;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.osgi.framework.BundleContext;

@SuppressWarnings("restriction")
public class EditHistoryUpdateNotifier {

	private static DynamicEditHistoryUpdateNotifier notifier;

	private EditHistoryUpdateNotifier() {
	}

	public static void fireUpdateChange(IEditHistory editHistory) {

		if(notifier == null) {
			BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
			IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
			eclipseContext.set(Logger.class, null);
			notifier = ContextInjectionFactory.make(DynamicEditHistoryUpdateNotifier.class, eclipseContext);
		}
		/*
		 * Updates all listener.
		 */
		notifier.update(editHistory);
	}
}
