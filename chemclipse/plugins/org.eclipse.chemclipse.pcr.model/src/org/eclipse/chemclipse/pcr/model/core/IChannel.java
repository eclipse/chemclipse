/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.numeric.core.IPoint;

public interface IChannel {

	String CHANNEL = "Channel";

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	int getTime();

	void setTime(int time);

	double getTemperature();

	void setTemperature(double temperature);

	boolean isValid();

	void setValid(boolean valid);

	List<Double> getPoints();

	void setPoints(List<Double> points);

	IPoint getCrossingPoint();

	void setCrossingPoint(IPoint crossingPoint);

	String getDetectionName();

	void setDetectionName(String detectionName);

	double getCrossingPointCalculated();

	void setCrossingPointCalculated(double crossingPointCalculated);
}