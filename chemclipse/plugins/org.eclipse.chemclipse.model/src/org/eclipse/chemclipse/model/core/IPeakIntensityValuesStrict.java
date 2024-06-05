/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.io.Serializable;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public interface IPeakIntensityValuesStrict extends Serializable {

	/**
	 * Calculates the increasing inflection point equation.<br/>
	 * You must provide the total signal of the peak maximum unless the
	 * intensity values store only a percentage distribution between 0 (0%) and
	 * 100 (100%) signal.<br/>
	 * The intensity do not know nothing about the total signal of the peak, but
	 * to not adjust the equation each time to the total signal, it is important
	 * to provide such value.
	 * 
	 * @param totalSignal
	 * @throws PeakException
	 * @return LinearEquation
	 */
	LinearEquation calculateIncreasingInflectionPointEquation(float totalSignal) throws PeakException;

	/**
	 * Calculates the decreasing inflection point equation. You must provide the
	 * total signal of the peak maximum unless the intensity values store only a
	 * percentage distribution between 0 (0%) and 100 (100%) signal.<br/>
	 * The intensity do not know nothing about the total signal of the peak, but
	 * to not adjust the equation each time to the total signal, it is important
	 * to provide such value.
	 * 
	 * @param totalSignal
	 * @throws PeakException
	 * @return LinearEquation
	 */
	LinearEquation calculateDecreasingInflectionPointEquation(float totalSignal) throws PeakException;
}