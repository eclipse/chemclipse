/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.model.AbstractDatabases;
import org.eclipse.chemclipse.database.model.IDatabaseProxy;
import org.eclipse.chemclipse.database.model.IDatabaseSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class QuantDatabases extends AbstractDatabases implements IQuantDatabases {

	public static final String IDENTIFIER_QUANTITATION = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse";

	public QuantDatabases() {
		super(IDENTIFIER_QUANTITATION);
	}

	@Override
	public void setDatabaseInPreferenceSupplier(IDatabaseProxy chromIdentDatabaseProxy) throws NoDatabaseAvailableException {

		IPreferenceSupplier preferenceSupplier = PreferenceSupplier.INSTANCE();
		if(preferenceSupplier instanceof PreferenceSupplier) {
			PreferenceSupplier ps = (PreferenceSupplier)preferenceSupplier;
			ps.setDatabase(chromIdentDatabaseProxy);
		} else {
			throw new NoDatabaseAvailableException("Something is wrong with the PreferenceSupplier");
		}
	}

	@Override
	public IQuantDatabase getDatabase() throws NoDatabaseAvailableException {

		IPreferenceSupplier preferenceSupplier = PreferenceSupplier.INSTANCE();
		if(preferenceSupplier instanceof PreferenceSupplier) {
			PreferenceSupplier ps = (PreferenceSupplier)preferenceSupplier;
			IDatabaseProxy databaseProxy = ps.getDatabaseProxy();
			return getDatabase(databaseProxy);
		} else {
			throw new NoDatabaseAvailableException("Something is wrong with the PreferenceSupplier");
		}
	}

	@Override
	public IQuantDatabase getDatabase(IDatabaseProxy quantDatabaseProxy) throws NoDatabaseAvailableException {

		if(databaseExists(quantDatabaseProxy)) {
			// return getDatabase(chromIdentDatabaseProxy);
			return new QuantDatabase(quantDatabaseProxy.getDatabaseUrl(), quantDatabaseProxy.getUsername(), quantDatabaseProxy.getPassword());
		} else {
			throw new NoDatabaseAvailableException("The database " + quantDatabaseProxy.getDatabaseUrl() + " is not available or does not exist");
		}
	}

	@Override
	public List<QuantDatabaseProxy> listAvailableDatabaseProxies() throws NoDatabaseAvailableException {

		List<QuantDatabaseProxy> databaseProxies = new ArrayList<QuantDatabaseProxy>();
		// URL, Name pairs
		IPreferenceSupplier preferenceSupplier = PreferenceSupplier.INSTANCE();
		if(preferenceSupplier instanceof PreferenceSupplier) {
			PreferenceSupplier ps = (PreferenceSupplier)preferenceSupplier;
			IDatabaseSettings databaseSettings = ps.getDatabaseSettings();
			Map<String, String> availableDatabases = mapAvailableDatabases(databaseSettings);
			for(Map.Entry<String, String> urlNamePair : availableDatabases.entrySet()) {
				databaseProxies.add(new QuantDatabaseProxy(urlNamePair.getKey(), urlNamePair.getValue(), databaseSettings.getUsername(), databaseSettings.getPassword()));
			}
			return databaseProxies;
		} else {
			throw new NoDatabaseAvailableException("Something is wrong with the PreferenceSupplier");
		}
	}

	@Override
	public List<QuantDatabaseProxy> listAvailableDatabaseProxies(IDatabaseSettings databaseSettings) throws NoDatabaseAvailableException {

		List<QuantDatabaseProxy> databaseProxies = new ArrayList<QuantDatabaseProxy>();
		// URL, Name pairs
		Map<String, String> availableDatabases = mapAvailableDatabases(databaseSettings);
		for(Map.Entry<String, String> urlNamePair : availableDatabases.entrySet()) {
			databaseProxies.add(new QuantDatabaseProxy(urlNamePair.getKey(), urlNamePair.getValue(), databaseSettings.getUsername(), databaseSettings.getPassword()));
		}
		return databaseProxies;
	}

	@Override
	public IQuantDatabase getQuantDatabase() throws NoDatabaseAvailableException {

		return this.getDatabase();
	}

	@Override
	public IQuantDatabase getQuantDatabase(IQuantDatabaseProxy quantDatabaseProxy) throws NoDatabaseAvailableException {

		return this.getDatabase(quantDatabaseProxy);
	}

	@Override
	public List<String> getDatabaseNames() throws NoDatabaseAvailableException {

		IPreferenceSupplier preferenceSupplier = PreferenceSupplier.INSTANCE();
		if(preferenceSupplier instanceof PreferenceSupplier) {
			PreferenceSupplier ps = (PreferenceSupplier)preferenceSupplier;
			return super.getDatabaseNames(ps.getDatabaseSettings());
		} else {
			throw new NoDatabaseAvailableException("Something is wrong with the PreferenceSupplier");
		}
	}
}
