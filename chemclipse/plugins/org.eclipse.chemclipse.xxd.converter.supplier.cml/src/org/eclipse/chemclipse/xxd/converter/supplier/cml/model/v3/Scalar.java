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

import java.util.HashMap;
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
public class Scalar {

	@XmlValue
	protected String value;
	@XmlAttribute(name = "dataType")
	protected String dataType;
	@XmlAttribute(name = "dictRef")
	protected String dictRef;
	@XmlAttribute(name = "min")
	protected String min;
	@XmlAttribute(name = "errorValue")
	protected Double errorValue;
	@XmlAttribute(name = "ref")
	protected String ref;
	@XmlAttribute(name = "title")
	protected String title;
	@XmlAttribute(name = "convention")
	protected String convention;
	@XmlAttribute(name = "max")
	protected String max;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "unitType")
	protected String unitType;
	@XmlAttribute(name = "units")
	protected String units;
	@XmlAttribute(name = "constantToSI")
	protected Double constantToSI;
	@XmlAttribute(name = "errorBasis")
	protected ErrorBasisType errorBasis;
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

	public String getDataType() {

		return dataType;
	}

	public void setDataType(String value) {

		this.dataType = value;
	}

	public String getDictRef() {

		return dictRef;
	}

	public void setDictRef(String value) {

		this.dictRef = value;
	}

	public String getMin() {

		return min;
	}

	public void setMin(String value) {

		this.min = value;
	}

	public Double getErrorValue() {

		return errorValue;
	}

	public void setErrorValue(Double value) {

		this.errorValue = value;
	}

	public String getRef() {

		return ref;
	}

	public void setRef(String value) {

		this.ref = value;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String value) {

		this.title = value;
	}

	public String getConvention() {

		return convention;
	}

	public void setConvention(String value) {

		this.convention = value;
	}

	public String getMax() {

		return max;
	}

	public void setMax(String value) {

		this.max = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public String getUnitType() {

		return unitType;
	}

	public void setUnitType(String value) {

		this.unitType = value;
	}

	public String getUnits() {

		return units;
	}

	public void setUnits(String value) {

		this.units = value;
	}

	public Double getConstantToSI() {

		return constantToSI;
	}

	public void setConstantToSI(Double value) {

		this.constantToSI = value;
	}

	public ErrorBasisType getErrorBasis() {

		return errorBasis;
	}

	public void setErrorBasis(ErrorBasisType value) {

		this.errorBasis = value;
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
