/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
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

public interface IQuantitationEntry extends Serializable {

	double getSignal();

	void setSignal(double signal);

	String getName();

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
}
