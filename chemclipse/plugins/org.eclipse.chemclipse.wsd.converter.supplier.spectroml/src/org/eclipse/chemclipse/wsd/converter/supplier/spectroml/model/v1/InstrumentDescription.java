/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class InstrumentDescription {

	@XmlElement(name = "instrumentDesignation")
	private InstrumentDesignation instrumentDesignation;
	@XmlElement(name = "instrumentApplication")
	private InstrumentApplication instrumentApplication;
	@XmlAttribute(name = "instrumentDescriptionId")
	private String instrumentDescriptionId;

	public InstrumentDesignation getInstrumentDesignation() {

		return instrumentDesignation;
	}

	public void setInstrumentDesignation(InstrumentDesignation instrumentDesignation) {

		this.instrumentDesignation = instrumentDesignation;
	}

	public InstrumentApplication getInstrumentApplication() {

		return instrumentApplication;
	}

	public void setInstrumentApplication(InstrumentApplication instrumentApplication) {

		this.instrumentApplication = instrumentApplication;
	}

	public String getInstrumentDescriptionId() {

		return instrumentDescriptionId;
	}

	public void setInstrumentDescriptionId(String instrumentDescriptionId) {

		this.instrumentDescriptionId = instrumentDescriptionId;
	}
}