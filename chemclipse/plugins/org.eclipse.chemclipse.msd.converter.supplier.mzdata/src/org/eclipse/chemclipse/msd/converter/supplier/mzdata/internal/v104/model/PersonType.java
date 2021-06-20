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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personType", propOrder = {"name", "institution", "contactInfo"})
public class PersonType {

	@XmlElement(required = true)
	protected String name;
	@XmlElement(required = true)
	protected String institution;
	protected String contactInfo;

	public String getName() {

		return name;
	}

	public void setName(String value) {

		this.name = value;
	}

	public String getInstitution() {

		return institution;
	}

	public void setInstitution(String value) {

		this.institution = value;
	}

	public String getContactInfo() {

		return contactInfo;
	}

	public void setContactInfo(String value) {

		this.contactInfo = value;
	}
}
