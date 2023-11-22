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

	public static final String PROPERTY_PRECURSOR_TYPE = "Precursor Type"; // [M+H]+, [M-H]-, ...
	public static final String PROPERTY_COLLISION_ENERGY = "Collision Energy"; // 30
	public static final String PROPERTY_INSTRUMENT_NAME = "Instrument Name"; // Agilent QTOF 6530
	public static final String PROPERTY_INSTRUMENT_TYPE = "Instrument Type"; // LC-ESI-QTOF

	String getPrecursorType();

	void setPrecursorType(String precursorType);

	double getNeutralMass();

	void setNeutralMass(double neutralMass);

	/**
	 * Returns the polarity if set.
	 * 
	 * Otherwise returns the polarity (+) or (-)
	 * if the precursor type is set.
	 * 
	 * If none is available "" will be returned.
	 * 
	 * @return String
	 */
	String getPolarity();

	void setPolarity(String polarity);

	Set<String> getPropertyKeySet();

	String getProperty(String property);

	void putProperty(String key, String value);
}