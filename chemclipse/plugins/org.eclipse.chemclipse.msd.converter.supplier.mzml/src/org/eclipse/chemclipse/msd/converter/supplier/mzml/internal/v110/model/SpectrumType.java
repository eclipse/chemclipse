/*******************************************************************************
 * Copyright (c) 2015, 2021 Lablicate GmbH.
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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpectrumType", propOrder = {"scanList", "precursorList", "productList", "binaryDataArrayList"})
public class SpectrumType extends ParamGroupType {

	private ScanListType scanList;
	private PrecursorListType precursorList;
	private ProductListType productList;
	private BinaryDataArrayListType binaryDataArrayList;
	@XmlAttribute(name = "id", required = true)
	private String id;
	@XmlAttribute(name = "spotID")
	private String spotID;
	@XmlAttribute(name = "index", required = true)
	@XmlSchemaType(name = "nonNegativeInteger") // zero based
	private BigInteger index;
	@XmlAttribute(name = "defaultArrayLength", required = true)
	private int defaultArrayLength;
	@XmlAttribute(name = "dataProcessingRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object dataProcessingRef;
	@XmlAttribute(name = "sourceFileRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object sourceFileRef;

	public ScanListType getScanList() {

		return scanList;
	}

	public void setScanList(ScanListType value) {

		this.scanList = value;
	}

	public PrecursorListType getPrecursorList() {

		return precursorList;
	}

	public void setPrecursorList(PrecursorListType value) {

		this.precursorList = value;
	}

	public ProductListType getProductList() {

		return productList;
	}

	public void setProductList(ProductListType value) {

		this.productList = value;
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

	public String getSpotID() {

		return spotID;
	}

	public void setSpotID(String value) {

		this.spotID = value;
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

	public Object getSourceFileRef() {

		return sourceFileRef;
	}

	public void setSourceFileRef(Object value) {

		this.sourceFileRef = value;
	}
}
