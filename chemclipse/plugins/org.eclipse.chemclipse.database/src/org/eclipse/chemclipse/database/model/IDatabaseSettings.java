/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate UG (haftungsbeschränkt).
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

public interface IDatabaseSettings {

	public abstract String getPassword();

	public abstract String getUrl();

	public abstract String getUsername();

	public abstract boolean isRemoteDatabase();
}