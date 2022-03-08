/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "precursorType", propOrder = {"ionSelection", "activation"})
public class PrecursorType {

	@XmlElement(required = true)
	private ParamType ionSelection;
	@XmlElement(required = true)
	private ParamType activation;
	@XmlAttribute(name = "msLevel", required = true)
	private int msLevel;
	@XmlAttribute(name = "spectrumRef", required = true)
	private int spectrumRef;

	public ParamType getIonSelection() {

		return ionSelection;
	}

	public void setIonSelection(ParamType value) {

		this.ionSelection = value;
	}

	public ParamType getActivation() {

		return activation;
	}

	public void setActivation(ParamType value) {

		this.activation = value;
	}

	public int getMsLevel() {

		return msLevel;
	}

	public void setMsLevel(int value) {

		this.msLevel = value;
	}

	public int getSpectrumRef() {

		return spectrumRef;
	}

	public void setSpectrumRef(int value) {

		this.spectrumRef = value;
	}
}
