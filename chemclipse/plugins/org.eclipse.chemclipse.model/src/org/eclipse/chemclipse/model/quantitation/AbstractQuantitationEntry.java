/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

public abstract class AbstractQuantitationEntry implements IQuantitationEntry {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -6312658397485712262L;
	//
	private String name;
	private String chemicalClass = "";
	private double concentration;
	private String concentrationUnit;
	private double area;
	private String calibrationMethod = "";
	private boolean usedCrossZero = true;
	private String description = "";

	public AbstractQuantitationEntry(String name, double concentration, String concentrationUnit, double area) {
		this.name = name;
		this.concentration = concentration;
		this.concentrationUnit = concentrationUnit;
		this.area = area;
	}

	@Override
	public String getName() {

		return name;
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
	public double getConcentration() {

		return concentration;
	}

	@Override
	public String getConcentrationUnit() {

		return concentrationUnit;
	}

	@Override
	public double getArea() {

		return area;
	}

	@Override
	public String getCalibrationMethod() {

		return calibrationMethod;
	}

	@Override
	public void setCalibrationMethod(String calibrationMethod) {

		if(calibrationMethod != null) {
			this.calibrationMethod = calibrationMethod;
		}
	}

	@Override
	public boolean getUsedCrossZero() {

		return usedCrossZero;
	}

	@Override
	public void setUsedCrossZero(boolean usedCrossZero) {

		this.usedCrossZero = usedCrossZero;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
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
		 * Object comparison
		 */
		IQuantitationEntry other = (IQuantitationEntry)otherObject;
		return getName().equals(other.getName()) && getChemicalClass().equals(other.getChemicalClass()) && getConcentration() == other.getConcentration() && getConcentrationUnit().equals(other.getConcentrationUnit()) && getArea() == other.getArea() && getCalibrationMethod().equals(other.getCalibrationMethod()) && getUsedCrossZero() == other.getUsedCrossZero() && getDescription().equals(other.getDescription());
	}

	@Override
	public int hashCode() {

		return 7 * name.hashCode() + 11 * chemicalClass.hashCode() + 13 * Double.valueOf(concentration).hashCode() + 11 * concentrationUnit.hashCode() + Double.valueOf(area).hashCode() + calibrationMethod.hashCode() + Boolean.valueOf(usedCrossZero).hashCode() + description.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("name=" + name);
		builder.append(",");
		builder.append("chemicalClass=" + chemicalClass);
		builder.append(",");
		builder.append("concentration=" + concentration);
		builder.append(",");
		builder.append("concentrationUnit=" + concentrationUnit);
		builder.append(",");
		builder.append("area=" + area);
		builder.append(",");
		builder.append("calibrationMethod=" + calibrationMethod);
		builder.append(",");
		builder.append("usedCrossZero=" + usedCrossZero);
		builder.append(",");
		builder.append("description=" + description);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
