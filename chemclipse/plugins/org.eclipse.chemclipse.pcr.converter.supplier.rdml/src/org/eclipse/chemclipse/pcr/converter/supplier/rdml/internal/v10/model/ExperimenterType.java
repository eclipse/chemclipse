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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Contact details of an experimenter.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "experimenterType", propOrder = {"firstName", "lastName", "email", "labName", "labAddress"})
public class ExperimenterType {

	@XmlElement(required = true)
	protected String firstName;
	@XmlElement(required = true)
	protected String lastName;
	protected String email;
	protected String labName;
	protected String labAddress;
	@XmlAttribute(name = "id", required = true)
	protected String id;

	public String getFirstName() {

		return firstName;
	}

	public void setFirstName(String value) {

		this.firstName = value;
	}

	public String getLastName() {

		return lastName;
	}

	public void setLastName(String value) {

		this.lastName = value;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(String value) {

		this.email = value;
	}

	public String getLabName() {

		return labName;
	}

	public void setLabName(String value) {

		this.labName = value;
	}

	public String getLabAddress() {

		return labAddress;
	}

	public void setLabAddress(String value) {

		this.labAddress = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
