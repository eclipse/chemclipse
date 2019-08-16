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
 * The {@link FIDSignal} consists of an acquisition time and an intensity
 * 
 * @author Christoph Läubrich
 *
 */
public interface FIDSignal extends IComplexSignal {

	/**
	 * 
	 * @return the (relative) time this signal was recorded in seconds
	 */
	BigDecimal getSignalTime();

	/**
	 * The real component of the intensity also known as the "real" part of the data
	 * 
	 * @return the intensity
	 */
	Number getRealComponent();

	/**
	 * the imaginary component of the intensity also known as the "imaginary" part of the data
	 * 
	 * @return the intensity
	 */
	Number getImaginaryComponent();

	@Override
	default double getX() {

		return getSignalTime().doubleValue();
	}

	@Override
	default double getY() {

		return getRealComponent().doubleValue();
	}

	@Override
	default double getImaginaryY() {

		return getImaginaryComponent().doubleValue();
	}
}
