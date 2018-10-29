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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;
import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;

public class ScanNMR extends AbstractMeasurementInfo implements IScanNMR {

	private static final long serialVersionUID = -4448729586928333575L;
	//
	private double[] rawSignals = new double[0];
	private Complex[] fourierTransformedData = new Complex[0];
	private Complex[] phaseCorrectedData = new Complex[0];
	private Complex[] baselineCorrectedData = new Complex[0];
	private final TreeSet<ISignalNMR> processedSignals = new TreeSet<>();
	private Map<String, Double> processingParametersMap = new LinkedHashMap<String, Double>();
	private Set<String> protectKeys = new LinkedHashSet<String>(processingParametersMap.keySet());

	@Override
	@Deprecated
	public Complex[] getBaselineCorrectedData() {

		return baselineCorrectedData;
	}

	@Override
	@Deprecated
	public void setBaselineCorrectedData(final Complex[] baselineCorrectedData) {

		this.baselineCorrectedData = baselineCorrectedData;
	}

	@Override
	@Deprecated
	public Complex[] getPhaseCorrectedData() {

		return phaseCorrectedData;
	}

	@Override
	@Deprecated
	public void setPhaseCorrectedData(final Complex[] phaseCorrectedData) {

		this.phaseCorrectedData = phaseCorrectedData;
	}

	@Override
	public double[] getRawSignals() {

		return rawSignals;
	}

	@Override
	public void setRawSignals(final double[] rawSignals) {

		this.rawSignals = rawSignals;
	}

	@Override
	@Deprecated
	public Complex[] getFourierTransformedData() {

		return fourierTransformedData;
	}

	@Override
	@Deprecated
	public void setFourierTransformedData(final Complex[] modifiedSignals) {

		this.fourierTransformedData = modifiedSignals;
	}

	@Override
	public TreeSet<ISignalNMR> getProcessedSignals() {

		return processedSignals;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(rawSignals);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if(this == obj) {
			return true;
		}
		if(!super.equals(obj)) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final ScanNMR other = (ScanNMR)obj;
		if(!Arrays.equals(rawSignals, other.rawSignals)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {

		return "ScanNMR [rawSignals=" + Arrays.toString(rawSignals) + "]";
	}

	@Override
	public Double getProcessingParameters(String key) {

		return processingParametersMap.get(key);
	}

	@Override
	public Double getProcessingParametersOrDefault(String key, Double defaultValue) {

		return processingParametersMap.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean processingParametersContainsKey(String key) {

		return processingParametersMap.containsKey(key);
	}

	@Override
	public void putProcessingParameters(String key, Double value) {

		processingParametersMap.put(key, value);
	}

	@Override
	public void removeProcessingParameters(String key) throws InvalidHeaderModificationException {

		if(protectKeys.contains(key)) {
			throw new InvalidHeaderModificationException("It's not possible to remove the following key: " + key);
		} else {
			processingParametersMap.remove(key);
		}
	}

	@Override
	public Map<String, Double> getprocessingParametersMap() {

		return Collections.unmodifiableMap(processingParametersMap);
	}
}
