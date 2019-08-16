/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

public class UserManagement {

	public static String getCurrentUser() {

		return System.getProperty("user.name"); // $NON-NLS-1$
	}

	public static String getUserHome() {

		return System.getProperty("user.home"); // $NON-NLS-1$
	}
}
