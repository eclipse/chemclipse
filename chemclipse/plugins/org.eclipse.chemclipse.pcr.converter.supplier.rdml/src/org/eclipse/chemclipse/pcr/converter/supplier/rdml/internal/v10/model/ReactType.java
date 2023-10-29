/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v10.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A reaction is an independent chemical reaction corresponding for example
 * to a well in a 96 well plate, a capillary in a rotor or a droplet on the
 * biotrophe slides.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reactType", propOrder = {"sample", "data"})
public class ReactType {

	@XmlElement(required = true)
	protected IdReferencesType sample;
	@XmlElement(required = true)
	protected List<DataType> data;
	@XmlAttribute(name = "id", required = true)
	protected String id;

	public IdReferencesType getSample() {

		return sample;
	}

	public void setSample(IdReferencesType value) {

		this.sample = value;
	}

	public List<DataType> getData() {

		if(data == null) {
			data = new ArrayList<>();
		}
		return this.data;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
