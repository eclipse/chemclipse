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
public class InstrumentProperty {

	@XmlElement(name = "instrumentSetting")
	private String instrumentSetting;
	@XmlElement(name = "instrumentParameter")
	private String instrumentParameter;
	@XmlAttribute(name = "instrumentPropertyId")
	private String instrumentPropertyId;

	public String getInstrumentSetting() {

		return instrumentSetting;
	}

	public void setInstrumentSetting(String instrumentSetting) {

		this.instrumentSetting = instrumentSetting;
	}

	public String getInstrumentParameter() {

		return instrumentParameter;
	}

	public void setInstrumentParameter(String instrumentParameter) {

		this.instrumentParameter = instrumentParameter;
	}

	public String getInstrumentPropertyId() {

		return instrumentPropertyId;
	}

	public void setInstrumentPropertyId(String instrumentPropertyId) {

		this.instrumentPropertyId = instrumentPropertyId;
	}
}