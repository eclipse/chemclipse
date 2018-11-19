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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;

public class Well extends AbstractDataModel implements IWell {

	private static final Logger logger = Logger.getLogger(Well.class);
	//
	private Position position = new Position();
	private Map<Integer, IChannel> channels = new HashMap<>();

	public Well() {
		addProtectedKey(SAMPLE_ID);
		addProtectedKey(TARGET_NAME);
		addProtectedKey(CROSSING_POINT);
		addProtectedKey(SAMPLE_SUBSET);
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
