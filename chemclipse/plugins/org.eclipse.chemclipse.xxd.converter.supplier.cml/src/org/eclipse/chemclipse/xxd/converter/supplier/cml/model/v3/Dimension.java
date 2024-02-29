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

import javax.xml.namespace.QName;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"anyCmlOrAnyOrAny"})
public class Dimension {

	@XmlElementRef(name = "anyCml", namespace = "http://www.xml-cml.org/schema", type = JAXBElement.class, required = false)
	@XmlAnyElement(lax = true)
	protected java.util.List<java.lang.Object> anyCmlOrAnyOrAny;
	@XmlAttribute(name = "unitType")
	protected String unitType;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "dimensionBasis")
	protected DimensionType dimensionBasis;
	@XmlAttribute(name = "power")
	protected Double power;
	@XmlAttribute(name = "preserve")
	protected Boolean preserve;
	@XmlAnyAttribute
	private java.util.Map<QName, String> otherAttributes = new HashMap<>();

	public java.util.List<java.lang.Object> getAnyCmlOrAnyOrAny() {

		if(anyCmlOrAnyOrAny == null) {
			anyCmlOrAnyOrAny = new java.util.ArrayList<>();
		}
		return this.anyCmlOrAnyOrAny;
	}

	public String getUnitType() {

		return unitType;
	}

	public void setUnitType(String value) {

		this.unitType = value;
	}

	public String getName() {

		return name;
	}

	public void setName(String value) {

		this.name = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public DimensionType getDimensionBasis() {

		return dimensionBasis;
	}

	public void setDimensionBasis(DimensionType value) {

		this.dimensionBasis = value;
	}

	public Double getPower() {

		return power;
	}

	public void setPower(Double value) {

		this.power = value;
	}

	public Boolean isPreserve() {

		return preserve;
	}

	public void setPreserve(Boolean value) {

		this.preserve = value;
	}

	public java.util.Map<QName, String> getOtherAttributes() {

		return otherAttributes;
	}
}
