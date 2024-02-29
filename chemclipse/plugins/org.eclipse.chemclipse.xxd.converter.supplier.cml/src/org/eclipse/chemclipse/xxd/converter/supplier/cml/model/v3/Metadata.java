/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class Metadata {

	@XmlValue
	protected String value;
	@XmlAttribute(name = "title")
	protected String title;
	@XmlAttribute(name = "content")
	protected String content;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "dictRef")
	protected String dictRef;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "convention")
	protected String convention;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<>();

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String value) {

		this.title = value;
	}

	public String getContent() {

		return content;
	}

	public void setContent(String value) {

		this.content = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public String getDictRef() {

		return dictRef;
	}

	public void setDictRef(String value) {

		this.dictRef = value;
	}

	public String getName() {

		return name;
	}

	public void setName(String value) {

		this.name = value;
	}

	public String getConvention() {

		return convention;
	}

	public void setConvention(String value) {

		this.convention = value;
	}

	public Map<QName, String> getOtherAttributes() {

		return otherAttributes;
	}
}
