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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v21.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ontologyEntryType")
@XmlSeeAlso({MsManufacturer.class, MsMassAnalyzer.class})
public class OntologyEntry implements Serializable {

	private final static long serialVersionUID = 210L;
	@XmlAttribute(name = "category", required = true)
	private String category;
	@XmlAttribute(name = "value", required = true)
	private String theValue;

	public String getCategory() {

		return category;
	}

	public void setCategory(String value) {

		this.category = value;
	}

	public String getTheValue() {

		return theValue;
	}

	public void setTheValue(String value) {

		this.theValue = value;
	}
}
