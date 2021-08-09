/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
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

import java.util.Collection;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IMarkedSignals;

public interface IMarkedWavelengths extends IMarkedSignals<IMarkedWavelength> {

	/**
	 * Returns the set of wavelengths.
	 * 
	 * @return Set<Double>
	 */
	Set<Double> getWavelengths();

	/**
	 * Adds the ion range with magnification factor = 1.
	 * 
	 * @param wavelength
	 */
	void add(double... wavelength);

	void add(Collection<? extends Number> wavelengths);

	/**
	 * Adds the ion range with magnification factor = 1.
	 * Deprecated because wavelength could be double values
	 * 
	 * @param wavelengthStart
	 * @param wavelengthStop
	 */
	@Deprecated
	void add(int wavelengthStart, int wavelengthStop);
}
