/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

public class MarkedIon implements IMarkedIon {

	private double ion;
	private int magnification;

	/**
	 * Adds the ions with magnification 1.
	 * 
	 * @param ion
	 */
	public MarkedIon(double ion) {
		this(ion, 1);
	}

	public MarkedIon(double ion, int magnification) {
		setIon(ion);
		setMagnification(magnification);
	}

	@Override
	public double getIon() {

		return ion;
	}

	@Override
	public void setIon(double ion) {

		this.ion = ion;
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
		MarkedIon other = (MarkedIon)otherObject;
		return ion == other.getIon() && magnification == other.getMagnification();
	}

	@Override
	public int hashCode() {

		return 7 * new Double(ion).hashCode() + 11 * new Integer(magnification).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("ion=" + ion);
		builder.append(",");
		builder.append("magnification=" + magnification);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
