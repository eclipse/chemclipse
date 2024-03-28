/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;

/**
 * This is an immutable zero ion.
 * m/z = 0
 * intensity = 0
 *
 */
public class ImmutableZeroIon extends AbstractIon implements IIon {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 3064769810691237713L;

	public ImmutableZeroIon() {

		super(TIC_ION, ZERO_INTENSITY);
	}

	@Override
	public boolean setAbundance(float abundance) {

		return false;
	}

	@Override
	public boolean setIon(double ion) {

		return false;
	}
}
