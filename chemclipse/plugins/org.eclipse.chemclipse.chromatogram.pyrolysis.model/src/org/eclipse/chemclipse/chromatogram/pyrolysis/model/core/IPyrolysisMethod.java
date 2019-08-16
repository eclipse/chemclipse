/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.pyrolysis.model.core;

public interface IPyrolysisMethod {

	/**
	 * Returns the pyrolysis temperature.
	 * 
	 * @return
	 */
	int getPyrolysisTemperature();

	/**
	 * Sets the pyrolysis temperature.
	 * 
	 * @param pyrolysisTemperature
	 */
	void setPyrolysisTemperature(int pyrolysisTemperature);

	/**
	 * Returns the interface temperature.
	 * 
	 * @return int
	 */
	int getInterfaceTemperature();

	/**
	 * Sets the interface temperature.
	 * 
	 * @param interfaceTemperature
	 */
	void setInterfaceTemperature(int interfaceTemperature);
	// TODO ausbauen
}
