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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v20.model;

import java.io.Serializable;
import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"maldiMatrix"})
public class Spot implements Serializable {

	private static final long serialVersionUID = 200L;
	@XmlElement(required = true)
	private OntologyEntry maldiMatrix;
	@XmlAttribute(name = "spotID", required = true)
	private String spotID;
	@XmlAttribute(name = "spotXPosition", required = true)
	private String spotXPosition;
	@XmlAttribute(name = "spotYPosition", required = true)
	private String spotYPosition;
	@XmlAttribute(name = "spotDiameter")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger spotDiameter;

	public OntologyEntry getMaldiMatrix() {

		return maldiMatrix;
	}

	public void setMaldiMatrix(OntologyEntry value) {

		this.maldiMatrix = value;
	}

	public String getSpotID() {

		return spotID;
	}

	public void setSpotID(String value) {

		this.spotID = value;
	}

	public String getSpotXPosition() {

		return spotXPosition;
	}

	public void setSpotXPosition(String value) {

		this.spotXPosition = value;
	}

	public String getSpotYPosition() {

		return spotYPosition;
	}

	public void setSpotYPosition(String value) {

		this.spotYPosition = value;
	}

	public BigInteger getSpotDiameter() {

		return spotDiameter;
	}

	public void setSpotDiameter(BigInteger value) {

		this.spotDiameter = value;
	}
}
