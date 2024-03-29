/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.implementation;

import org.eclipse.chemclipse.wsd.model.core.ISignalWSD;

public class SignalWSD extends AbstractSignalWSD implements ISignalWSD, Comparable<ISignalWSD> {

	private static final long serialVersionUID = -6878875442146282898L;

	public SignalWSD(double wavelength, double absorbance, double transmittance) {

		super(wavelength, absorbance, transmittance);
	}
}
