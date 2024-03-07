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
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class InstrumentDesignation {

	@XmlElement(name = "identifier")
	private String identifier;
	@XmlElement(name = "manufacturer")
	private String manufacturer;
	@XmlElement(name = "model")
	private String model;
	@XmlElement(name = "owner")
	private String owner;
	@XmlElement(name = "location")
	private Location location;
	@XmlElement(name = "comment")
	private String comment;

	public String getIdentifier() {

		return identifier;
	}

	public void setIdentifier(String identifier) {

		this.identifier = identifier;
	}

	public String getManufacturer() {

		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {

		this.manufacturer = manufacturer;
	}

	public String getModel() {

		return model;
	}

	public void setModel(String model) {

		this.model = model;
	}

	public String getOwner() {

		return owner;
	}

	public void setOwner(String owner) {

		this.owner = owner;
	}

	public Location getLocation() {

		return location;
	}

	public void setLocation(Location location) {

		this.location = location;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}
