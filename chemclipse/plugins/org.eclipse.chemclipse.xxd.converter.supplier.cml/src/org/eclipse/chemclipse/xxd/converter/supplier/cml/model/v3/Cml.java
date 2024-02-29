/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "cml")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"spectrum"})
public class Cml {

	@XmlElement(name = "spectrum")
	protected Spectrum spectrum;

	public void setSpectrum(Spectrum spectrum) {

		this.spectrum = spectrum;
	}

	public Spectrum getSpectrum() {

		return spectrum;
	}
}
