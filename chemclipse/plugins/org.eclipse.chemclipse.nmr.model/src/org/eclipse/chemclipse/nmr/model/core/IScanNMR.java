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
import java.util.NavigableSet;
import java.util.TreeSet;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;

public interface IScanNMR extends IMeasurementInfo {

	@Deprecated
	double[] getRawSignals();

	@Deprecated
	void setRawSignals(double[] rawSignals);

	/**
	 * Returns the fourier-transformed data.
	 *
	 * @return the fourier-transformed data
	 */
	@Deprecated
	Complex[] getFourierTransformedData();

	/**
	 * Sets the the fourier-transformed data.
	 *
	 * @param modifiedSignals
	 *            the fourier-transformed data
	 */
	@Deprecated
	void setFourierTransformedData(Complex[] data);

	@Deprecated
	Complex[] getPhaseCorrectedData();

	@Deprecated
	void setPhaseCorrectedData(Complex[] data);

	@Deprecated
	Complex[] getBaselineCorrectedData();

	@Deprecated
	void setBaselineCorrectedData(Complex[] data);

	@Deprecated
	TreeSet<ISignalNMR> getProcessedSignals();

	NavigableSet<ISignalNMR> getSignalsNMR();

	NavigableSet<ISignalFID> getSignalsFID();

	void addSignalNMR(ISignalNMR signalNMR);

	void addSignalFID(ISignalFID signalFID);

	void removeSignalNMR(ISignalNMR signalNMR);

	void removeSignalFID(ISignalFID signalFID);

	void removeAllSignalsFID();

	void removeAllSignalsNMR();

	int getNumberOfFourierPoints();

	void setNumberOfFourierPoints(int numberOfFourierPoints);

	/*
	 * processing parameters
	 */
	Double getProcessingParameters(String key);

	Double getProcessingParametersOrDefault(String key, Double defaultValue);

	boolean processingParametersContainsKey(String key);

	void putProcessingParameters(String key, Double value);

	Map<String, Double> getprocessingParametersMap();

	void removeProcessingParameters(String key) throws InvalidHeaderModificationException;
}