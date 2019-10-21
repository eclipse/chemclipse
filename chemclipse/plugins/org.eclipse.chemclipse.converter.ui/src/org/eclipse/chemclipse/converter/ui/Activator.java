/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.ui;

import org.eclipse.chemclipse.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends AbstractActivatorUI {

	private static Activator plugin;
	private static ServiceTracker<ProcessSupplierContext, ProcessSupplierContext> tracker;

	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
		tracker = new ServiceTracker<>(context, ProcessSupplierContext.class, null);
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
		tracker.close();
		tracker = null;
	}

	public static AbstractActivatorUI getDefault() {

		return plugin;
	}

	public static ProcessSupplierContext getProcessSupplierContext() {

		if(tracker != null) {
			return tracker.getService();
		} else {
			return null;
		}
	}
}
