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
public class Supplier {

	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "contact")
	private String contact;

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getContact() {

		return contact;
	}

	public void setContact(String contact) {

		this.contact = contact;
	}
}