/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;

/**
 * This is an immutable zero ion.
 * m/z = 0
 * intensity = 0
 * It is stateless, hence only one shared instance exists: {@link #INSTANCE}
 *
 */
public class ImmutableZeroIon extends AbstractIon {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 3064769810691237713L;

	public static final ImmutableZeroIon INSTANCE = new ImmutableZeroIon();

	private ImmutableZeroIon() {

		super(TIC_ION, ZERO_INTENSITY);
	}

	/**
	 * Preserve the singleton on deserialization.
	 */
	private Object readResolve() {

		return INSTANCE;
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