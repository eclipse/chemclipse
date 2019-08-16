/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.IMethod;

public class TripleQuadMethod implements IMethod {

	/*
	 * These are default values for TripleQuad instruments.
	 */
	private String instrumentName = "QQQ";
	private String ionSource = "EI";
	private String stopMode = "ByChromatographTime";
	private int stopTime = 60000; // milliseconds => 1s
	private int solventDelay = 360000; // milliseconds => 6s
	private boolean collisionGasOn = true;
	private int timeFilterPeakWidth = 700; // milliseconds => 0.011666666666666666s
	private double sourceHeater = 230;
	private double samplingRate = 5;

	@Override
	public String getInstrumentName() {

		return instrumentName;
	}

	@Override
	public void setInstrumentName(String instrumentName) {

		this.instrumentName = instrumentName;
	}

	@Override
	public String getIonSource() {

		return ionSource;
	}

	@Override
	public void setIonSource(String ionSource) {

		this.ionSource = ionSource;
	}

	@Override
	public String getStopMode() {

		return stopMode;
	}

	@Override
	public void setStopMode(String stopMode) {

		this.stopMode = stopMode;
	}

	@Override
	public int getStopTime() {

		return stopTime;
	}

	@Override
	public void setStopTime(int stopTime) {

		this.stopTime = stopTime;
	}

	@Override
	public int getSolventDelay() {

		return solventDelay;
	}

	@Override
	public void setSolventDelay(int solventDelay) {

		this.solventDelay = solventDelay;
	}

	@Override
	public boolean isCollisionGasOn() {

		return collisionGasOn;
	}

	@Override
	public void setCollisionGasOn(boolean collisionGasOn) {

		this.collisionGasOn = collisionGasOn;
	}

	@Override
	public int getTimeFilterPeakWidth() {

		return timeFilterPeakWidth;
	}

	@Override
	public void setTimeFilterPeakWidth(int timeFilterPeakWidth) {

		this.timeFilterPeakWidth = timeFilterPeakWidth;
	}

	@Override
	public double getSourceHeater() {

		return sourceHeater;
	}

	@Override
	public void setSourceHeater(double sourceHeater) {

		this.sourceHeater = sourceHeater;
	}

	@Override
	public double getSamplingRate() {

		return samplingRate;
	}

	@Override
	public void setSamplingRate(double samplingRate) {

		this.samplingRate = samplingRate;
	}
}
