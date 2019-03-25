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

import org.eclipse.chemclipse.model.core.ISignal;

/**
 * The {@link FIDSignal} consists of an acquisition time and an intensity
 * 
 * @author Christoph Läubrich
 *
 */
public interface FIDSignal extends ISignal {

	/**
	 * 
	 * @return the acquisition time in seconds
	 */
	BigDecimal getAcquisitionTime();

	/**
	 * 
	 * @return the intensity
	 */
	BigDecimal getIntensity();

	@Override
	default double getX() {

		return getAcquisitionTime().doubleValue();
	}

	@Override
	default double getY() {

		return getIntensity().doubleValue();
	}
}
