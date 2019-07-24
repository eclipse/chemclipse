/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.math.BigDecimal;

import org.eclipse.chemclipse.model.core.IComplexSignal;

/**
 * The {@link SpectrumSignal} consists of an acquisition time and an intensity
 *
 * @author Christoph Läubrich
 *
 */
public interface SpectrumSignal extends IComplexSignal {

	/**
	 *
	 * @return the frequency in Hz
	 */
	BigDecimal getFrequency();

	/**
	 * The absorptive intensity (also known as the "real part" of the signal)
	 *
	 * @return the intensity
	 */
	Number getAbsorptiveIntensity();

	/**
	 * The dispersive intensity (also knows as the "imaginary part" of the signal)
	 *
	 * @return the intensity
	 */
	Number getDispersiveIntensity();

	@Override
	default double getX() {

		return getFrequency().doubleValue();
	}

	@Override
	default double getY() {

		return getAbsorptiveIntensity().doubleValue();
	}

	@Override
	default double getImaginaryY() {

		return getDispersiveIntensity().doubleValue();
	}
}
