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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;

public class DataNMRSelection extends Observable implements IDataNMRSelection {

	private List<IComplexSignalMeasurement<?>> measurements = new ArrayList<>();
	private IComplexSignalMeasurement<?> measurement;
	private String name;

	public DataNMRSelection() {
		this("NMR");
	}

	public DataNMRSelection(String name) {
		this.name = name;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public synchronized IComplexSignalMeasurement<?> getMeasurement() {

		return measurement;
	}

	@Override
	public void replace(IComplexSignalMeasurement<?> measurement, IComplexSignalMeasurement<?> replacement) {

		int indexOf = measurements.indexOf(measurement);
		if(indexOf > 0) {
			measurements.set(indexOf, replacement);
			if(this.measurement == measurement) {
				setActiveMeasurement(replacement);
			}
		}
	}

	@Override
	public synchronized void addMeasurement(IComplexSignalMeasurement<?> measurement) {

		if(!measurements.contains(measurement)) {
			measurements.add(measurement);
			setActiveMeasurement(measurement);
			setChanged();
			notifyObservers(ChangeType.NEW_ITEM);
		}
	}

	@Override
	public void setActiveMeasurement(IComplexSignalMeasurement<?> measurement) {

		if(measurement == null || measurements.contains(measurement)) {
			this.measurement = measurement;
			setChanged();
			notifyObservers(ChangeType.SELECTION_CHANGED);
		}
	}

	@Override
	public synchronized IComplexSignalMeasurement<?>[] getMeasurements() {

		return measurements.toArray(new IComplexSignalMeasurement<?>[0]);
	}

	public synchronized void removeMeasurement(IComplexSignalMeasurement<?> measurement) {

		if(measurements.remove(measurement)) {
			if(this.measurement == measurement) {
				if(measurements.size() > 0) {
					setActiveMeasurement(measurements.get(measurements.size() - 1));
				} else {
					setActiveMeasurement(null);
				}
			}
			setChanged();
			notifyObservers(ChangeType.REMOVED_ITEM);
		}
	}

	@Override
	public void removeObserver(Observer observer) {

		deleteObserver(observer);
	}

	@Override
	public synchronized void addObserver(Observer o) {

		super.addObserver(o);
	}
}
