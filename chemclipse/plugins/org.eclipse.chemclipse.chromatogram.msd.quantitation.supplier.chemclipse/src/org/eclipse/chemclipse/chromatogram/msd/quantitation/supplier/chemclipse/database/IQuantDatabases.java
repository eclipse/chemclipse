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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.NoQuantitationTableAvailableException;

public interface IQuantDatabases {

	IQuantDatabase getQuantDatabase() throws NoQuantitationTableAvailableException;

	IQuantDatabase getQuantDatabase(String databaseName) throws NoQuantitationTableAvailableException;

	List<String> getDatabaseNames() throws NoQuantitationTableAvailableException;

	List<IQuantDatabaseProxy> listAvailableDatabaseProxies() throws NoQuantitationTableAvailableException;
}
