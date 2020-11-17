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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;

public abstract class AbstractDataModel implements IDataModel {

	private Map<String, String> data = new HashMap<>();
	private Set<String> protectKeys = new HashSet<>();

	@Override
	public void addProtectedKey(String key) {

		protectKeys.add(key);
	}

	@Override
	public Map<String, String> getData() {

		return data;
	}

	@Override
	public void putData(String key, String value) {

		data.put(key, value);
	}

	@Override
	public String getData(String key, String defaultValue) {

		return data.getOrDefault(key, defaultValue);
	}

	@Override
	public void setData(String key, String value) {

		if(key != null && value != null) {
			data.put(key, value);
		}
	}

	@Override
	public void removeData(String key) throws InvalidHeaderModificationException {

		if(protectKeys.contains(key)) {
			throw new InvalidHeaderModificationException("It's not possible to remove the following key: " + key);
		} else {
			data.remove(key);
		}
	}
}
