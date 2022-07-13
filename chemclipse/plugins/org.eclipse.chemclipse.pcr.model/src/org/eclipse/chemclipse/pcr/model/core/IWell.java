/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

public interface IWell extends Comparable<IWell>, IDataModel {

	String SAMPLE_ID = "Sample ID";
	String TARGET_NAME = "Target Name";
	String CROSSING_POINT = "Crossing Point";
	String SAMPLE_SUBSET = "Sample Subset";

	String getLabel();

	/**
	 * Returns null if no active channel is set.
	 * 
	 * @return {@link IChannel}
	 */
	IChannel getActiveChannel();

	/**
	 * Sets the active channel by id.
	 * 
	 * @param activeChannel
	 */
	void setActiveChannel(int activeChannel);

	void clearActiveChannel();

	boolean isActiveSubset();

	void setActiveSubset(String activeSubset);

	void clearActiveSubset();

	Position getPosition();

	Map<Integer, IChannel> getChannels();

	String getSampleId();

	String getSampleSubset();

	String getTargetName();

	double getCrossingPoint();

	boolean isEmptyMeasurement();

	boolean isPositiveMeasurement();

	void applyDetectionFormat(IDetectionFormat detectionFormat);

	IWell makeDeepCopy();
}
