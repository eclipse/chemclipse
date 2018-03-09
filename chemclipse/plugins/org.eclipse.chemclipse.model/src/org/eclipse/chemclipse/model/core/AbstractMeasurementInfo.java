/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMeasurementInfo implements IMeasurementInfo {

	private Map<String, Object> headerDataMap = new HashMap<String, Object>();

	@Override
	public Object getHeaderData(String key) {

		return headerDataMap.get(key);
	}

	@Override
	public Object getHeaderDataOrDefault(String key, Object defaultValue) {

		return headerDataMap.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean headerDataContainsKey(String key) {

		return headerDataMap.containsKey(key);
	}

	@Override
	public void putHeaderData(String key, Object value) {

		headerDataMap.put(key, value);
	}

	@Override
	public void removeHeaderData(String key) {

		headerDataMap.remove(key);
	}

	@Override
	public Map<String, Object> getHeaderDataMap() {

		return Collections.unmodifiableMap(headerDataMap);
	}
}
