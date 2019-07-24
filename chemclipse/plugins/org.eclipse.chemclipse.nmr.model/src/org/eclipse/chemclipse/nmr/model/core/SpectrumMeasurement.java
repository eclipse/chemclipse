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

import java.util.List;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;

public interface SpectrumMeasurement extends IComplexSignalMeasurement<SpectrumSignal> {

	/**
	 * 
	 * @return the Acquisition Parameter for this spectral measurement
	 */
	SpectrumAcquisitionParameter getAcquisitionParameter();

	/**
	 * Contains the signals of this {@link SpectrumMeasurement}, ordered with the highest ppm value first
	 * 
	 * @return the signals that makes up this {@link SpectrumMeasurement}
	 */
	@Override
	List<? extends SpectrumSignal> getSignals();

	/**
	 * 
	 * @param centerFrequency
	 *            center frequency in megahertz
	 * @param ppm
	 *            ppm value to convert
	 * @return
	 */
	default double ppmToHz(double ppm) {

		return (getAcquisitionParameter().getSpectrometerFrequency() * 1000d * ppm) / 10E6;
	}
}
