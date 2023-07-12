/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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
	private double compensationFactor;
	private String chemicalClass = "";

	public InternalStandard(String name, double concentration, String concentrationUnit) {

		this(name, concentration, concentrationUnit, STANDARD_COMPENSATION_FACTOR);
	}

	public InternalStandard(String name, double concentration, String concentrationUnit, double compensationFactor) {

		this.name = name;
		this.concentration = concentration;
		this.concentrationUnit = concentrationUnit;
		this.compensationFactor = compensationFactor;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		if(name == null) {
			name = "";
		} else {
			this.name = name;
		}
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
	public double getCompensationFactor() {

		return compensationFactor;
	}

	@Override
	public double getResponseFactor() {

		if(compensationFactor > 0.0d) {
			return 1.0d / compensationFactor;
		}
		//
		return 0.0d;
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
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(concentration);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		result = prime * result + ((concentrationUnit == null) ? 0 : concentrationUnit.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		InternalStandard other = (InternalStandard)obj;
		if(Double.doubleToLongBits(concentration) != Double.doubleToLongBits(other.concentration))
			return false;
		if(concentrationUnit == null) {
			if(other.concentrationUnit != null)
				return false;
		} else if(!concentrationUnit.equals(other.concentrationUnit))
			return false;
		if(name == null) {
			if(other.name != null)
				return false;
		} else if(!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "InternalStandard [name=" + name + ", concentration=" + concentration + ", concentrationUnit=" + concentrationUnit + ", compensationFactor=" + compensationFactor + ", chemicalClass=" + chemicalClass + "]";
	}
}
