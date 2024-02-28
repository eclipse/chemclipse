/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class Array {

	@XmlValue
	protected String value;
	@XmlAttribute(name = "dictRef")
	protected String dictRef;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "delimiter")
	protected String delimiter;
	@XmlAttribute(name = "errorValueArray")
	protected List<Double> errorValueArray;
	@XmlAttribute(name = "units")
	protected String units;
	@XmlAttribute(name = "start")
	protected Double start;
	@XmlAttribute(name = "maxValueArray")
	protected List<Double> maxValueArray;
	@XmlAttribute(name = "minValueArray")
	protected List<Double> minValueArray;
	@XmlAttribute(name = "convention")
	protected String convention;
	@XmlAttribute(name = "size")
	protected BigInteger size;
	@XmlAttribute(name = "constantToSI")
	protected Double constantToSI;
	@XmlAttribute(name = "unitType")
	protected String unitType;
	@XmlAttribute(name = "title")
	protected String title;
	@XmlAttribute(name = "dataType")
	protected String dataType;
	@XmlAttribute(name = "ref")
	protected String ref;
	@XmlAttribute(name = "errorBasis")
	protected ErrorBasisType errorBasis;
	@XmlAttribute(name = "end")
	protected Double end;
	@XmlAttribute(name = "multiplierToSI")
	protected Double multiplierToSI;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<>();

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getDictRef() {

		return dictRef;
	}

	public void setDictRef(String value) {

		this.dictRef = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public String getDelimiter() {

		return delimiter;
	}

	public void setDelimiter(String value) {

		this.delimiter = value;
	}

	public List<Double> getErrorValueArray() {

		if(errorValueArray == null) {
			errorValueArray = new ArrayList<>();
		}
		return this.errorValueArray;
	}

	public String getUnits() {

		return units;
	}

	public void setUnits(String value) {

		this.units = value;
	}

	public Double getStart() {

		return start;
	}

	public void setStart(Double value) {

		this.start = value;
	}

	public List<Double> getMaxValueArray() {

		if(maxValueArray == null) {
			maxValueArray = new ArrayList<>();
		}
		return this.maxValueArray;
	}

	public List<Double> getMinValueArray() {

		if(minValueArray == null) {
			minValueArray = new ArrayList<>();
		}
		return this.minValueArray;
	}

	public String getConvention() {

		return convention;
	}

	public void setConvention(String value) {

		this.convention = value;
	}

	public BigInteger getSize() {

		return size;
	}

	public void setSize(BigInteger value) {

		this.size = value;
	}

	public Double getConstantToSI() {

		return constantToSI;
	}

	public void setConstantToSI(Double value) {

		this.constantToSI = value;
	}

	public String getUnitType() {

		return unitType;
	}

	public void setUnitType(String value) {

		this.unitType = value;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String value) {

		this.title = value;
	}

	public String getDataType() {

		return dataType;
	}

	public void setDataType(String value) {

		this.dataType = value;
	}

	public String getRef() {

		return ref;
	}

	public void setRef(String value) {

		this.ref = value;
	}

	public ErrorBasisType getErrorBasis() {

		return errorBasis;
	}

	public void setErrorBasis(ErrorBasisType value) {

		this.errorBasis = value;
	}

	public Double getEnd() {

		return end;
	}

	public void setEnd(Double value) {

		this.end = value;
	}

	public Double getMultiplierToSI() {

		return multiplierToSI;
	}

	public void setMultiplierToSI(Double value) {

		this.multiplierToSI = value;
	}

	public Map<QName, String> getOtherAttributes() {

		return otherAttributes;
	}
}
