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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v20.model;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class Peaks implements Serializable {

	private final static long serialVersionUID = 200L;
	@XmlValue
	private byte[] value;
	@XmlAttribute(name = "precision", required = true)
	private BigInteger precision;
	@XmlAttribute(name = "byteOrder", required = true)
	private String byteOrder;
	@XmlAttribute(name = "pairOrder", required = true)
	private String pairOrder;

	public byte[] getValue() {

		return value;
	}

	public void setValue(byte[] value) {

		this.value = value;
	}

	public BigInteger getPrecision() {

		return precision;
	}

	public void setPrecision(BigInteger value) {

		this.precision = value;
	}

	public String getByteOrder() {

		if(byteOrder == null) {
			return "network";
		} else {
			return byteOrder;
		}
	}

	public void setByteOrder(String value) {

		this.byteOrder = value;
	}

	public String getPairOrder() {

		if(pairOrder == null) {
			return "m/z-int";
		} else {
			return pairOrder;
		}
	}

	public void setPairOrder(String value) {

		this.pairOrder = value;
	}
}
