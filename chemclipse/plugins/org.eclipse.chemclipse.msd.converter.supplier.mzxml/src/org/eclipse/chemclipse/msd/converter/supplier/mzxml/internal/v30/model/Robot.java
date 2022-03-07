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
import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"robotManufacturer", "robotModel"})
public class Robot implements Serializable {

	private final static long serialVersionUID = 300L;
	@XmlElement(required = true)
	private OntologyEntry robotManufacturer;
	@XmlElement(required = true)
	private OntologyEntry robotModel;
	@XmlAttribute(name = "timePerSpot", required = true)
	private Duration timePerSpot;
	@XmlAttribute(name = "deadVolume")
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger deadVolume;

	public OntologyEntry getRobotManufacturer() {

		return robotManufacturer;
	}

	public void setRobotManufacturer(OntologyEntry value) {

		this.robotManufacturer = value;
	}

	public OntologyEntry getRobotModel() {

		return robotModel;
	}

	public void setRobotModel(OntologyEntry value) {

		this.robotModel = value;
	}

	public Duration getTimePerSpot() {

		return timePerSpot;
	}

	public void setTimePerSpot(Duration value) {

		this.timePerSpot = value;
	}

	public BigInteger getDeadVolume() {

		return deadVolume;
	}

	public void setDeadVolume(BigInteger value) {

		this.deadVolume = value;
	}
}
