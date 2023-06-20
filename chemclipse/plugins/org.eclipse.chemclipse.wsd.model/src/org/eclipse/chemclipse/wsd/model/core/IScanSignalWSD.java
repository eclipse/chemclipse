/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

import java.io.Serializable;

public interface IScanSignalWSD extends Serializable {

	float TOTAL_INTENSITY = 0.0f;

	/**
	 * Returns the wavelength in nanometer (nm) scale.
	 * 
	 * @return float
	 */
	float getWavelength();

	/**
	 * Sets the wavelength in nanometer (nm) scale.
	 * 
	 */
	void setWavelength(float wavelength);

	/**
	 * Returns the actual abundance of the wavelength.
	 * 
	 * @return float
	 */
	float getAbundance();

	/**
	 * Sets an abundance value for the wavelength.
	 * 
	 * @param abundance
	 */
	void setAbundance(float abundance);
}
