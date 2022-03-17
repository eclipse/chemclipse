/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - complete redesign
 * Philip Wenig - refactoring Observable
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.selection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;

public class DataNMRSelection extends PropertyChangeSupport implements IDataNMRSelection {

	private static final long serialVersionUID = -6184349224960987655L;
	//
	private List<IComplexSignalMeasurement<?>> measurements = new ArrayList<>();
	private IComplexSignalMeasurement<?> measurement;
	private String name;

	public DataNMRSelection() {

		this("NMR");
	}

	public DataNMRSelection(String name) {

		super(name);
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
			firePropertyChange(new PropertyChangeEvent(this, name, measurement, ChangeType.NEW_ITEM));
		}
	}

	@Override
	public void setActiveMeasurement(IComplexSignalMeasurement<?> measurement) {

		if(measurement == null || measurements.contains(measurement)) {
			this.measurement = measurement;
			firePropertyChange(new PropertyChangeEvent(this, name, measurement, ChangeType.SELECTION_CHANGED));
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
			firePropertyChange(new PropertyChangeEvent(this, name, measurement, ChangeType.REMOVED_ITEM));
		}
	}

	@Override
	public void removeObserver(PropertyChangeListener observer) {

		removePropertyChangeListener(observer);
	}

	@Override
	public synchronized void addObserver(PropertyChangeListener observer) {

		addPropertyChangeListener(observer);
	}
}