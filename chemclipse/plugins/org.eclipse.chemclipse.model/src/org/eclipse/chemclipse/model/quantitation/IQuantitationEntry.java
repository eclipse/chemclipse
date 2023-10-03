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

import java.io.Serializable;
import java.util.List;

public interface IQuantitationEntry extends Serializable {

	/**
	 * Legacy method - Better use:
	 * List<Double> getSignals();
	 * 
	 * @return List<Double>
	 */
	double getSignal();

	/**
	 * Legacy method - Better use:
	 * setSignals(List<Double> signals)
	 * 
	 * @param signal
	 */
	void setSignal(double signal);

	List<Double> getSignals();

	void setSignals(List<Double> signals);

	String getName();

	String getGroup();

	String getChemicalClass();

	void setChemicalClass(String chemicalClass);

	double getConcentration();

	String getConcentrationUnit();

	double getArea();

	String getCalibrationMethod();

	void setCalibrationMethod(String calibrationMethod);

	boolean getUsedCrossZero();

	void setUsedCrossZero(boolean usedCrossZero);

	String getDescription();

	void setDescription(String description);

	void appendDescription(String description);

	QuantitationFlag getQuantitationFlag();

	void setQuantitationFlag(QuantitationFlag quantitationFlag);
}