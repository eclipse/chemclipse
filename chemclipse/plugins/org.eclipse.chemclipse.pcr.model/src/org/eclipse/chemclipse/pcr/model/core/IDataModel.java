/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.Map;

import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;

public interface IDataModel {

	void addProtectedKey(String key);

	Map<String, String> getData();

	void putData(String key, String value);

	String getData(String key, String defaultValue);

	void setData(String key, String value);

	void removeData(String key) throws InvalidHeaderModificationException;
}
