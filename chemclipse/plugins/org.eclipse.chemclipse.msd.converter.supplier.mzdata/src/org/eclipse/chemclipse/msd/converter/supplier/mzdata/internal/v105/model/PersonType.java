/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personType", propOrder = {"name", "institution", "contactInfo"})
public class PersonType {

	@XmlElement(required = true)
	private String name;
	@XmlElement(required = true)
	private String institution;
	private String contactInfo;

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
