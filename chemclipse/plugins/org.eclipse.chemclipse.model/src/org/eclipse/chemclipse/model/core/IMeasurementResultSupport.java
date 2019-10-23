/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for getting a result by class
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Collection;

public interface IMeasurementResultSupport {

	/**
	 * An existing result will be replaced if a measurement result with the same id
	 * is still stored.
	 * 
	 * @param chromatogramResult
	 */
	void addMeasurementResult(IMeasurementResult<?> chromatogramResult);

	/**
	 * Returns the result stored by the given identifier.
	 * 
	 * @param identifier
	 * @return {@link IMeasurementResult}
	 */
	IMeasurementResult<?> getMeasurementResult(String identifier);

	default <T extends IMeasurementResult<?>> T getMeasurementResult(Class<T> type) {

		Collection<IMeasurementResult<?>> measurementResults = getMeasurementResults();
		for(IMeasurementResult<?> result : measurementResults) {
			if(type.isInstance(result)) {
				return type.cast(result);
			}
		}
		return null;
	}

	/**
	 * Delete the measurement result stored by the given identifier.
	 * 
	 * @param identifier
	 */
	void deleteMeasurementResult(String identifier);

	/**
	 * Deletes all measurement results.
	 */
	void removeAllMeasurementResults();

	/**
	 * Returns a collection of all measurement results.
	 * 
	 * @return Collection
	 */
	Collection<IMeasurementResult<?>> getMeasurementResults();
}
