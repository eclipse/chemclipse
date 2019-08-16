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

import java.util.Observer;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurement;

public interface IDataNMRSelection {

	enum ChangeType {
		SELECTION_CHANGED, NEW_ITEM, REMOVED_ITEM;
	}

	/**
	 * 
	 * @return the name of this selection
	 */
	String getName();

	/**
	 * 
	 * @return the current active {@link IMeasurement}
	 */
	IComplexSignalMeasurement<?> getMeasurement();

	default void setActiveMeasurement(IComplexSignalMeasurement<?> selection) {

		throw new UnsupportedOperationException();
	}

	default void addMeasurement(IComplexSignalMeasurement<?> measurement) {

		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @return all measurements that belongs to this selection
	 */
	IComplexSignalMeasurement<?>[] getMeasurements();

	/**
	 * Adds an observer that is called whenever the current active selection changes
	 * 
	 * @param observer
	 */
	void addObserver(Observer observer);

	void removeObserver(Observer observer);

	default void replace(IComplexSignalMeasurement<?> measurement, IComplexSignalMeasurement<?> replacement) {

		throw new UnsupportedOperationException();
	}
}
