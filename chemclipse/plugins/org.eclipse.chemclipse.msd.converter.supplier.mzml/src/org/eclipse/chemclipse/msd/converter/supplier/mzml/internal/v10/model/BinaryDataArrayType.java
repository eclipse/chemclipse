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

import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * The structure into which encoded binary data goes. Byte ordering is always little endian (Intel style). Computers using a different endian style must convert to/from little endian when writing/reading mzML
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryDataArrayType", propOrder = {"binary"})
public class BinaryDataArrayType extends ParamGroupType {

	@XmlElement(required = true)
	protected byte[] binary;
	@XmlAttribute(name = "arrayLength")
	@XmlSchemaType(name = "nonNegativeInteger")
	protected BigInteger arrayLength;
	@XmlAttribute(name = "dataProcessingRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object dataProcessingRef;
	@XmlAttribute(name = "encodedLength", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	protected BigInteger encodedLength;

	public byte[] getBinary() {

		return binary;
	}

	public void setBinary(byte[] value) {

		this.binary = value;
	}

	public BigInteger getArrayLength() {

		return arrayLength;
	}

	public void setArrayLength(BigInteger value) {

		this.arrayLength = value;
	}

	public Object getDataProcessingRef() {

		return dataProcessingRef;
	}

	public void setDataProcessingRef(Object value) {

		this.dataProcessingRef = value;
	}

	public BigInteger getEncodedLength() {

		return encodedLength;
	}

	public void setEncodedLength(BigInteger value) {

		this.encodedLength = value;
	}
}
