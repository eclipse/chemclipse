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
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.core;

import org.eclipse.chemclipse.model.core.AbstractSignal;

public class SignalXIR extends AbstractSignal implements ISignalXIR, Comparable<ISignalXIR> {

	private static final long serialVersionUID = -2575735757102126907L;
	//
	private double wavenumber = 0.0d; // 1/cm
	private double intensity = 0.0d;

	public SignalXIR() {

	}

	public SignalXIR(double wavelength, double intensity) {

		this.wavenumber = wavelength;
		this.intensity = intensity;
	}

	@Override
	public double getX() {

		return wavenumber;
	}

	@Override
	public double getY() {

		return intensity;
	}

	@Override
	public double getWavenumber() {

		return wavenumber;
	}

	@Override
	public void setWavenumber(double wavelength) {

		if(wavelength >= 0) {
			this.wavenumber = wavelength;
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
		temp = Double.doubleToLongBits(wavenumber);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		SignalXIR other = (SignalXIR)obj;
		return (Double.doubleToLongBits(wavenumber) == Double.doubleToLongBits(other.wavenumber));
	}

	@Override
	public String toString() {

		return "SignalXIR [wavenumber=" + wavenumber + ", intensity=" + intensity + "]";
	}

	@Override
	public int compareTo(ISignalXIR signalXIR) {

		if(signalXIR != null) {
			return Double.compare(wavenumber, signalXIR.getWavenumber());
		} else {
			return 0;
		}
	}
}
