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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;

public abstract class AbstractQuantitationCompound<T extends IPeak> implements IQuantitationCompound<T> {

	private static final long serialVersionUID = 1901297052527290065L;
	//
	private String name = ""; // Styrene
	private String chemicalClass = ""; // Styrene-Butadiene
	private IRetentionTimeWindow retentionTimeWindow = new RetentionTimeWindow();
	private IRetentionIndexWindow retentionIndexWindow = new RetentionIndexWindow();
	private String concentrationUnit = ""; // mg/kg
	private boolean useTIC = true;
	private CalibrationMethod calibrationMethod = CalibrationMethod.LINEAR;
	private boolean useCrossZero = true;
	//
	private IQuantitationSignals quantitationSignals = new QuantitationSignals();
	private IResponseSignals concentrationResponseEntries = new ResponseSignals();
	private List<IQuantitationPeak<T>> quantitationPeaks = new ArrayList<>();

	/**
	 * Name, e.g.: Styrene<br/>
	 * ConcentrationUnit, e.g.: mg/kg<br/>
	 * 
	 * @param name
	 * @param concentrationUnit
	 */
	public AbstractQuantitationCompound(String name, String concentrationUnit) {
		this(name, concentrationUnit, 0);
	}

	/**
	 * Name, e.g.: Styrene<br/>
	 * ConcentrationUnit, e.g.: mg/kg<br/>
	 * RetentionTime: in milliseconds
	 * 
	 * @param name
	 * @param concentrationUnit
	 * @param retentionTime
	 */
	public AbstractQuantitationCompound(String name, String concentrationUnit, int retentionTime) {
		this.name = name;
		this.concentrationUnit = concentrationUnit;
		retentionTimeWindow.setRetentionTime(retentionTime);
		quantitationSignals.add(new QuantitationSignal(IQuantitationSignal.TIC_SIGNAL, IQuantitationSignal.ABSOLUTE_RELATIVE_RESPONSE, 0.0, true));
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getChemicalClass() {

		return chemicalClass;
	}

	@Override
	public void setChemicalClass(String chemicalClass) {

		if(chemicalClass != null) {
			this.chemicalClass = chemicalClass;
		}
	}

	@Override
	public IRetentionTimeWindow getRetentionTimeWindow() {

		return retentionTimeWindow;
	}

	@Override
	public IRetentionIndexWindow getRetentionIndexWindow() {

		return retentionIndexWindow;
	}

	@Override
	public String getConcentrationUnit() {

		return concentrationUnit;
	}

	@Override
	public boolean isUseTIC() {

		return useTIC;
	}

	@Override
	public void setUseTIC(boolean useTIC) {

		this.useTIC = useTIC;
	}

	@Override
	public CalibrationMethod getCalibrationMethod() {

		return calibrationMethod;
	}

	@Override
	public void setCalibrationMethod(CalibrationMethod calibrationMethod) {

		this.calibrationMethod = calibrationMethod;
	}

	@Override
	public boolean isCrossZero() {

		return useCrossZero;
	}

	@Override
	public void setUseCrossZero(boolean useCrossZero) {

		this.useCrossZero = useCrossZero;
	}

	@Override
	public IQuantitationSignals getQuantitationSignals() {

		return quantitationSignals;
	}

	@Override
	public IResponseSignals getResponseSignals() {

		return concentrationResponseEntries;
	}

	@Override
	public List<IQuantitationPeak<T>> getQuantitationPeaks() {

		return quantitationPeaks;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		AbstractQuantitationCompound other = (AbstractQuantitationCompound)obj;
		if(name == null) {
			if(other.name != null)
				return false;
		} else if(!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "AbstractQuantitationCompound [name=" + name + ", chemicalClass=" + chemicalClass + ", concentrationUnit=" + concentrationUnit + ", useTIC=" + useTIC + ", calibrationMethod=" + calibrationMethod + ", useCrossZero=" + useCrossZero + "]";
	}
}
