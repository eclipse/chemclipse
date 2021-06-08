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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public class Channel implements IChannel {

	private int id = -1;
	private String name = "";
	private int time = 0;
	private double temperature = 0.0d;
	private boolean valid = false;
	private List<Double> points = new ArrayList<>();
	private IPoint crossingPoint = null;
	private String detectionName = "";

	@Override
	public int getId() {

		return id;
	}

	@Override
	public void setId(int id) {

		this.id = id;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
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
	public IPoint getCrossingPoint() {

		return crossingPoint;
	}

	@Override
	public void setCrossingPoint(IPoint crossingPoint) {

		this.crossingPoint = crossingPoint;
	}

	@Override
	public void setCrossingPoint(double crossingPointX) {

		int floor = (int)Math.floor(crossingPointX);
		int ceil = (int)Math.ceil(crossingPointX);
		//
		if(floor >= 0 && floor < points.size() && ceil >= 0 && ceil < points.size()) {
			if(floor == ceil) {
				double y = points.get(floor);
				crossingPoint = new Point(crossingPointX, y);
			} else {
				IPoint p1 = new Point(floor, points.get(floor));
				IPoint p2 = new Point(ceil, points.get(ceil));
				LinearEquation equation = Equations.createLinearEquation(p1, p2);
				double y = equation.calculateY(crossingPointX);
				crossingPoint = new Point(crossingPointX, y);
			}
		}
	}

	@Override
	public String getDetectionName() {

		return detectionName;
	}

	@Override
	public void setDetectionName(String detectionName) {

		this.detectionName = detectionName;
	}
}
