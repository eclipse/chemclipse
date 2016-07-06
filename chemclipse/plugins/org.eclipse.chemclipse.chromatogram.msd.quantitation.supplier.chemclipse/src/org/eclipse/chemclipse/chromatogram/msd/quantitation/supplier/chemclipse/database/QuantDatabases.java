/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Janos Binder - new features
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.NoQuantitationTableAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;

public class QuantDatabases implements IQuantDatabases {

	public static final String IDENTIFIER_QUANTITATION = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse";

	@Override
	public IQuantDatabase getQuantDatabase() throws NoQuantitationTableAvailableException {

		String databaseName = PreferenceSupplier.getSelectedQuantitationTable();
		return getQuantDatabase(databaseName);
	}

	@Override
	public IQuantDatabase getQuantDatabase(String databaseName) throws NoQuantitationTableAvailableException {

		IQuantDatabase quantDatabase = new QuantDatabase();
		System.out.println("Load quant database: " + databaseName);
		return quantDatabase;
	}

	@Override
	public List<String> getDatabaseNames() throws NoQuantitationTableAvailableException {

		List<String> databaseNames = new ArrayList<String>();
		databaseNames.add("Read DBs locally");
		return databaseNames;
	}

	@Override
	public List<IQuantDatabaseProxy> listAvailableDatabaseProxies() throws NoQuantitationTableAvailableException {

		List<IQuantDatabaseProxy> databaseProxies = new ArrayList<IQuantDatabaseProxy>();
		List<String> databaseNames = getDatabaseNames();
		for(String databaseName : databaseNames) {
			databaseProxies.add(new QuantDatabaseProxy(databaseName));
		}
		return databaseProxies;
	}
}
