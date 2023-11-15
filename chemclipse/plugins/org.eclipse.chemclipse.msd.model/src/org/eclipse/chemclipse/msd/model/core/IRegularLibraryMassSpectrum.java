/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.Set;

public interface IRegularLibraryMassSpectrum extends IRegularMassSpectrum, ILibraryMassSpectrum {

	public static final String PROPERTY_PRECURSOR_TYPE = "Precursor Type";

	void setPrecursorType(String precursorType);

	/**
	 * TODO - it's not fully implemented yet!
	 * ----
	 * If the precursor type e.g. '[M+H]+' is available in the properties,
	 * the neutral mass will be calculated. Otherwise, this method returns
	 * the value of the given precursor ion.
	 * 
	 * @return double
	 */
	double getNeutralMass(double precursorIon);

	/**
	 * Returns the polarity (+) or (-) if the precursor type is set.
	 * If none is available "" will be returned.
	 * 
	 * @return String
	 */
	String getPolarity();

	Set<String> getPropertyKeySet();

	String getProperty(String property);

	void putProperty(String key, String value);
}