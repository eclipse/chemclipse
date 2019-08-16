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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v22.model;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Maldi implements Serializable {

	private final static long serialVersionUID = 220L;
	@XmlAttribute(name = "plateID", required = true)
	private String plateID;
	@XmlAttribute(name = "spotID", required = true)
	private String spotID;
	@XmlAttribute(name = "laserShootCount")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger laserShootCount;
	@XmlAttribute(name = "laserFrequency")
	private Duration laserFrequency;
	@XmlAttribute(name = "laserIntensity")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger laserIntensity;
	@XmlAttribute(name = "collisionGas")
	private Boolean collisionGas;

	public String getPlateID() {

		return plateID;
	}

	public void setPlateID(String value) {

		this.plateID = value;
	}

	public String getSpotID() {

		return spotID;
	}

	public void setSpotID(String value) {

		this.spotID = value;
	}

	public BigInteger getLaserShootCount() {

		return laserShootCount;
	}

	public void setLaserShootCount(BigInteger value) {

		this.laserShootCount = value;
	}

	public Duration getLaserFrequency() {

		return laserFrequency;
	}

	public void setLaserFrequency(Duration value) {

		this.laserFrequency = value;
	}

	public BigInteger getLaserIntensity() {

		return laserIntensity;
	}

	public void setLaserIntensity(BigInteger value) {

		this.laserIntensity = value;
	}

	public Boolean isCollisionGas() {

		return collisionGas;
	}

	public void setCollisionGas(Boolean value) {

		this.collisionGas = value;
	}
}
