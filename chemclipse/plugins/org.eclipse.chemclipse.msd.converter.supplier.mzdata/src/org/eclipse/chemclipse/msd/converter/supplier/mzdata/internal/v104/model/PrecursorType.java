/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "precursorType", propOrder = {"ionSelection", "activation"})
public class PrecursorType {

	@XmlElement(required = true)
	protected ParamType ionSelection;
	@XmlElement(required = true)
	protected ParamType activation;
	@XmlAttribute(name = "msLevel", required = true)
	protected int msLevel;
	@XmlAttribute(name = "spectrumRef", required = true)
	protected int spectrumRef;

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
