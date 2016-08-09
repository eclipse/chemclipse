/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

public class InternalStandard implements IInternalStandard {

	private String name = "";
	private double concentration;
	private String concentrationUnit;
	private double responseFactor;
	private String chemicalClass = "";

	public InternalStandard(String name, double concentration, String concentrationUnit, double responseFactor) {
		this.name = name;
		this.concentration = concentration;
		this.concentrationUnit = concentrationUnit;
		this.responseFactor = responseFactor;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public double getConcentration() {

		return concentration;
	}

	@Override
	public String getConcentrationUnit() {

		return concentrationUnit;
	}

	@Override
	public double getResponseFactor() {

		return responseFactor;
	}

	@Override
	public String getChemicalClass() {

		return chemicalClass;
	}

	@Override
	public void setChemicalClass(String chemicalClass) {

		this.chemicalClass = chemicalClass;
	}

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
		IInternalStandard other = (IInternalStandard)otherObject;
		return concentration == other.getConcentration() && //
				concentrationUnit == other.getConcentrationUnit() && //
				responseFactor == other.getResponseFactor();
	}

	@Override
	public int hashCode() {

		return Double.valueOf(concentration).hashCode() + //
				concentrationUnit.hashCode() + //
				Double.valueOf(responseFactor).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("concentration=" + concentration);
		builder.append(",");
		builder.append("concentrationUnit=" + concentrationUnit);
		builder.append(",");
		builder.append("responseFactor=" + responseFactor);
		builder.append("]");
		return builder.toString();
	}
}
