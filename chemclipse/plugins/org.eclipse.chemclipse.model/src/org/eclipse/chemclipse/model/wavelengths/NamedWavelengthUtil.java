/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.wavelengths;

public class NamedWavelengthUtil {

	private static final String TRADITIONAL = "Traditional Wavelengths";
	private static final String MERCURY_LAMP = "Low Pressure Mercury Lamp";
	private static final String ZINC_LAMP = "Zinc Lamp";
	//

	public static final String getDefaultWavelengths() {

		NamedWavelengths namedWavelengths = new NamedWavelengths();
		//
		namedWavelengths.add(new NamedWavelength(TRADITIONAL, "214 254 280 365"));
		namedWavelengths.add(new NamedWavelength(MERCURY_LAMP, "254")); // 253.7 nm
		namedWavelengths.add(new NamedWavelength(ZINC_LAMP, "214"));
		//
		return namedWavelengths.save();
	}
}
