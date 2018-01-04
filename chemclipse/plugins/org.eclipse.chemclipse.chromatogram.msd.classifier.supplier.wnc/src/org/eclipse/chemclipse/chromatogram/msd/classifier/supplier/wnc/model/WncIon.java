/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.preferences.PreferenceSupplier;

public class WncIon implements IWncIon {

	private int ion;
	private String name;
	private double percentageMaxIntensity;
	private double percentageSumIntensity;

	public WncIon(int ion, String name) {
		this.ion = ion;
		/*
		 * Some characters are not allowed.
		 * They are used to persist the entries.
		 */
		name = name.trim();
		name = name.replace(PreferenceSupplier.VALUE_DELIMITER, "");
		name = name.replace(PreferenceSupplier.ENTRY_DELIMITER, "");
		this.name = name;
	}

	@Override
	public int getIon() {

		return ion;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public double getPercentageMaxIntensity() {

		return percentageMaxIntensity;
	}

	@Override
	public void setPercentageMaxIntensity(double percentageMaxIntensity) {

		this.percentageMaxIntensity = percentageMaxIntensity;
	}

	@Override
	public double getPercentageSumIntensity() {

		return percentageSumIntensity;
	}

	@Override
	public void setPercentageSumIntensity(double percentageSumIntensity) {

		this.percentageSumIntensity = percentageSumIntensity;
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
		WncIon other = (WncIon)otherObject;
		return ion == other.getIon() && name.equals(other.getName());
	}

	@Override
	public int hashCode() {

		return 7 * new Integer(ion).hashCode() + 11 * name.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("ion=" + ion);
		builder.append(",");
		builder.append("name=" + name);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
