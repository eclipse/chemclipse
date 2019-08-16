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

public interface IIdentificationWindow extends Serializable {

	/**
	 * Returns the negative deviation.
	 * 
	 * @return float
	 */
	float getAllowedNegativeDeviation();

	/**
	 * Sets the negative deviation.
	 * Allowed values >= 0
	 * 
	 * @param allowedNegativeDeviation
	 */
	void setAllowedNegativeDeviation(float allowedNegativeDeviation);

	/**
	 * Returns the positive deviation.
	 * 
	 * @return float
	 */
	float getAllowedPositiveDeviation();

	/**
	 * Sets the positive deviation.
	 * Allowed values >= 0
	 * 
	 * @param allowedPositiveDeviation
	 */
	void setAllowedPositiveDeviation(float allowedPositiveDeviation);
}
