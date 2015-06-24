/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.model.IDatabaseProxy;
import org.eclipse.chemclipse.database.model.IDatabases;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
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
		createDatabaseOnDemand();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {

		Activator.context = null;
	}

	/**
	 * Creates a new database if it doesn't exists.
	 */
	private void createDatabaseOnDemand() {

		IPreferenceSupplier preferenceSupplier = PreferenceSupplier.INSTANCE();
		IDatabases databases = new QuantDatabases();
		if(preferenceSupplier instanceof PreferenceSupplier) {
			PreferenceSupplier ps = (PreferenceSupplier)preferenceSupplier;
			IDatabaseProxy databaseProxy = ps.getDatabaseProxy();
			String name = databaseProxy.getDatabaseUrl();
			try {
				databases.createDatabase(name, "This database has been created by default.");
			} catch(NoDatabaseAvailableException e) {
				logger.warn(e);
			}
		}
	}
}
