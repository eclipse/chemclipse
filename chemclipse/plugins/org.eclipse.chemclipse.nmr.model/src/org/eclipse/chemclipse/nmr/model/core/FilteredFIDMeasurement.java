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
import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractMeasurement;
import org.eclipse.chemclipse.model.core.FilteredMeasurement;

/**
 * This class is meant as a class for Filters that wants to filter some aspects of a {@link FIDMeasurement}, the class simply delegates to an original {@link FIDMeasurement} and returns all his data to the caller.
 * A filter can now extend this class and return for example a filtered set of signals.
 * 
 * <b>Important</b> This class is not meant for format readers, these should extend {@link AbstractMeasurement} instead and implement the interface on a reader specific class
 * 
 * @author Christoph Läubrich
 *
 */
public class FilteredFIDMeasurement extends FilteredMeasurement<FIDMeasurement> implements FIDMeasurement, FIDAcquisitionParameter {

	private static final long serialVersionUID = -4499531764775929976L;
	private List<? extends FIDSignal> signals;

	public FilteredFIDMeasurement(FIDMeasurement measurement) {
		super(measurement);
	}

	@Override
	public DataDimension getDataDimension() {

		return getFilteredObject().getDataDimension();
	}

	@Override
	public FIDAcquisitionParameter getAcquisitionParameter() {

		return this;
	}

	@Override
	public List<? extends FIDSignal> getSignals() {

		if(signals != null) {
			return signals;
		}
		return getFilteredObject().getSignals();
	}

	public void setSignals(List<? extends FIDSignal> signals) {

		this.signals = signals;
	}

	@Override
	public double getPulseWidth() {

		return getFilteredObject().getAcquisitionParameter().getPulseWidth();
	}

	@Override
	public BigDecimal getAcquisitionTime() {

		if(signals != null) {
			return signals.get(signals.size() - 1).getSignalTime();
		}
		return getFilteredObject().getAcquisitionParameter().getAcquisitionTime();
	}

	@Override
	public int getNumberOfPoints() {

		if(signals != null) {
			return signals.size();
		}
		return getFilteredObject().getAcquisitionParameter().getNumberOfPoints();
	}

	@Override
	public double getRecycleDelay() {

		return getFilteredObject().getAcquisitionParameter().getRecycleDelay();
	}
}
