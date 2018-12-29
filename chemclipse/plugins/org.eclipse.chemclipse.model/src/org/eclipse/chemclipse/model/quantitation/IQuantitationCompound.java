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

import java.io.Serializable;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;

public interface IQuantitationCompound<T extends IPeak> extends Serializable {

	/**
	 * Levoglucosan
	 * 
	 * @return String
	 */
	String getName();

	void setName(String name);

	/**
	 * Cellulose
	 * 
	 * @return String
	 */
	String getChemicalClass();

	void setChemicalClass(String chemicalClass);

	/**
	 * Allowed +/- retention time.
	 * 
	 * @return {@link IRetentionTimeWindow}
	 */
	IRetentionTimeWindow getRetentionTimeWindow();

	/**
	 * Allowed +/- retention index.
	 * 
	 * @return {@link IRetentionIndexWindow}
	 */
	IRetentionIndexWindow getRetentionIndexWindow();

	/**
	 * E.g. mg/ml
	 * 
	 * @return String
	 */
	String getConcentrationUnit();

	/**
	 * Use TIC or XIC
	 * 
	 * @return boolean
	 */
	boolean isUseTIC();

	/**
	 * Set use TIC or XIC.
	 * 
	 * @param boolean
	 */
	void setUseTIC(boolean useTIC);

	/**
	 * This method will override the quantitation signal and
	 * concentration response entry list if quantitation peaks
	 * are stored.
	 * The list of quantitation signals and concentration entries
	 * will be calculated by using the stored quantitation peaks.
	 */
	void calculateQuantitationSignalsAndConcentrationResponseEntries();

	/**
	 * Returns the quantitation signals.
	 * 
	 * @return IQuantitationSignals
	 */
	IQuantitationSignals getQuantitationSignals();

	/**
	 * Updates the existing signals by the given list.
	 * 
	 * @param quantitationSignals
	 */
	void updateQuantitationSignals(IQuantitationSignals quantitationSignals);

	/**
	 * Returns the concentration / response entries.
	 * 
	 * @return {@link IConcentrationResponseEntries}
	 */
	IConcentrationResponseEntries getConcentrationResponseEntries();

	/**
	 * Updates the concentration response entries.
	 * 
	 * @param concentrationResponseEntries
	 */
	void updateConcentrationResponseEntries(IConcentrationResponseEntries concentrationResponseEntries);

	/**
	 * Linear, ...
	 * 
	 * @return {@link CalibrationMethod}
	 */
	CalibrationMethod getCalibrationMethod();

	/**
	 * Sets the calibration method.
	 * 
	 * @param calibrationMethod
	 */
	void setCalibrationMethod(CalibrationMethod calibrationMethod);

	/**
	 * Cross zero when calculate the equation.
	 * 
	 * @return boolean
	 */
	boolean isCrossZero();

	/**
	 * Set use zero crossing.
	 * 
	 * @param boolean
	 */
	void setUseCrossZero(boolean useCrossZero);

	void updateQuantitationCompound(IQuantitationCompound<T> quantitationCompound);

	List<IQuantitationPeak<T>> getQuantitationPeaks();

	void createSignalTablesTIC(List<IQuantitationPeak<T>> quantitationPeaks); // TODO

	void createSignalTablesXIC(List<IQuantitationPeak<T>> quantitationPeaks); // TODO
}
