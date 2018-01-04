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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v30.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "namevalueType", propOrder = {"value"})
public class NameValue implements Serializable {

	private final static long serialVersionUID = 300L;
	@XmlValue
	@XmlSchemaType(name = "anySimpleType")
	private Object value;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "value")
	@XmlSchemaType(name = "anySimpleType")
	private String theValue;
	@XmlAttribute(name = "type")
	@XmlSchemaType(name = "anySimpleType")
	private String type;

	public Object getValue() {

		return value;
	}

	public void setValue(Object value) {

		this.value = value;
	}

	public String getName() {

		return name;
	}

	public void setName(String value) {

		this.name = value;
	}

	public String getTheValue() {

		return theValue;
	}

	public void setTheValue(String value) {

		this.theValue = value;
	}

	public String getType() {

		return type;
	}

	public void setType(String value) {

		this.type = value;
	}
}
