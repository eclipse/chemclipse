/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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
package org.eclipse.chemclipse.xir.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractSignal;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;

/*
 * It's either FT-IR/NIR/MIR or Raman
 */
public abstract class AbstractSignalXIR extends AbstractSignal implements ISignalXIR, Comparable<ISignalXIR> {

	private static final long serialVersionUID = -2575735757102126907L;
	private double wavenumber = 0.0d; // 1/cm

	public AbstractSignalXIR(double wavenumber) {

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
	public void setWavenumber(double wavelength) {

		if(wavelength >= 0) {
			this.wavenumber = wavelength;
		}
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
		AbstractSignalXIR other = (AbstractSignalXIR)obj;
		return (Double.doubleToLongBits(wavenumber) == Double.doubleToLongBits(other.wavenumber));
	}

	@Override
	public String toString() {

		return "AbstractSignalXIR [wavenumber=" + wavenumber + "]";
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