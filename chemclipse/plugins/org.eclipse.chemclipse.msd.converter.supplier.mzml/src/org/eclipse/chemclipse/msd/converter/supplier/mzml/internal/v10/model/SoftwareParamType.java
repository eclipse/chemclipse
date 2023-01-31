/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v10.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoftwareParamType")
public class SoftwareParamType {

	@XmlAttribute(name = "cvRef", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object cvRef;
	@XmlAttribute(name = "accession", required = true)
	protected String accession;
	@XmlAttribute(name = "name", required = true)
	protected String name;
	@XmlAttribute(name = "version", required = true)
	protected String version;

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
}
