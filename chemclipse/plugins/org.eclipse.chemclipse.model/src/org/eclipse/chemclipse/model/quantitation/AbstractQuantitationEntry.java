/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.chemclipse.model.core.ISignal;

public abstract class AbstractQuantitationEntry implements IQuantitationEntry {

	private static final long serialVersionUID = -9169349509443899583L;
	private static final String DESCRIPTION_DELIMITER = " | ";
	//
	private List<Double> signals = new ArrayList<>();
	private String name = "";
	private String group = ""; // Used e.g. for repetitions
	private String chemicalClass = "";
	private double concentration = 0.0d;
	private String concentrationUnit = "";
	private double area = 0.0d;
	private String calibrationMethod = "";
	private boolean usedCrossZero = true;
	private String description = "";
	private QuantitationFlag quantitationFlag = QuantitationFlag.NONE;

	public AbstractQuantitationEntry(String name, String group, double concentration, String concentrationUnit, double area) {

		this.name = name;
		this.group = group;
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
	public String getGroup() {

		return group;
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

		return Objects.hash(area, calibrationMethod, chemicalClass, concentration, concentrationUnit, group, name, signals, usedCrossZero);
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
		return Double.doubleToLongBits(area) == Double.doubleToLongBits(other.area) && Objects.equals(calibrationMethod, other.calibrationMethod) && Objects.equals(chemicalClass, other.chemicalClass) && Double.doubleToLongBits(concentration) == Double.doubleToLongBits(other.concentration) && Objects.equals(concentrationUnit, other.concentrationUnit) && Objects.equals(group, other.group) && Objects.equals(name, other.name) && Objects.equals(signals, other.signals) && usedCrossZero == other.usedCrossZero;
	}

	@Override
	public String toString() {

		return "AbstractQuantitationEntry [signals=" + signals + ", name=" + name + ", group=" + group + ", chemicalClass=" + chemicalClass + ", concentration=" + concentration + ", concentrationUnit=" + concentrationUnit + ", area=" + area + ", calibrationMethod=" + calibrationMethod + ", usedCrossZero=" + usedCrossZero + ", description=" + description + ", quantitationFlag=" + quantitationFlag + "]";
	}
}