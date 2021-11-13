/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add inclusive/exclusive mode
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import java.util.Collection;

import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelength.WavelengthMarkMode;

public class MarkedWavelengths extends AbstractMarkedWavelengths implements IMarkedWavelengths {

	private WavelengthMarkMode mode;

	public MarkedWavelengths() {

	}

	public MarkedWavelengths(WavelengthMarkMode mode) {

		this.mode = mode;
	}

	public MarkedWavelengths(Collection<? extends Number> wavelengths, WavelengthMarkMode mode) {

		super(wavelengths);
		this.mode = mode;
	}

	@Override
	public WavelengthMarkMode getMode() {

		return mode;
	}
}
