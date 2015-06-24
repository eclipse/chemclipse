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
 *******************************************************************************/
package org.eclipse.chemclipse.database.model;

import org.eclipse.chemclipse.database.support.DatabasePathHelper;

public class DatabaseProxy implements IDatabaseProxy {

	private String databaseUrl;
	private String databaseName;
	private String username = AbstractDatabase.DEFAULT_USER_NAME;
	private String password = AbstractDatabase.DEFAULT_USER_PASSWORD;

	public DatabaseProxy(String databaseUrl, String databaseName) {

		this.databaseUrl = databaseUrl;
		this.databaseName = databaseName;
	}

	public DatabaseProxy(String databaseUrl, String databaseName, String username, String password) {

		this(databaseUrl, databaseName);
		this.username = DatabasePathHelper.isRemoteDatabasePath(databaseUrl) ? username : AbstractDatabase.DEFAULT_USER_NAME;
		this.password = DatabasePathHelper.isRemoteDatabasePath(databaseUrl) ? password : AbstractDatabase.DEFAULT_USER_PASSWORD;
	}

	public DatabaseProxy(String databaseUrl, String databaseName, IDatabaseSettings databaseSettings) {

		this(databaseUrl, databaseName);
		this.username = DatabasePathHelper.isRemoteDatabasePath(databaseUrl) ? databaseSettings.getUsername() : AbstractDatabase.DEFAULT_USER_NAME;
		this.password = DatabasePathHelper.isRemoteDatabasePath(databaseUrl) ? databaseSettings.getPassword() : AbstractDatabase.DEFAULT_USER_PASSWORD;
	}

	@Override
	public String getDatabaseUrl() {

		return databaseUrl;
	}

	@Override
	public String getDatabaseName() {

		return databaseName;
	}

	@Override
	public String getUsername() {

		return username;
	}

	@Override
	public String getPassword() {

		return password;
	}

	@Override
	public boolean isRemoteDatabase() {

		return DatabasePathHelper.isRemoteDatabasePath(databaseUrl);
	}

	@Override
	public IDatabaseSettings getDatabaseSettings() {

		if(isRemoteDatabase()) {
			String serverUrl = databaseUrl.substring(0, databaseUrl.indexOf("/"));
			return new DatabaseSettings(serverUrl, username, password);
		} else {
			return new DatabaseSettings(databaseUrl, username, password);
		}
	}
}
