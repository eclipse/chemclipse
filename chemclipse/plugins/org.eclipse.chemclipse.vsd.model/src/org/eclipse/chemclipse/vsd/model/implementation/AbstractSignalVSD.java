/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder differentiate transmission vs absorbance
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractSignal;
import org.eclipse.chemclipse.vsd.model.core.ISignalVSD;

/*
 * Vibrational Spectroscopy Signal: Infrared or Raman
 */
public abstract class AbstractSignalVSD extends AbstractSignal implements ISignalVSD, Comparable<ISignalVSD> {

	private static final long serialVersionUID = -2575735757102126907L;
	private double wavenumber = 0.0d; // 1/cm

	public AbstractSignalVSD(double wavenumber) {

		this.wavenumber = wavenumber;
	}

	@Override
	public double getX() {

		return wavenumber;
	}

	@Override
	public double getWavenumber() {

		return wavenumber;
	}

	@Override
	public void setWavenumber(double wavenumber) {

		/*
		 * Raman could be also negative,
		 * hence no check.
		 */
		this.wavenumber = wavenumber;
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
		AbstractSignalVSD other = (AbstractSignalVSD)obj;
		return (Double.doubleToLongBits(wavenumber) == Double.doubleToLongBits(other.wavenumber));
	}

	@Override
	public String toString() {

		return "AbstractSignalVS [wavenumber=" + wavenumber + "]";
	}

	@Override
	public int compareTo(ISignalVSD signal) {

		if(signal != null) {
			return Double.compare(wavenumber, signal.getWavenumber());
		} else {
			return 0;
		}
	}
}