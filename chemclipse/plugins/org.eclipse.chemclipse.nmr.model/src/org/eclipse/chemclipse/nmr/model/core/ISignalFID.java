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

import org.eclipse.chemclipse.model.core.ISignal;

/**
 * An NMR FID signal.
 * </p>
 * A singal's magnitude is the 'raw intensity'.
 *
 * @author Jan Holy
 *
 */
public interface ISignalFID extends ISignal {

	/**
	 * Returns the intensity of the signal.
	 *
	 * @see #getMagnitude()
	 * @return the intensity of the signal
	 */
	double getIntensity();

	/**
	 *
	 * @param intensity
	 */
	void setIntensity(double intensity);

	/**
	 *
	 */
	void resetIntensity();

	/**
	 *
	 * @param magnitude
	 */
	void setMagnitude(double magnitude);

	/**
	 * Returns the magnitude of the signal.
	 *
	 * @see #getIntensity()
	 * @return the magnitude of the signal
	 */
	double getMagnitude();

	/**
	 *
	 * @return time in milliseconds
	 */
	int getTime();

	/**
	 * set time in milliseconds
	 *
	 * @param millisecond
	 */
	void setTime(int milliseconds);
}
