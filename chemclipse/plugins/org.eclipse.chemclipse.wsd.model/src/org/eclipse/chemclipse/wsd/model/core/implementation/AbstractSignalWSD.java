/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.implementation;

import org.eclipse.chemclipse.model.core.AbstractSignal;
import org.eclipse.chemclipse.wsd.model.core.ISignalWSD;

/*
 * UV/Vis Spectroscopy
 */
public abstract class AbstractSignalWSD extends AbstractSignal implements ISignalWSD, Comparable<ISignalWSD> {

	private static final long serialVersionUID = -3849935170783926023L;
	private double wavelength = 0.0d; // nm
	private double absorbance = 0;

	public AbstractSignalWSD(double wavelength, double absorbance) {

		this.wavelength = wavelength;
		this.absorbance = absorbance;
	}

	@Override
	public double getX() {

		return wavelength;
	}

	@Override
	public double getY() {

		return absorbance;
	}

	@Override
	public double getWavelength() {

		return wavelength;
	}

	@Override
	public void setWavelength(double wavelength) {

		this.wavelength = wavelength;
	}

	@Override
	public double getAbsorbance() {

		return absorbance;
	}

	@Override
	public void setAbsorbance(double absorbance) {

		this.absorbance = absorbance;
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

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		AbstractSignalWSD other = (AbstractSignalWSD)obj;
		return (Double.doubleToLongBits(wavelength) == Double.doubleToLongBits(other.wavelength));
	}

	@Override
	public String toString() {

		return "AbstractSignalWSD [wavelength=" + wavelength + "]";
	}

	@Override
	public int compareTo(ISignalWSD signal) {

		if(signal != null) {
			return Double.compare(wavelength, signal.getWavelength());
		} else {
			return 0;
		}
	}
}