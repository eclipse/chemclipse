/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.database.model;

import org.eclipse.chemclipse.database.support.DatabasePathHelper;

public class DatabaseSettings implements IDatabaseSettings {

	private String url;
	private String username;
	private String password;

	public DatabaseSettings(String url, String username, String password) {
		this.url = url;
		this.username = DatabasePathHelper.isRemoteDatabasePath(url) ? username : AbstractDatabase.DEFAULT_USER_NAME;
		this.password = DatabasePathHelper.isRemoteDatabasePath(url) ? password : AbstractDatabase.DEFAULT_USER_PASSWORD;
	}

	@Override
	public String getPassword() {

		return password;
	}

	@Override
	public String getUrl() {

		return url;
	}

	@Override
	public String getUsername() {

		return username;
	}

	@Override
	public boolean isRemoteDatabase() {

		return DatabasePathHelper.isRemoteDatabasePath(url);
	}
}
