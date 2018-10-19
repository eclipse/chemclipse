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

import java.util.List;

public interface IChannel {

	int getId();

	void setId(int id);

	int getTime();

	void setTime(int time);

	double getTemperature();

	void setTemperature(double temperature);

	boolean isValid();

	void setValid(boolean valid);

	List<Double> getPoints();

	void setPoints(List<Double> points);

	double getCrossingPoint();

	void setCrossingPoint(double crossingPoint);
}