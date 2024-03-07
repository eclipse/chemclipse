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
public class MeasurementDesignation {

	@XmlElement(name = "identifier")
	private String identifier;
	@XmlElement(name = "title")
	private String title;
	@XmlElement(name = "owner")
	private Owner owner;
	@XmlElement(name = "laboratoryReference")
	private String laboratoryReference;
	@XmlElement(name = "comment")
	private String comment;

	public String getIdentifier() {

		return identifier;
	}

	public void setIdentifier(String identifier) {

		this.identifier = identifier;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public Owner getOwner() {

		return owner;
	}

	public void setOwner(Owner owner) {

		this.owner = owner;
	}

	public String getLaboratoryReference() {

		return laboratoryReference;
	}

	public void setLaboratoryReference(String laboratoryReference) {

		this.laboratoryReference = laboratoryReference;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}