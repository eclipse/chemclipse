/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.NoQuantitationTableAvailableException;

public class QuantDatabaseProxy implements IQuantDatabaseProxy {

	private String databaseName;

	public QuantDatabaseProxy(String databaseName) {
		this.databaseName = databaseName;
	}

	@Override
	public String getDatabaseName() {

		return databaseName;
	}

	@Override
	public String getDatabaseUrl() {

		return QuantDatabases.getStoragePath().getAbsolutePath();
	}

	@Override
	public IQuantDatabase getDatabase() throws NoQuantitationTableAvailableException {

		IQuantDatabase database = QuantDatabases.getQuantDatabase(databaseName);
		return database;
	}
}
