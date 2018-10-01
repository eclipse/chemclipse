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
package org.eclipse.chemclipse.wsd.model.core;

public interface IScanSignalWSD {

	/**
	 * @deprecated because in WSD chromatography total signal does not exist
	 */
	@Deprecated
	double TIC_SIGNAL = 0;

	/**
	 * Returns the wavelength in nanometer (nm) scale.
	 * 
	 * @return double
	 */
	double getWavelength();

	/**
	 * Sets the wavelength in nanometer (nm) scale.
	 * 
	 */
	void setWavelength(double wavelength);

	/**
	 * Returns the actual abundance of the wavelength.
	 * 
	 * @return float - abundance
	 */
	float getAbundance();

	/**
	 * Sets an abundance value for the wavelength.
	 * 
	 * @param abundance
	 */
	void setAbundance(float abundance);
}
