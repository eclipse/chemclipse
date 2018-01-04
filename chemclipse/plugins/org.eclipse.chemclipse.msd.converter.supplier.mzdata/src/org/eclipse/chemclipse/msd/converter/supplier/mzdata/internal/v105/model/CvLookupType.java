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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cvLookupType")
public class CvLookupType {

	@XmlAttribute(name = "cvLabel", required = true)
	private String cvLabel;
	@XmlAttribute(name = "fullName")
	private String fullName;
	@XmlAttribute(name = "version", required = true)
	private String version;
	@XmlAttribute(name = "address", required = true)
	@XmlSchemaType(name = "anyURI")
	private String address;

	public String getCvLabel() {

		return cvLabel;
	}

	public void setCvLabel(String value) {

		this.cvLabel = value;
	}

	public String getFullName() {

		return fullName;
	}

	public void setFullName(String value) {

		this.fullName = value;
	}

	public String getVersion() {

		return version;
	}

	public void setVersion(String value) {

		this.version = value;
	}

	public String getAddress() {

		return address;
	}

	public void setAddress(String value) {

		this.address = value;
	}
}
