/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.ranges;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TimeRanges {

	/*
	 * The map is used to have an easier access to the stored time ranges.
	 * To keep the map and time range instances in sync, this put method
	 * is used.
	 */
	private final Map<String, TimeRange> timeRangeMap = new HashMap<>();

	public void put(TimeRange timeRange) {

		timeRangeMap.put(timeRange.getIdentifier(), timeRange);
	}

	public TimeRange get(String identifier) {

		return timeRangeMap.get(identifier);
	}

	public Collection<String> listKeys() {

		return Collections.unmodifiableCollection(timeRangeMap.keySet());
	}

	public Collection<TimeRange> listValues() {

		return Collections.unmodifiableCollection(timeRangeMap.values());
	}

	public void clear() {

		timeRangeMap.clear();
	}
}
