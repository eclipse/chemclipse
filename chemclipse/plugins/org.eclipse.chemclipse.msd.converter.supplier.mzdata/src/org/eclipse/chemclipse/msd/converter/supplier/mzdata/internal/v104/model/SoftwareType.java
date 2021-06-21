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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "softwareType", propOrder = {"name", "version", "comments"})
@XmlSeeAlso({DataProcessingType.Software.class})
public class SoftwareType {

	@XmlElement(required = true)
	protected String name;
	@XmlElement(required = true)
	protected String version;
	protected String comments;
	@XmlAttribute(name = "completionTime")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar completionTime;

	public String getName() {

		return name;
	}

	public void setName(String value) {

		this.name = value;
	}

	public String getVersion() {

		return version;
	}

	public void setVersion(String value) {

		this.version = value;
	}

	public String getComments() {

		return comments;
	}

	public void setComments(String value) {

		this.comments = value;
	}

	public XMLGregorianCalendar getCompletionTime() {

		return completionTime;
	}

	public void setCompletionTime(XMLGregorianCalendar value) {

		this.completionTime = value;
	}
}
