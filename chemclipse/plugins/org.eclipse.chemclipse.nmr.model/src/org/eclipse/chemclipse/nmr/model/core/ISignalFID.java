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

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.model.core.ISignal;

/**
 * An NMR FID signal.
 * </p>
 * A singalFID is the 'raw intensity'.
 *
 * @author Jan Holy
 *
 */
public interface ISignalFID extends ISignal {

	/**
	 * Returns the intensity of the signal.
	 *
	 * @see #getIntensityFID()
	 * @return the intensity of the signal
	 */
	Complex getIntensity();

	/**
	 *
	 * @param intensity
	 */
	void setIntensity(Complex intensity);

	/**
	 *
	 */
	void resetIntensity();

	/**
	 *
	 * @param magnitude
	 */
	void setIntensityFID(Complex magnitude);

	/**
	 * Returns the intensity of the signal. (Signal is not process)
	 *
	 * @see #getIntensity()
	 * @return the intensity of the signal
	 */
	Complex getIntensityFID();

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
