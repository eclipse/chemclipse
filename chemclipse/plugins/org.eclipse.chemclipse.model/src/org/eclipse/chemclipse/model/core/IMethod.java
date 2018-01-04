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
package org.eclipse.chemclipse.model.core;

public interface IMethod {

	String getInstrumentName();

	void setInstrumentName(String instrumentName);

	String getIonSource();

	void setIonSource(String ionSource);

	String getStopMode();

	void setStopMode(String stopMode);

	int getStopTime();

	void setStopTime(int stopTime);

	int getSolventDelay();

	void setSolventDelay(int solventDelay);

	boolean isCollisionGasOn();

	void setCollisionGasOn(boolean collisionGasOn);

	int getTimeFilterPeakWidth();

	void setTimeFilterPeakWidth(int timeFilterPeakWidth);

	double getSourceHeater();

	void setSourceHeater(double sourceHeater);

	double getSamplingRate();

	void setSamplingRate(double samplingRate);
}