/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.ISignal;

public abstract class AbstractQuantitationEntry implements IQuantitationEntry {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -6312658397485712262L;
	//
	private static final String DESCRIPTION_DELIMITER = " | ";
	//
	private List<Double> signals = new ArrayList<>();
	private String name = "";
	private String chemicalClass = "";
	private double concentration = 0.0d;
	private String concentrationUnit = "";
	private double area = 0.0d;
	private String calibrationMethod = "";
	private boolean usedCrossZero = true;
	private String description = "";
	private QuantitationFlag quantitationFlag = QuantitationFlag.NONE;

	public AbstractQuantitationEntry(String name, double concentration, String concentrationUnit, double area) {

		this.name = name;
		this.concentration = concentration;
		this.concentrationUnit = concentrationUnit;
		this.area = area;
	}

	@Override
	public double getSignal() {

		/*
		 * This method has been added for backward compatibility reasons.
		 */
		return signals.isEmpty() ? ISignal.TOTAL_INTENSITY : signals.get(0);
	}

	@Override
	public void setSignal(double signal) {

		/*
		 * Better use:
		 * void setSignals(List<Double> signals)
		 */
		this.signals.clear();
		this.signals.add(signal);
	}

	@Override
	public List<Double> getSignals() {

		return signals;
	}

	@Override
	public void setSignals(List<Double> signals) {

		this.signals.clear();
		this.signals.addAll(signals);
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

	@Override
	public void appendDescription(String description) {

		if(description != null) {
			if(!this.description.contains(description)) {
				if(this.description.length() == 0) {
					this.description = description;
				} else {
					this.description += DESCRIPTION_DELIMITER + description;
				}
			}
		}
	}

	@Override
	public QuantitationFlag getQuantitationFlag() {

		return quantitationFlag;
	}

	@Override
	public void setQuantitationFlag(QuantitationFlag quantitationFlag) {

		this.quantitationFlag = quantitationFlag;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(area);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		result = prime * result + ((calibrationMethod == null) ? 0 : calibrationMethod.hashCode());
		result = prime * result + ((chemicalClass == null) ? 0 : chemicalClass.hashCode());
		temp = Double.doubleToLongBits(concentration);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		result = prime * result + ((concentrationUnit == null) ? 0 : concentrationUnit.hashCode());
		result = prime * result + (usedCrossZero ? 1231 : 1237);
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
		AbstractQuantitationEntry other = (AbstractQuantitationEntry)obj;
		if(Double.doubleToLongBits(area) != Double.doubleToLongBits(other.area))
			return false;
		if(calibrationMethod == null) {
			if(other.calibrationMethod != null)
				return false;
		} else if(!calibrationMethod.equals(other.calibrationMethod))
			return false;
		if(chemicalClass == null) {
			if(other.chemicalClass != null)
				return false;
		} else if(!chemicalClass.equals(other.chemicalClass))
			return false;
		if(Double.doubleToLongBits(concentration) != Double.doubleToLongBits(other.concentration))
			return false;
		if(concentrationUnit == null) {
			if(other.concentrationUnit != null)
				return false;
		} else if(!concentrationUnit.equals(other.concentrationUnit))
			return false;
		if(usedCrossZero != other.usedCrossZero)
			return false;
		return true;
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
}