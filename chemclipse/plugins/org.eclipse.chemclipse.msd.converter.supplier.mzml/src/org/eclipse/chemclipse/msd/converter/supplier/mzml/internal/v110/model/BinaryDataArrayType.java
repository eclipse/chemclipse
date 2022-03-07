/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
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

import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryDataArrayType", propOrder = {"binary"})
public class BinaryDataArrayType extends ParamGroupType {

	@XmlElement(required = true)
	private byte[] binary;
	@XmlAttribute(name = "arrayLength")
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger arrayLength; // optional, may override defaultArrayLength, not to be used for m/z, intensity and time
	@XmlAttribute(name = "dataProcessingRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object dataProcessingRef;
	@XmlAttribute(name = "encodedLength", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger encodedLength;

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
