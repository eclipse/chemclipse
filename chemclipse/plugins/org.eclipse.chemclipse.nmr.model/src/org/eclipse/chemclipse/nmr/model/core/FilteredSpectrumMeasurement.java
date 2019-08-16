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

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractMeasurement;
import org.eclipse.chemclipse.model.core.FilteredMeasurement;
import org.eclipse.chemclipse.processing.filter.FilterContext;

/**
 * This class is meant as a class for Filters that wants to filter some aspects of a {@link SpectrumMeasurement}, the class simply delegates to an original {@link SpectrumMeasurement} and returns all his data to the caller.
 * A filter can now extend this class and return for example a filtered set of signals.
 * 
 * <b>Important</b> This class is not meant for format readers, these should extend {@link AbstractMeasurement} instead and implement the interface on a reader specific class
 * 
 * @author Christoph Läubrich
 *
 */
public class FilteredSpectrumMeasurement<ConfigType> extends FilteredMeasurement<SpectrumMeasurement, ConfigType> implements SpectrumMeasurement {

	private static final long serialVersionUID = -4028057722405624626L;
	private transient List<? extends SpectrumSignal> signals;

	public FilteredSpectrumMeasurement(FilterContext<SpectrumMeasurement, ConfigType> context) {
		super(context);
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
	public final AcquisitionParameter getAcquisitionParameter() {

		return getFilteredObject().getAcquisitionParameter();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

		out.defaultWriteObject();
		if(signals == null) {
			out.writeObject(null);
		} else {
			ArrayList<SpectrumSignal> serList = new ArrayList<>(signals.size());
			for(SpectrumSignal spectrumSignal : signals) {
				if(spectrumSignal instanceof Serializable) {
					// we can add it as is
					serList.add(spectrumSignal);
				} else {
					serList.add(new SerializableSpectrumSignal(spectrumSignal));
				}
			}
			out.writeObject(serList);
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

		in.defaultReadObject();
		signals = (List<? extends SpectrumSignal>)in.readObject();
	}

	private static final class SerializableSpectrumSignal implements SpectrumSignal, Serializable {

		private static final long serialVersionUID = -5726100506736511582L;
		private BigDecimal frequency;
		private Number absorptiveIntensity;
		private Number dispersiveIntensity;

		public SerializableSpectrumSignal(SpectrumSignal copyfrom) {
			frequency = copyfrom.getFrequency();
			absorptiveIntensity = copyfrom.getAbsorptiveIntensity();
			dispersiveIntensity = copyfrom.getDispersiveIntensity();
		}

		@Override
		public BigDecimal getFrequency() {

			return frequency;
		}

		@Override
		public Number getAbsorptiveIntensity() {

			return absorptiveIntensity;
		}

		@Override
		public Number getDispersiveIntensity() {

			return dispersiveIntensity;
		}
	}
}
