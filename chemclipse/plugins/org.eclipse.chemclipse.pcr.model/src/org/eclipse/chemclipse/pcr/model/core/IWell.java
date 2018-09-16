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

import java.util.Map;

import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;

public interface IWell extends Comparable<IWell> {

	String SAMPLE_ID = "Sample ID";
	String TARGET_NAME = "Target Name";
	String CROSSING_POINT = "Crossing Point";
	String SAMPLE_SUBSET = "Sample Subset";

	Position getPosition();

	Map<Integer, IChannel> getChannels();

	Map<String, String> getData();

	String getData(String key, String defaultValue);

	void setData(String key, String value);

	void removeData(String key) throws InvalidHeaderModificationException;

	String getSampleId();

	String getTargetName();

	double getCrossingPoint();

	boolean isEmptyMeasurement();
}
