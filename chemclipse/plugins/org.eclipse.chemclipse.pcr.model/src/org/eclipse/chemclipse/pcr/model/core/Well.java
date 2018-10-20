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
package org.eclipse.chemclipse.pcr.model.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;

public class Well implements IWell {

	private static final Logger logger = Logger.getLogger(Well.class);
	//
	private Position position = new Position();
	private Map<Integer, IChannel> channels = new HashMap<>();
	private Map<String, String> data = new HashMap<>();
	private Set<String> protectKeys = new HashSet<>();

	public Well() {
		protectKeys.add(SAMPLE_ID);
		protectKeys.add(TARGET_NAME);
		protectKeys.add(CROSSING_POINT);
		protectKeys.add(SAMPLE_SUBSET);
	}

	@Override
	public Position getPosition() {

		return position;
	}

	@Override
	public Map<Integer, IChannel> getChannels() {

		return channels;
	}

	@Override
	public Map<String, String> getData() {

		return Collections.unmodifiableMap(data);
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

	@Override
	public int compareTo(IWell well) {

		if(well != null) {
			return Integer.compare(position.getId(), well.getPosition().getId());
		} else {
			return 0;
		}
	}

	@Override
	public String getSampleId() {

		return getData(SAMPLE_ID, "").trim();
	}

	@Override
	public String getTargetName() {

		return getData(TARGET_NAME, "").trim();
	}

	@Override
	public double getCrossingPoint() {

		String value = getData(CROSSING_POINT, "0");
		double result = 0.0d;
		try {
			result = Double.parseDouble(value);
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return result;
	}

	@Override
	public boolean isEmptyMeasurement() {

		return ("".equals(getSampleId().trim()) || "_".equals(getSampleId().trim()));
	}

	@Override
	public boolean isPositiveMeasurement() {

		for(IChannel channel : getChannels().values()) {
			if(channel.getCrossingPoint() != null) {
				return true;
			}
		}
		return false;
	}
}
