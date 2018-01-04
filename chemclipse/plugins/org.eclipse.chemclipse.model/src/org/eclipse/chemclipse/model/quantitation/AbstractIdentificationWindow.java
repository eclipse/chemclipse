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

public abstract class AbstractIdentificationWindow implements IIdentificationWindow {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -1544984148413303660L;
	//
	private float allowedNegativeDeviation; // Sets the negative deviation (>= 0)
	private float allowedPositiveDeviation; // Sets the positive deviation (>= 0)

	@Override
	public float getAllowedNegativeDeviation() {

		return allowedNegativeDeviation;
	}

	@Override
	public void setAllowedNegativeDeviation(float allowedNegativeDeviation) {

		if(allowedNegativeDeviation >= 0) {
			this.allowedNegativeDeviation = allowedNegativeDeviation;
		}
	}

	@Override
	public float getAllowedPositiveDeviation() {

		return allowedPositiveDeviation;
	}

	@Override
	public void setAllowedPositiveDeviation(float allowedPositiveDeviation) {

		if(allowedPositiveDeviation >= 0) {
			this.allowedPositiveDeviation = allowedPositiveDeviation;
		}
	}
}
