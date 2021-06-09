/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add color compensation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.ArrayList;
import java.util.List;

public class Channel implements IChannel {

	private int id = -1;
	private String name = "";
	private int time = 0;
	private double temperature = 0.0d;
	private boolean valid = false;
	private List<Double> fluorescence = new ArrayList<>();
	private List<Double> colorCompensatedFluorescence = new ArrayList<>();
	private double crossingPoint = 0.0d;
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
	public List<Double> getFluorescence() {

		return fluorescence;
	}

	@Override
	public void setFluorescence(List<Double> flurorescence) {

		this.fluorescence = flurorescence;
	}

	@Override
	public List<Double> getColorCompensatedFluorescence() {

		return colorCompensatedFluorescence;
	}

	@Override
	public void setColorCompensatedFluorescence(List<Double> colorCompensations) {

		this.colorCompensatedFluorescence = colorCompensations;
	}

	@Override
	public double getCrossingPoint() {

		return crossingPoint;
	}

	@Override
	public void setCrossingPoint(double crossingPoint) {

		this.crossingPoint = crossingPoint;
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