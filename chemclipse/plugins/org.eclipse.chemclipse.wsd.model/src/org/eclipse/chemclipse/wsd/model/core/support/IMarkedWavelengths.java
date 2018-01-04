/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import java.util.Set;

public interface IMarkedWavelengths extends Set<IMarkedWavelength> {

	/**
	 * Returns the set of wavelengths.
	 * 
	 * @return Set<Double>
	 */
	Set<Double> getWavelengths();

	/**
	 * Adds the ion range with magnification factor = 1.
	 * 
	 * @param wavelengthStart
	 * @param wavelengthStop
	 */
	void add(int wavelengthStart, int wavelengthStop);
}
