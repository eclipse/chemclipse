/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

public class MarkedWavelength implements IMarkedWavelength {

	private double wavelength;
	private int magnification;

	/**
	 * Adds the wavelength with magnification 1.
	 * 
	 * @param ion
	 */
	public MarkedWavelength(double wavelength) {
		this(wavelength, 1);
	}

	public MarkedWavelength(double wavelength, int magnification) {
		setWavelength(wavelength);
		setMagnification(magnification);
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
	public int getMagnification() {

		return magnification;
	}

	@Override
	public void setMagnification(int magnification) {

		this.magnification = magnification;
	}

	// -----------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		MarkedWavelength other = (MarkedWavelength)otherObject;
		return wavelength == other.getWavelength() && //
				magnification == other.getMagnification();
	}

	@Override
	public int hashCode() {

		return 7 * new Double(wavelength).hashCode() + //
				11 * new Integer(magnification).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("wavelength=" + wavelength);
		builder.append(",");
		builder.append("magnification=" + magnification);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
