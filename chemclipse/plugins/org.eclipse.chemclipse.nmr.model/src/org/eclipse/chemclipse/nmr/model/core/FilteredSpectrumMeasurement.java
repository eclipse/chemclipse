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

import org.eclipse.chemclipse.model.core.AbstractMeasurement;
import org.eclipse.chemclipse.model.core.FilteredMeasurement;

/**
 * This class is meant as a class for Filters that wants to filter some aspects of a {@link SpectrumMeasurement}, the class simply delegates to an original {@link SpectrumMeasurement} and returns all his data to the caller.
 * A filter can now extend this class and return for example a filtered set of signals.
 * 
 * <b>Important</b> This class is not meant for format readers, these should extend {@link AbstractMeasurement} instead and implement the interface on a reader specific class
 * 
 * @author Christoph Läubrich
 *
 */
public class FilteredSpectrumMeasurement extends FilteredMeasurement<SpectrumMeasurement> implements SpectrumMeasurement {

	private static final long serialVersionUID = -4028057722405624626L;
	private List<? extends SpectrumSignal> signals;

	public FilteredSpectrumMeasurement(SpectrumMeasurement original) {
		super(original);
	}

	@Override
	public List<? extends SpectrumSignal> getSignals() {

		if(signals != null) {
			return signals;
		}
		return getFilteredObject().getSignals();
	}

	public void setSignals(List<? extends SpectrumSignal> signals) {

		this.signals = signals;
	}

	@Override
	public SpectrumAcquisitionParameter getAcquisitionParameter() {

		return getFilteredObject().getAcquisitionParameter();
	}
}
