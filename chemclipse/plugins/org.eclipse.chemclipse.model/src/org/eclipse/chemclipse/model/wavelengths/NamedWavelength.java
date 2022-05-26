/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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

public class NamedWavelength {

	private String identifier = "";
	private String wavelength = "";

	public NamedWavelength(String identifier, String wavelength) {

		this.identifier = identifier;
		this.wavelength = wavelength;
	}

	public String getIdentifier() {

		return identifier;
	}

	public void setIdentifier(String identifier) {

		this.identifier = identifier;
	}

	public String getWavelengths() {

		return wavelength;
	}

	public void setWavelengths(String wavelength) {

		this.wavelength = wavelength;
	}
}
