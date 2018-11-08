/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.Objects;

public class SignalFID implements ISignalFID {

	private int time;
	private double magnitude;
	private double intensity;

	public SignalFID(int time, double magnitude) {

		super();
		this.time = time;
		this.magnitude = magnitude;
		this.intensity = magnitude;
	}

	@Override
	public double getX() {

		return time;
	}

	@Override
	public double getY() {

		return intensity;
	}

	@Override
	public double getIntensity() {

		return intensity;
	}

	@Override
	public void setIntensity(double intensity) {

		this.intensity = intensity;
	}

	@Override
	public void resetIntensity() {

		intensity = magnitude;
	}

	@Override
	public void setMagnitude(double magnitude) {

		this.magnitude = magnitude;
	}

	@Override
	public double getMagnitude() {

		return magnitude;
	}

	@Override
	public int getTime() {

		return time;
	}

	@Override
	public void setTime(int milliseconds) {

		this.time = milliseconds;
	}

	@Override
	public int hashCode() {

		return Objects.hash(time);
	}
}
