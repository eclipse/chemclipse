/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.io.Serializable;

public final class SPECTRAL_REFERENCE implements ISpectralReference, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	//
	private double frequency;
	private String nucleus;

	public SPECTRAL_REFERENCE() {
	}

	public SPECTRAL_REFERENCE(String nucleus, double frequency) {
		this.nucleus = nucleus;
		this.frequency = frequency;
	}

	@Override
	public double getFrequency() {

		return frequency;
	}

	public void setFrequency(double frequency) {

		this.frequency = frequency;
	}

	@Override
	public boolean equals(Object obj) {

		if(this.getClass().equals(obj.getClass())) {
			SPECTRAL_REFERENCE spectral_reference = (SPECTRAL_REFERENCE)obj;
			return spectral_reference.frequency == frequency;
		}
		return false;
	}

	@Override
	public String getNucleus() {

		return nucleus;
	}

	public void setNucleus(String nucleusOfFrequencyChannelF1) {

		this.nucleus = nucleusOfFrequencyChannelF1;
	}
}
