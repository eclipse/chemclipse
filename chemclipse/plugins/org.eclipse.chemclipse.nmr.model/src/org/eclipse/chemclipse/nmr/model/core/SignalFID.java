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

import org.apache.commons.math3.complex.Complex;

public class SignalFID implements ISignalFID {

	private int time;
	private Complex intesityFID;
	private Complex intensity;

	public SignalFID(int time, Complex intesityFID) {

		super();
		this.time = time;
		this.intesityFID = intesityFID;
		this.intensity = intesityFID;
	}

	@Override
	public double getX() {

		return time;
	}

	@Override
	public double getY() {

		return intensity.getReal();
	}

	@Override
	public Complex getIntensity() {

		return intensity;
	}

	@Override
	public void setIntensity(Complex intensity) {

		this.intensity = intensity;
	}

	@Override
	public void resetIntensity() {

		intensity = intesityFID;
	}

	@Override
	public void setIntensityFID(Complex magnitude) {

		this.intesityFID = magnitude;
	}

	@Override
	public Complex getIntensityFID() {

		return intesityFID;
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
