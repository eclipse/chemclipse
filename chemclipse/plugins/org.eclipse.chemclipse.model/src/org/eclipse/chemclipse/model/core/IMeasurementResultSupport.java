/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
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
	void addMeasurementResult(IMeasurementResult chromatogramResult);

	/**
	 * Returns the result stored by the given identifier.
	 * 
	 * @param identifier
	 * @return {@link IMeasurementResult}
	 */
	IMeasurementResult getMeasurementResult(String identifier);

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
	Collection<IMeasurementResult> getMeasurementResults();
}
