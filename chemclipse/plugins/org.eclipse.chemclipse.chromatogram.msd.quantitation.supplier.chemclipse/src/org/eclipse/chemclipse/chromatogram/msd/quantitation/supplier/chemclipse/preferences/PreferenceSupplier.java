/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 * Janos Binder - new features
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.Activator;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabaseProxy;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabaseProxy;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.settings.ChemClipsePeakQuantifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.settings.IChemClipsePeakQuantifierSettings;
import org.eclipse.chemclipse.database.model.DatabaseSettings;
import org.eclipse.chemclipse.database.model.IDatabaseProxy;
import org.eclipse.chemclipse.database.model.IDatabaseSettings;
import org.eclipse.chemclipse.database.preferences.IDatabasePreferences;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier, IDatabasePreferences {

	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_USE_DATABASE_CONNECTION, DEF_USE_DATABASE_CONNECTION);
		defaultValues.put(P_SELECTED_DATABASE, DEF_SELECTED_DATABASE);
		defaultValues.put(P_SELECTED_SERVER_REMOTE, DEF_SELECTED_SERVER_REMOTE);
		defaultValues.put(P_SELECTED_USER_REMOTE, DEF_SELECTED_USER_REMOTE);
		defaultValues.put(P_SELECTED_PASSWORD_REMOTE, DEF_SELECTED_PASSWORD_REMOTE);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static IChemClipsePeakQuantifierSettings getPeakQuantifierSetting() {

		IChemClipsePeakQuantifierSettings peakQuantifierSettings = new ChemClipsePeakQuantifierSettings();
		return peakQuantifierSettings;
	}

	public static boolean useDatabaseRemote() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String connection = preferences.get(P_USE_DATABASE_CONNECTION, DEF_USE_DATABASE_CONNECTION);
		if(connection.equals(DatabasePathHelper.REMOTE_DB_PREFIX)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getSelectedDatabase() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_SELECTED_DATABASE, DEF_SELECTED_DATABASE);
	}

	@Override
	public void setSelectedDatabase(String selectedDatabase) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.put(P_SELECTED_DATABASE, selectedDatabase);
	}

	@Override
	public String getSelectedServer() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String remoteDB = preferences.get(P_SELECTED_SERVER_REMOTE, DEF_SELECTED_SERVER_REMOTE);
		return remoteDB;
	}

	@Override
	public String getSelectedUserRemote() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_SELECTED_USER_REMOTE, DEF_SELECTED_USER_REMOTE);
	}

	@Override
	public String getSelectedPasswordRemote() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_SELECTED_PASSWORD_REMOTE, DEF_SELECTED_PASSWORD_REMOTE);
	}

	@Override
	public IDatabaseSettings getDatabaseSettings() {

		return new DatabaseSettings(getSelectedDatabase(), getSelectedUserRemote(), getSelectedPasswordRemote());
	}

	@Override
	public IQuantDatabaseProxy getDatabaseProxy() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String url = preferences.get(P_SELECTED_DATABASE, DEF_SELECTED_DATABASE);
		return new QuantDatabaseProxy(url, "", getSelectedUserRemote(), getSelectedPasswordRemote());
	}

	@Override
	public void setDatabase(IDatabaseProxy quantDatabaseProxy) {

		String databaseURL = quantDatabaseProxy.getDatabaseUrl();
		String username = quantDatabaseProxy.getUsername();
		String password = quantDatabaseProxy.getPassword();
		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.put(P_SELECTED_DATABASE, databaseURL);
		preferences.put(P_SELECTED_USER_REMOTE, username);
		preferences.put(P_SELECTED_PASSWORD_REMOTE, password);
	}

	@Override
	public boolean isRemoteConnection() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String connection = preferences.get(P_USE_DATABASE_CONNECTION, DEF_USE_DATABASE_CONNECTION);
		return connection.equals(DatabasePathHelper.REMOTE_DB_PREFIX);
	}
}
