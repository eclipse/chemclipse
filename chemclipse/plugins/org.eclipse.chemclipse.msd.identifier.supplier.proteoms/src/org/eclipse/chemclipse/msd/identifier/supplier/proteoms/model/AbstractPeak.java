/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model;

/**
 * Contain m/z and intensity.
 *
 * @author Janko Diminic
 *
 */
public abstract class AbstractPeak {

	private double mz = 0;
	private double intensity = 0;

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(mz);
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
		AbstractPeak other = (AbstractPeak)obj;
		if(Double.doubleToLongBits(mz) != Double.doubleToLongBits(other.mz))
			return false;
		return true;
	}

	public AbstractPeak(double mz, double intensity) {
		this.setMz(mz);
		this.setIntensity(intensity);
	}

	public double getIntensity() {

		return intensity;
	}

	public void setIntensity(double intensity) {

		this.intensity = intensity;
	}

	public double getMz() {

		return mz;
	}

	public void setMz(double mz) {

		this.mz = mz;
	}
}
