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
package org.eclipse.chemclipse.database.preferences;

import org.eclipse.chemclipse.database.model.IDatabaseProxy;
import org.eclipse.chemclipse.database.model.IDatabaseSettings;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;

public interface IDatabasePreferences {

	/*
	 * Preferences
	 */
	String P_USE_DATABASE_CONNECTION = "useDatabaseConnection";
	String DEF_USE_DATABASE_CONNECTION = DatabasePathHelper.LOCAL_DB_PREFIX;
	//
	String P_SELECTED_DATABASE = "selectedDatabase";
	String DEF_SELECTED_DATABASE = DatabasePathHelper.LOCAL_DB_PREFIX + "DefaultDB";
	String P_SELECTED_SERVER_REMOTE = "selectedDatabaseServer";
	String DEF_SELECTED_SERVER_REMOTE = "";
	String P_SELECTED_USER_REMOTE = "selectedUserRemote";
	String DEF_SELECTED_USER_REMOTE = "";
	String P_SELECTED_PASSWORD_REMOTE = "selectedPasswordRemote";
	String DEF_SELECTED_PASSWORD_REMOTE = "";

	/**
	 * Returns the selected local database.
	 * 
	 * @return String
	 */
	String getSelectedDatabase();

	/**
	 * Sets the selected local database.
	 * 
	 * @param selectedDatabase
	 */
	void setSelectedDatabase(String selectedDatabase);

	IDatabaseSettings getDatabaseSettings();

	IDatabaseProxy getDatabaseProxy();

	boolean isRemoteConnection();

	String getSelectedServer();

	String getSelectedUserRemote();

	String getSelectedPasswordRemote();

	void setDatabase(IDatabaseProxy chromIdentDatabaseProxy);
}
