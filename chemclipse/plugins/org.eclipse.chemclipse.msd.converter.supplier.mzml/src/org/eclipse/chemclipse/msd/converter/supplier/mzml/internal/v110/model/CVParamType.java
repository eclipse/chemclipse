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
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CVParamType")
public class CVParamType {

	@XmlAttribute(name = "cvRef", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object cvRef;
	@XmlAttribute(name = "accession", required = true)
	private String accession;
	@XmlAttribute(name = "value")
	private String value;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "unitAccession")
	private String unitAccession;
	@XmlAttribute(name = "unitName")
	private String unitName;
	@XmlAttribute(name = "unitCvRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object unitCvRef;

	public Object getCvRef() {

		return cvRef;
	}

	public void setCvRef(Object value) {

		this.cvRef = value;
	}

	public String getAccession() {

		return accession;
	}

	public void setAccession(String value) {

		this.accession = value;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getName() {

		return name;
	}

	public void setName(String value) {

		this.name = value;
	}

	public String getUnitAccession() {

		return unitAccession;
	}

	public void setUnitAccession(String value) {

		this.unitAccession = value;
	}

	public String getUnitName() {

		return unitName;
	}

	public void setUnitName(String value) {

		this.unitName = value;
	}

	public Object getUnitCvRef() {

		return unitCvRef;
	}

	public void setUnitCvRef(Object value) {

		this.unitCvRef = value;
	}
}
