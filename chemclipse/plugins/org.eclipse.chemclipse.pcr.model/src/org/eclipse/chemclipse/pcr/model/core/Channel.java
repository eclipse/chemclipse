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

import java.util.ArrayList;
import java.util.List;

public class Channel implements IChannel {

	private int id = -1;
	private int time = 0;
	private double temperature = 0.0d;
	private boolean valid = false;
	private List<Double> points = new ArrayList<>();
	private double crossingPoint = 0.0d;

	@Override
	public int getId() {

		return id;
	}

	@Override
	public void setId(int id) {

		this.id = id;
	}

	@Override
	public int getTime() {

		return time;
	}

	@Override
	public void setTime(int time) {

		this.time = time;
	}

	@Override
	public double getTemperature() {

		return temperature;
	}

	@Override
	public void setTemperature(double temperature) {

		this.temperature = temperature;
	}

	@Override
	public boolean isValid() {

		return valid;
	}

	@Override
	public void setValid(boolean valid) {

		this.valid = valid;
	}

	@Override
	public List<Double> getPoints() {

		return points;
	}

	@Override
	public void setPoints(List<Double> points) {

		this.points = points;
	}

	@Override
	public double getCrossingPoint() {

		return crossingPoint;
	}

	@Override
	public void setCrossingPoint(double crossingPoint) {

		this.crossingPoint = crossingPoint;
	}
}
