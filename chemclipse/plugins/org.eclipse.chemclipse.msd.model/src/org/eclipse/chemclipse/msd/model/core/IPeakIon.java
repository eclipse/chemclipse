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

public interface IPeakIon extends IIon {

	/**
	 * Returns the uncertainty factor of the ion.<br/>
	 * A factor of 0.0f mean 0%, a factor of 1.0f means 100%.<br/>
	 * 100% means, that this ions belongs surely to the corresponding
	 * mass spectrum.
	 * 
	 * @return float
	 */
	float getUncertaintyFactor();

	/**
	 * Sets the uncertainty factor of the ion.<br/>
	 * A factor of 0.0f mean 0%, a factor of 1.0f means 100%.<br/>
	 * 100% means, that this ions belongs surely to the corresponding
	 * mass spectrum.
	 * 
	 * @param uncertaintyFactor
	 */
	void setUncertaintyFactor(float uncertaintyFactor);

	/**
	 * Returns the peak ion type.
	 * 
	 * @return PeakIonType
	 */
	PeakIonType getPeakIonType();

	/**
	 * Sets the peak ion type.
	 * 
	 * @param peakIonType
	 */
	void setPeakIonType(PeakIonType peakIonType);
}
