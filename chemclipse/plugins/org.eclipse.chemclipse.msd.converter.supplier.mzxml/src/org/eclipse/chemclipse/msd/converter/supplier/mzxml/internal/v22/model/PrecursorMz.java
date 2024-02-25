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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class PrecursorMz implements Serializable {

	private static final long serialVersionUID = 220L;
	@XmlValue
	private float value;
	@XmlAttribute(name = "precursorScanNum")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger precursorScanNum;
	@XmlAttribute(name = "precursorIntensity", required = true)
	private float precursorIntensity;
	@XmlAttribute(name = "precursorCharge")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger precursorCharge;
	@XmlAttribute(name = "windowWideness")
	private Float windowWideness;

	public float getValue() {

		return value;
	}

	public void setValue(float value) {

		this.value = value;
	}

	public BigInteger getPrecursorScanNum() {

		return precursorScanNum;
	}

	public void setPrecursorScanNum(BigInteger value) {

		this.precursorScanNum = value;
	}

	public float getPrecursorIntensity() {

		return precursorIntensity;
	}

	public void setPrecursorIntensity(float value) {

		this.precursorIntensity = value;
	}

	public BigInteger getPrecursorCharge() {

		return precursorCharge;
	}

	public void setPrecursorCharge(BigInteger value) {

		this.precursorCharge = value;
	}

	public Float getWindowWideness() {

		return windowWideness;
	}

	public void setWindowWideness(Float value) {

		this.windowWideness = value;
	}
}
