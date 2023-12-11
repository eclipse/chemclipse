/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.io.Serializable;

public interface IRatingSupplier extends Serializable {

	/**
	 * Returns an advice, e.g. "The spectrum is of low quality".
	 * 
	 * @return String
	 */
	String getAdvise();

	/**
	 * Returns a score between 0 and 100.
	 * 0 - bad
	 * 100 - perfect
	 * 
	 * @return
	 */
	float getScore();

	RatingStatus getStatus();

	/**
	 * Must not be null.
	 * Updates the underlying comparison result required to calculate the
	 * rating, advise and scheme.
	 * 
	 * @param comparisonResult
	 */
	void updateComparisonResult(IComparisonResult comparisonResult);
}