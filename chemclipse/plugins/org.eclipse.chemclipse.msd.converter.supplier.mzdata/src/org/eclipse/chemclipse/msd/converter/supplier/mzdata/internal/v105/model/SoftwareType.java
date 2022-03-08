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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "softwareType", propOrder = {"name", "version", "comments"})
@XmlSeeAlso({org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.DataProcessingType.Software.class})
public class SoftwareType {

	@XmlElement(required = true)
	private String name;
	@XmlElement(required = true)
	private String version;
	private String comments;
	@XmlAttribute(name = "completionTime")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar completionTime;

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
