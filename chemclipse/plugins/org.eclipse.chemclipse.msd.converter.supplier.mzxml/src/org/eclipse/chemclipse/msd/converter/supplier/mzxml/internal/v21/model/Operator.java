/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v21.model;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
@XmlRootElement(name = "operator")
public class Operator implements Serializable {

	private static final long serialVersionUID = 210L;
	@XmlValue
	@XmlSchemaType(name = "anySimpleType")
	private Object value;
	@XmlAttribute(name = "first", required = true)
	private String first;
	@XmlAttribute(name = "last", required = true)
	private String last;
	@XmlAttribute(name = "phone")
	private String phone;
	@XmlAttribute(name = "email")
	private String email;
	@XmlAttribute(name = "URI")
	@XmlSchemaType(name = "anyURI")
	private String uri;

	public Object getValue() {

		return value;
	}

	public void setValue(Object value) {

		this.value = value;
	}

	public String getFirst() {

		return first;
	}

	public void setFirst(String value) {

		this.first = value;
	}

	public String getLast() {

		return last;
	}

	public void setLast(String value) {

		this.last = value;
	}

	public String getPhone() {

		return phone;
	}

	public void setPhone(String value) {

		this.phone = value;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(String value) {

		this.email = value;
	}

	public String getURI() {

		return uri;
	}

	public void setURI(String value) {

		this.uri = value;
	}
}
