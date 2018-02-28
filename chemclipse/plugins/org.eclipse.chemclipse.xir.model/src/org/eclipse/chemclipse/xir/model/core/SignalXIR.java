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
package org.eclipse.chemclipse.xir.model.core;

import org.eclipse.chemclipse.model.core.AbstractSignal;

public class SignalXIR extends AbstractSignal implements ISignalXIR, Comparable<ISignalXIR> {

	private static final long serialVersionUID = -2575735757102126907L;
	//
	private double wavelength = 0.0d; // nm
	private double intensity = 0.0d;

	public SignalXIR() {
	}

	public SignalXIR(double wavelength, double intensity) {
		this.wavelength = wavelength;
		this.intensity = intensity;
	}

	@Override
	public double getX() {

		return wavelength;
	}

	@Override
	public double getY() {

		return intensity;
	}

	@Override
	public double getWavelength() {

		return wavelength;
	}

	@Override
	public void setWavelength(double wavelength) {

		if(wavelength >= 0) {
			this.wavelength = wavelength;
		}
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
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(wavelength);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		SignalXIR other = (SignalXIR)obj;
		if(Double.doubleToLongBits(wavelength) != Double.doubleToLongBits(other.wavelength))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "SignalXIR [wavelength=" + wavelength + ", intensity=" + intensity + "]";
	}

	@Override
	public int compareTo(ISignalXIR signalXIR) {

		if(signalXIR != null) {
			return Double.compare(wavelength, signalXIR.getWavelength());
		} else {
			return 0;
		}
	}
}
