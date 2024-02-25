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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v22.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"plateManufacturer", "plateModel", "pattern", "spot"})
public class Plate implements Serializable {

	private static final long serialVersionUID = 220L;
	@XmlElement(required = true)
	private OntologyEntry plateManufacturer;
	@XmlElement(required = true)
	private OntologyEntry plateModel;
	private Pattern pattern;
	@XmlElement(required = true)
	private List<Spot> spot;
	@XmlAttribute(name = "plateID", required = true)
	private String plateID;
	@XmlAttribute(name = "spotXCount", required = true)
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger spotXCount;
	@XmlAttribute(name = "spotYCount", required = true)
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger spotYCount;

	public OntologyEntry getPlateManufacturer() {

		return plateManufacturer;
	}

	public void setPlateManufacturer(OntologyEntry value) {

		this.plateManufacturer = value;
	}

	public OntologyEntry getPlateModel() {

		return plateModel;
	}

	public void setPlateModel(OntologyEntry value) {

		this.plateModel = value;
	}

	public Pattern getPattern() {

		return pattern;
	}

	public void setPattern(Pattern value) {

		this.pattern = value;
	}

	public List<Spot> getSpot() {

		if(spot == null) {
			spot = new ArrayList<Spot>();
		}
		return this.spot;
	}

	public String getPlateID() {

		return plateID;
	}

	public void setPlateID(String value) {

		this.plateID = value;
	}

	public BigInteger getSpotXCount() {

		return spotXCount;
	}

	public void setSpotXCount(BigInteger value) {

		this.spotXCount = value;
	}

	public BigInteger getSpotYCount() {

		return spotYCount;
	}

	public void setSpotYCount(BigInteger value) {

		this.spotYCount = value;
	}
}
