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

import java.io.Serializable;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.model.core.ISignal;

/**
 * A NMR FID signal.
 * </p>
 * The signalFID contains the digitized time-domain signal of the acquired data.
 *
 * @author Jan Holy, Alexander Stark
 *
 */
public interface ISignalFID extends Serializable, ISignal, Comparable<ISignalFID> {

	/**
	 * Returns the intensity of the processed FID,
	 * e.g. after apodization.
	 *
	 * @see #getIntensityUnprocessedFID()
	 * @return the intensity of the processed FID
	 */
	Complex getIntensityProcessedFID();

	/**
	 * Sets the intensity of the processed FID.
	 *
	 * @param processedIntensity
	 */
	void setIntensityProcessedFID(Complex processedIntensity);

	/**
	 * Returns the intensity of the processed FID,
	 * e.g. after digital filter
	 *
	 * @see #getIntensityUnprocessedFID()
	 * @return the intensity of the processed FID
	 */
	Complex getIntensityPreprocessedFID();

	/**
	 * Sets the intensity of the processed FID.
	 *
	 * @param preprocessedFID
	 */
	void setIntensityPreprocessedFID(Complex preprocessedFID);

	/**
	 * Resets the intensity of the processed FID.
	 *
	 * @return the reseted intensity of the processed FID
	 */
	void resetIntensityProcessedFID();

	/**
	 * Sets the intensity of the unprocessed FID.
	 *
	 * @param unprocessedIntensity
	 */
	void setIntensityUnprocessedFID(Complex unprocessedIntensity);

	/**
	 * Returns the intensity of the unprocessed FID.
	 * </p>
	 * This FID represents the analog version of the acquired
	 * digital FID without a possible digital filter.
	 *
	 * @see #getIntensityProcessedFID()
	 * @return the intensity of the unprocessed FID
	 */
	Complex getIntensityUnprocessedFID();

	/**
	 * Returns the acquisition time of the FID.
	 *
	 * @return time in nanoseconds
	 */
	long getAcquisitionTime();

	/**
	 * Sets the acquisition time of the FID.
	 *
	 * @param nanoseconds
	 */
	void setAcquisitionTime(long nanoseconds);
}
