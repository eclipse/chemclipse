/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

/**
 * @author eselmeister
 */
public interface IIonUniquenessValues {

	float MIN_PROBABILITY_VALUE = 0.0f;
	float MAX_PROBABILITY_VALUE = 1.0f;

	/**
	 * Adds a probability value for a given ion (ion) to the list.<br/>
	 * The probability value must be in the range of MIN_PROBABILITY_VALUE and
	 * MAX_PROBABILITY_VALUE.
	 * 
	 * @param ion
	 * @param probabilityValue
	 */
	void add(int ion, float probabilityValue);

	/**
	 * Removes the probability value for a given ion.
	 * 
	 * @param ion
	 */
	void remove(int ion);

	/**
	 * Returns the uniqueness value for a given ion.<br/>
	 * 0.0 (0%) means the ion is not unique (total presence), 1.0
	 * (100%) means the ion has only zero values over the total scan
	 * range (it is unique). This method is the counterpart to
	 * getPropabilityValue(int ion).
	 * 
	 * @param ion
	 * @return float
	 */
	float getUniquenessValue(int ion);

	/**
	 * Returns the probability value for a given ion.<br/>
	 * 0.0 (0%) means the ion is not present, 1.0 (100%) means the
	 * ion has no zero values over the total scan range. This method
	 * is the counterpart to getUniquenessValue(int ion).
	 * 
	 * @param ion
	 * @return float
	 */
	float getPropabilityValue(int ion);
}
