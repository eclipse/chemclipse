/*******************************************************************************
 * Copyright (c) 2010, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.process.supplier.batchprocess.core.IBatchProcess;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static final AtomicReference<ServiceTracker<IBatchProcess, IBatchProcess>> batchProcessTracker = new AtomicReference<>();

	public static BundleContext getContext() {

		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {

		ServiceTracker<IBatchProcess, IBatchProcess> serviceTracker = new ServiceTracker<>(bundleContext, IBatchProcess.class, null);
		serviceTracker.open();
		ServiceTracker<IBatchProcess, IBatchProcess> tracker = batchProcessTracker.getAndSet(serviceTracker);
		if(tracker != null) {
			tracker.close();
		}
		Activator.context = bundleContext;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {

		ServiceTracker<IBatchProcess, IBatchProcess> tracker = batchProcessTracker.getAndSet(null);
		if(tracker != null) {
			tracker.close();
		}
		Activator.context = null;
	}

	/**
	 *
	 * @return an array holding all currently active {@link IBatchProcess}
	 */
	public static IBatchProcess[] getBatchProcesses() {

		ServiceTracker<IBatchProcess, IBatchProcess> tracker = batchProcessTracker.get();
		if(tracker != null) {
			return tracker.getServices(new IBatchProcess[0]);
		}

		return new IBatchProcess[0];
	}
}
