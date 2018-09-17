/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.model.core.IMeasurementInfo;

public interface IScanNMR extends IMeasurementInfo {

	double[] getRawSignals();

	void setRawSignals(double[] rawSignals);

	/**
	 * Returns the fourier-transformed data.
	 *
	 * @return the fourier-transformed data
	 */
	Complex[] getFourierTransformedData();

	/**
	 * Sets the the fourier-transformed data.
	 *
	 * @param modifiedSignals
	 *            the fourier-transformed data
	 */
	void setFourierTransformedData(Complex[] data);

	Complex[] getPhaseCorrectedData();

	void setPhaseCorrectedData(Complex[] data);

	Complex[] getBaselineCorrectedData();

	void setBaselineCorrectedData(Complex[] data);

	TreeSet<ISignalNMR> getProcessedSignals();

	Map<String, Double> getProcessingParameters();

	void setProcessingParameters(Map<String, Double> processingParameters);
}