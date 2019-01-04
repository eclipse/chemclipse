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

public interface IQuantitationCompound extends Serializable, Comparable<IQuantitationCompound> {

	String getName();

	void setName(String name);

	String getChemicalClass();

	void setChemicalClass(String chemicalClass);

	String getConcentrationUnit();

	void setConcentrationUnit(String concentrationUnit);

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

	boolean isUseTIC();

	void setUseTIC(boolean useTIC);

	/**
	 * Returns the quantitation signals.
	 * 
	 * @return IQuantitationSignals
	 */
	IQuantitationSignals getQuantitationSignals();

	/**
	 * Returns the concentration / response entries.
	 * 
	 * @return {@link IResponseSignals}
	 */
	IResponseSignals getResponseSignals();

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

	List<IQuantitationPeak> getQuantitationPeaks();

	/**
	 * IQuantitationSignals and IConcentrationResponseEntries
	 * are automatically calculated using the stored QuantitationPeaks.
	 * TIC or XIC values are calculated depending on the compound setting (isUseTIC).
	 * 
	 */
	void calculateSignalTablesFromPeaks();
}
