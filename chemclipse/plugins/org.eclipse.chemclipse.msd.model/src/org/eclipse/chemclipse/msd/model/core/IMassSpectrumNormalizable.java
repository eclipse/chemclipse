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
package org.eclipse.chemclipse.msd.model.core;

/**
 * This interface declares the functionality to normalize a mass spectrum.
 * 
 * @author eselmeister
 */
public interface IMassSpectrumNormalizable {

	/**
	 * This method normalizes the mass spectrum to the base of 100.<br/>
	 * It means that the highest abundance gets the value of 100 (100%).<br/>
	 * Subsequent all lower abundances are related to 100.
	 */
	IScanMSD normalize();

	/**
	 * This method normalizes the mass spectrum to the given base.<br/>
	 * It means that the highest abundance gets the value of the given base
	 * (100%).<br/>
	 * Subsequent all lower abundances are related to the given base.<br/>
	 * Only values > 0 are permitted.
	 */
	IScanMSD normalize(float base);

	/**
	 * Returns whether the mass spectrum is normalized or not.
	 * 
	 * @return boolean
	 */
	boolean isNormalized();

	/**
	 * Returns the used normalization base or null if no normalization has been
	 * applied.
	 * 
	 * @return float
	 */
	float getNormalizationBase();
}
