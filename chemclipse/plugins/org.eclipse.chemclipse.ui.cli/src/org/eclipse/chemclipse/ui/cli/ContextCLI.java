/*******************************************************************************
 * Copyright (c) 2020, 2022 Christoph Läubrich.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - adjust bundle/class naming conventions
 *******************************************************************************/
package org.eclipse.chemclipse.ui.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.E4ProcessSupplierContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Global CLI Exchange object
 */
public class ContextCLI implements BundleActivator {

	private static List<IChromatogram<?>> CHROMATOGRAMS = new ArrayList<IChromatogram<?>>();
	private static IProcessSupplierContext processSupplierContext;
	private static ServiceTracker<IProcessSupplierContext, IProcessSupplierContext> serviceTracker;

	@Inject
	synchronized void setE4ProcessSupplierContext(E4ProcessSupplierContext e4Context) {

		processSupplierContext = e4Context;
	}

	public static synchronized void addChromatogram(IChromatogram<?> chromatogram) {

		CHROMATOGRAMS.add(chromatogram);
	}

	public static synchronized List<IChromatogram<?>> getChromatograms() {

		return Collections.unmodifiableList(CHROMATOGRAMS);
	}

	public synchronized static IProcessSupplierContext getProcessSupplierContext() {

		if(processSupplierContext != null) {
			return processSupplierContext;
		}
		if(serviceTracker != null) {
			try {
				return serviceTracker.waitForService(TimeUnit.SECONDS.toMillis(30));
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return null;
	}

	@Override
	public synchronized void start(BundleContext context) throws Exception {

		serviceTracker = new ServiceTracker<>(context, IProcessSupplierContext.class, null);
		serviceTracker.open();
	}

	@Override
	public synchronized void stop(BundleContext context) throws Exception {

		serviceTracker.close();
	}
}
