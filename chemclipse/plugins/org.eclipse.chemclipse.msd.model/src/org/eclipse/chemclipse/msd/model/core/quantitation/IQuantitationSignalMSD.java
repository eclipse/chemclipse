/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

public interface IQuantitationSignalMSD {

	float ABSOLUTE_RESPONSE = 100.0f; // 100 is used for the TIC signal.

	/**
	 * E.g. 104 for styrene
	 * 
	 * @return double
	 */
	double getIon();

	/**
	 * E.g. 100
	 * 
	 * @return float
	 */
	float getRelativeResponse();

	/**
	 * E.g. 0.02 for a uncertainty of 0.02 %.
	 * 
	 * @return double
	 */
	double getUncertainty();

	void setUncertainty(double uncertainty);

	/**
	 * Use for quantitation.
	 * 
	 * @return
	 */
	boolean isUse();

	void setUse(boolean use);
}
