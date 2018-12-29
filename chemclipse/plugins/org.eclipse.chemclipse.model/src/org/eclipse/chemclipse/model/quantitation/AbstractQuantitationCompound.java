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
	private String chemicalClass = ""; // SB (Styrene-Butadiene)
	private IRetentionTimeWindow retentionTimeWindow = new RetentionTimeWindow();
	private IRetentionIndexWindow retentionIndexWindow = new RetentionIndexWindow();
	private String concentrationUnit = "";
	private IQuantitationSignals quantitationSignals = new QuantitationSignals();
	private IConcentrationResponseEntries concentrationResponseEntries = new ConcentrationResponseEntries();
	private boolean useTIC = true;
	private CalibrationMethod calibrationMethod = CalibrationMethod.LINEAR;
	private boolean useCrossZero = true;
	private List<IQuantitationPeak<T>> quantitationPeaks = new ArrayList<>();

	/**
	 * Name, e.g. Styrene<br/>
	 * <br/>
	 * Concentration unit, e.g.:<br/>
	 * "ng/ml"<br/>
	 * "µg/ml"<br/>
	 * "ppm"<br/>
	 * "mg/ml"<br/>
	 * <br/>
	 * Retention Time in milliseconds
	 * 
	 * @param name
	 * @param concentrationUnit
	 * @param retentionTime
	 */
	public AbstractQuantitationCompound(String name, String concentrationUnit, int retentionTime) {
		this.name = name;
		this.concentrationUnit = concentrationUnit;
		retentionTimeWindow.setRetentionTime(retentionTime);
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
	public IQuantitationSignals getQuantitationSignals() {

		return quantitationSignals;
	}

	@Override
	public void updateQuantitationSignals(IQuantitationSignals quantitationSignalsMSD) {

		if(quantitationSignalsMSD != null) {
			this.quantitationSignals = quantitationSignalsMSD;
		}
	}

	@Override
	public IConcentrationResponseEntries getConcentrationResponseEntries() {

		return concentrationResponseEntries;
	}

	@Override
	public void updateConcentrationResponseEntries(IConcentrationResponseEntries concentrationResponseEntries) {

		if(concentrationResponseEntries != null) {
			this.concentrationResponseEntries.clear();
			this.concentrationResponseEntries.addAll(concentrationResponseEntries);
		}
	}

	@Override
	public void calculateQuantitationSignalsAndConcentrationResponseEntries() {

		/*
		 * Create a new table only, if there is at least 1 peak stored.
		 */
		if(quantitationPeaks.size() > 0) {
			/*
			 * Delete the current lists.
			 */
			quantitationSignals.clear();
			concentrationResponseEntries.clear();
			/*
			 * Extract the values for the lists.
			 */
			if(isUseTIC()) {
				createSignalTablesTIC(quantitationPeaks);
			} else {
				createSignalTablesXIC(quantitationPeaks);
			}
		}
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
	public void updateQuantitationCompound(IQuantitationCompound<T> quantitationCompoundMSD) {

		if(quantitationCompoundMSD != null) {
			this.name = quantitationCompoundMSD.getName();
			this.chemicalClass = quantitationCompoundMSD.getChemicalClass();
			this.concentrationUnit = quantitationCompoundMSD.getConcentrationUnit();
			this.useTIC = quantitationCompoundMSD.isUseTIC();
			this.calibrationMethod = quantitationCompoundMSD.getCalibrationMethod();
			this.useCrossZero = quantitationCompoundMSD.isCrossZero();
		}
	}

	@Override
	public List<IQuantitationPeak<T>> getQuantitationPeaks() {

		return quantitationPeaks;
	}
}
