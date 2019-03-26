/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - complete redesign
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.selection;

import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.core.runtime.Adapters;

public class DataNMRSelection implements IDataNMRSelection {

	private Queue<IComplexSignalMeasurement<?>> measurements = new LinkedList<>();

	public DataNMRSelection(IComplexSignalMeasurement<?> measurement) {
		measurements.add(measurement);
	}

	@Override
	public synchronized IComplexSignalMeasurement<?> getMeasurement() {

		return measurements.peek();
	}

	@Override
	public <T extends IComplexSignalMeasurement<?>> T getMeasurement(Class<T> type) {

		return Adapters.adapt(getMeasurement(), type);
	}

	public void addMeasurement(IComplexSignalMeasurement<?> measurement) {

		measurements.add(measurement);
	}

	public synchronized IComplexSignalMeasurement<?>[] getMeasurements() {

		return measurements.toArray(new IComplexSignalMeasurement<?>[0]);
	}

	public synchronized int size() {

		return measurements.size();
	}

	public synchronized IComplexSignalMeasurement<?> remove() {

		if(measurements.size() > 1) {
			return measurements.poll();
		} else {
			return null;
		}
	}
}
