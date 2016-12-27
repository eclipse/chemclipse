/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

public class AbstractConcentrationResponseEntry implements IConcentrationResponseEntryMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 4734986421851701665L;
	//
	private double ion;
	private double concentration;
	private double response;

	public AbstractConcentrationResponseEntry(double ion, double concentration, double response) {
		this.ion = ion;
		this.concentration = concentration;
		this.response = response;
	}

	@Override
	public double getIon() {

		return ion;
	}

	@Override
	public double getConcentration() {

		return concentration;
	}

	@Override
	public double getResponse() {

		return response;
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
		/*
		 * The response shall be not part of the equals method.
		 */
		AbstractConcentrationResponseEntry other = (AbstractConcentrationResponseEntry)otherObject;
		return getIon() == other.getIon() && getConcentration() == other.getConcentration();
	}

	@Override
	public int hashCode() {

		return 7 * Double.valueOf(ion).hashCode() + 11 * Double.valueOf(concentration).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("ion=" + ion);
		builder.append(",");
		builder.append("concentration=" + concentration);
		builder.append(",");
		builder.append("response=" + response);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
