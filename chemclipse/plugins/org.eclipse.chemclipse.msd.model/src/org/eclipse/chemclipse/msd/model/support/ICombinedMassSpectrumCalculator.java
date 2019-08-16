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
package org.eclipse.chemclipse.msd.model.support;

import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

// TODO JUnit
public interface ICombinedMassSpectrumCalculator {

	/**
	 * Adds the abundance of the given ion to the calculator.
	 * 
	 * @param ion
	 * @param abundance
	 */
	void addIon(double ion, float abundance);

	/**
	 * Adds the ions given in the list to the calculator.<br/>
	 * All ions listed in excludedIons will not be
	 * considered.
	 * 
	 * @param ions
	 * @param excludedIons
	 */
	void addIons(List<IIon> ions, IMarkedIons excludedIons);

	/**
	 * Removes the given ion.
	 * 
	 * @param ion
	 */
	void removeIon(double ion);

	/**
	 * Removes the given ions.
	 * 
	 * @param excludedIons
	 */
	void removeIons(IMarkedIons excludedIons);

	/**
	 * Normalizes the stored values to the given normalization factor.<br/>
	 * The biggest value gets the normalization factor. All others get values
	 * relative to it.<br/>
	 * The normalization factor must not be smaller or equal zero. That doesn't
	 * make sense.
	 * 
	 * @param normalizationFactor
	 */
	void normalize(float normalizationFactor);

	/**
	 * Returns the abundance value to the given ion (ion).
	 * 
	 * @param ion
	 * @return double
	 */
	double getAbundance(double ion);

	/**
	 * Returns a list of the stored key - values mappings.<br/>
	 * Use it as:<br/>
	 * key - ion<br/>
	 * value - abundance<br/>
	 * 
	 * @return Map<Integer, Double>
	 */
	Map<Integer, Double> getValues();
}
