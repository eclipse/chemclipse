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

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Instrument {

	@XmlElement(name = "instrumentDescription")
	private List<InstrumentDescription> instrumentDescriptionList;
	@XmlElement(name = "instrumentProperty")
	private List<InstrumentProperty> instrumentPropertyList;

	public List<InstrumentDescription> getInstrumentDescriptionList() {

		return instrumentDescriptionList;
	}

	public void setInstrumentDescriptionList(List<InstrumentDescription> instrumentDescriptionList) {

		this.instrumentDescriptionList = instrumentDescriptionList;
	}

	public List<InstrumentProperty> getInstrumentPropertyList() {

		return instrumentPropertyList;
	}

	public void setInstrumentPropertyList(List<InstrumentProperty> instrumentPropertyList) {

		this.instrumentPropertyList = instrumentPropertyList;
	}
}