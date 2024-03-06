/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.vsd.filter.model;

import java.util.Objects;

public class WavenumberSignal {

	private double wavenumber = 0.0d;
	private double intensity = 0.0d;

	public void copyFrom(WavenumberSignal wavenumberSignal) {

		this.wavenumber = wavenumberSignal.getWavenumber();
		this.intensity = wavenumberSignal.getIntensity();
	}

	public double getWavenumber() {

		return wavenumber;
	}

	public void setWavenumber(double wavenumber) {

		this.wavenumber = wavenumber;
	}

	public double getIntensity() {

		return intensity;
	}

	public void setIntensity(double intensity) {

		this.intensity = intensity;
	}

	@Override
	public int hashCode() {

		return Objects.hash(wavenumber);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		WavenumberSignal other = (WavenumberSignal)obj;
		return Double.doubleToLongBits(wavenumber) == Double.doubleToLongBits(other.wavenumber);
	}

	@Override
	public String toString() {

		return "WavenumberSignal [wavenumber=" + wavenumber + ", intensity=" + intensity + "]";
	}
}