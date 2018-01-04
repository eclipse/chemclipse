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
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChromatogramType", propOrder = {"precursor", "product", "binaryDataArrayList"})
public class ChromatogramType extends ParamGroupType {

	private PrecursorType precursor;
	private ProductType product;
	@XmlElement(required = true)
	private BinaryDataArrayListType binaryDataArrayList;
	@XmlAttribute(name = "id", required = true)
	private String id;
	@XmlAttribute(name = "index", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger index;
	@XmlAttribute(name = "defaultArrayLength", required = true)
	private int defaultArrayLength;
	@XmlAttribute(name = "dataProcessingRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object dataProcessingRef;

	public PrecursorType getPrecursor() {

		return precursor;
	}

	public void setPrecursor(PrecursorType value) {

		this.precursor = value;
	}

	public ProductType getProduct() {

		return product;
	}

	public void setProduct(ProductType value) {

		this.product = value;
	}

	public BinaryDataArrayListType getBinaryDataArrayList() {

		return binaryDataArrayList;
	}

	public void setBinaryDataArrayList(BinaryDataArrayListType value) {

		this.binaryDataArrayList = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public BigInteger getIndex() {

		return index;
	}

	public void setIndex(BigInteger value) {

		this.index = value;
	}

	public int getDefaultArrayLength() {

		return defaultArrayLength;
	}

	public void setDefaultArrayLength(int value) {

		this.defaultArrayLength = value;
	}

	public Object getDataProcessingRef() {

		return dataProcessingRef;
	}

	public void setDataProcessingRef(Object value) {

		this.dataProcessingRef = value;
	}
}
