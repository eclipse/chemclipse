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
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class Peaks implements Serializable {

	private final static long serialVersionUID = 300L;
	@XmlValue
	private byte[] value;
	@XmlAttribute(name = "precision")
	private BigInteger precision;
	@XmlAttribute(name = "byteOrder", required = true)
	private String byteOrder;
	@XmlAttribute(name = "contentType", required = true)
	private String contentType;
	@XmlAttribute(name = "compressionType", required = true)
	private String compressionType;
	@XmlAttribute(name = "compressedLen", required = true)
	private int compressedLen;

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

	public String getContentType() {

		return contentType;
	}

	public void setContentType(String value) {

		this.contentType = value;
	}

	public String getCompressionType() {

		return compressionType;
	}

	public void setCompressionType(String value) {

		this.compressionType = value;
	}

	public int getCompressedLen() {

		return compressedLen;
	}

	public void setCompressedLen(int value) {

		this.compressedLen = value;
	}
}
