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
package org.eclipse.chemclipse.database.ui.internal.provider;

import java.util.Comparator;

import org.eclipse.chemclipse.database.model.IDatabaseProxy;

public class DatabaseProxyComparator implements Comparator<IDatabaseProxy> {

	@Override
	public int compare(IDatabaseProxy proxy1, IDatabaseProxy proxy2) {

		return proxy1.getDatabaseName().compareTo(proxy2.getDatabaseName());
	}
}
