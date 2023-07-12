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

public interface IInternalStandard {

	double STANDARD_COMPENSATION_FACTOR = 1.0d;

	String getName();

	void setName(String name);

	double getConcentration();

	String getConcentrationUnit();

	double getCompensationFactor();

	/**
	 * The response factor is the reciprocal of the compensation factor.
	 * 
	 * @return double
	 */
	double getResponseFactor();

	String getChemicalClass();

	void setChemicalClass(String chemicalClass);
}