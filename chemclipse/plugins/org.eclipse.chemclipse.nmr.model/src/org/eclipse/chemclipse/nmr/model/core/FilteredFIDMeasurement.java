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
 * This class is meant as a class for Filters that wants to filter some aspects of a {@link FIDMeasurement}, the class simply delegates to an original {@link FIDMeasurement} and returns all his data to the caller.
 * A filter can now extend this class and return for example a filtered set of signals.
 * 
 * <b>Important</b> This class is not meant for format readers, these should extend {@link AbstractMeasurement} instead and implement the interface on a reader specific class
 * 
 * @author Christoph Läubrich
 *
 */
public class FilteredFIDMeasurement<ConfigType> extends FilteredMeasurement<FIDMeasurement, ConfigType> implements FIDMeasurement {

	private static final long serialVersionUID = -4499531764775929976L;
	private transient List<? extends FIDSignal> signals;

	public FilteredFIDMeasurement(FilterContext<FIDMeasurement, ConfigType> context) {
		super(context);
	}

	@Override
	public DataDimension getDataDimension() {

		return getFilteredObject().getDataDimension();
	}

	@Override
	public final AcquisitionParameter getAcquisitionParameter() {

		// Acquisition parameter can't change!
		return getFilteredObject().getAcquisitionParameter();
	}

	@Override
	public List<? extends FIDSignal> getSignals() {

		if(signals != null) {
			return signals;
		}
		return getFilteredObject().getSignals();
	}

	public final void setSignals(List<? extends FIDSignal> signals) {

		this.signals = signals;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

		out.defaultWriteObject();
		if(signals == null) {
			out.writeObject(null);
		} else {
			ArrayList<FIDSignal> serList = new ArrayList<>(signals.size());
			for(FIDSignal fidSignal : signals) {
				if(fidSignal instanceof Serializable) {
					// we can add it as is
					serList.add(fidSignal);
				} else {
					serList.add(new SerializableFIDSignal(fidSignal));
				}
			}
			out.writeObject(serList);
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

		in.defaultReadObject();
		signals = (List<? extends FIDSignal>)in.readObject();
	}

	private static final class SerializableFIDSignal implements FIDSignal, Serializable {

		private static final long serialVersionUID = -2997689760733045361L;
		private BigDecimal signalTime;
		private Number realComponent;
		private Number imaginaryComponent;

		public SerializableFIDSignal(FIDSignal tocopy) {
			signalTime = tocopy.getSignalTime();
			realComponent = tocopy.getRealComponent();
			imaginaryComponent = tocopy.getImaginaryComponent();
		}

		@Override
		public BigDecimal getSignalTime() {

			return signalTime;
		}

		@Override
		public Number getRealComponent() {

			return realComponent;
		}

		@Override
		public Number getImaginaryComponent() {

			return imaginaryComponent;
		}
	}
}
