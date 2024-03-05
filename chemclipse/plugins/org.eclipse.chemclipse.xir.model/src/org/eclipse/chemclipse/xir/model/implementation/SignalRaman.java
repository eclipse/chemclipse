/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.implementation;

import org.eclipse.chemclipse.xir.model.core.ISignalRaman;

public class SignalRaman extends AbstractSignalVS implements ISignalRaman {

	private static final long serialVersionUID = -8694972698204376845L;
	//
	private double scattering = 0.0d;

	public SignalRaman(double wavenumber, double scattering) {

		super(wavenumber);
		this.scattering = scattering;
	}

	@Override
	public double getIntensity() {

		return getScattering();
	}

	@Override
	public void setIntensity(double intensity) {

		setScattering(intensity);
	}

	@Override
	public double getScattering() {

		return scattering;
	}

	@Override
	public void setScattering(double scattering) {

		this.scattering = scattering;
	}

	@Override
	public double getY() {

		return scattering;
	}
}
