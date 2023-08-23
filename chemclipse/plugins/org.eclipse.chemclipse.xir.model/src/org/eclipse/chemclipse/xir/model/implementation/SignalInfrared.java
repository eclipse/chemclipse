/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder differentiate transmission vs absorbance
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.implementation;

import org.eclipse.chemclipse.xir.model.core.ISignalInfrared;

public class SignalInfrared extends AbstractSignalXIR implements ISignalInfrared {

	private static final long serialVersionUID = -8802517559910089354L;
	//
	private double absorbance = 0.0d;
	private double transmission = 0.0d;

	public SignalInfrared(double wavenumber, double absorbance) {

		this(wavenumber, absorbance, 0.0d);
	}

	public SignalInfrared(double wavenumber, double absorbance, double transmission) {

		super(wavenumber);
		this.absorbance = absorbance;
		this.transmission = transmission;
	}

	@Override
	public double getIntensity() {

		return getAbsorbance();
	}

	@Override
	public void setIntensity(double intensity) {

		setAbsorbance(intensity);
	}

	@Override
	public double getTransmission() {

		if(transmission > 0) {
			return transmission;
		}
		//
		if(absorbance > 0) {
			return 100 / Math.pow(10, absorbance);
		}
		//
		return 100;
	}

	@Override
	public void setTransmission(double transmission) {

		this.transmission = transmission;
	}

	@Override
	public double getAbsorbance() {

		if(absorbance > 0) {
			return absorbance;
		} else if(transmission > 0) {
			return Math.log(1 / transmission);
		}
		//
		return 0;
	}

	@Override
	public void setAbsorbance(double absorbance) {

		this.absorbance = absorbance;
	}

	@Override
	public double getY() {

		return transmission > 0 ? transmission : absorbance;
	}
}