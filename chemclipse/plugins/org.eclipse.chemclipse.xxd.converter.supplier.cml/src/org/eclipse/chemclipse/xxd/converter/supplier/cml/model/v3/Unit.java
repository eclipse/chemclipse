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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Unit {

	@XmlAttribute(name = "constantToSI")
	protected Double constantToSI;
	@XmlAttribute(name = "isSI")
	protected Boolean isSI;
	@XmlAttribute(name = "unitType")
	protected String unitType;
	@XmlAttribute(name = "title")
	protected String title;
	@XmlAttribute(name = "power")
	protected Double power;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "multiplierToSI")
	protected Double multiplierToSI;
	@XmlAttribute(name = "parentSI")
	protected String parentSI;
	@XmlAttribute(name = "symbol")
	protected String symbol;
	@XmlAttribute(name = "units")
	protected String units;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "multiplierToData")
	protected Double multiplierToData;

	public Double getConstantToSI() {

		return constantToSI;
	}

	public void setConstantToSI(Double value) {

		this.constantToSI = value;
	}

	public Boolean isIsSI() {

		return isSI;
	}

	public void setIsSI(Boolean value) {

		this.isSI = value;
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

	public Double getPower() {

		return power;
	}

	public void setPower(Double value) {

		this.power = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public Double getMultiplierToSI() {

		return multiplierToSI;
	}

	public void setMultiplierToSI(Double value) {

		this.multiplierToSI = value;
	}

	public String getParentSI() {

		return parentSI;
	}

	public void setParentSI(String value) {

		this.parentSI = value;
	}

	public String getSymbol() {

		return symbol;
	}

	public void setSymbol(String value) {

		this.symbol = value;
	}

	public String getUnits() {

		return units;
	}

	public void setUnits(String value) {

		this.units = value;
	}

	public String getName() {

		return name;
	}

	public void setName(String value) {

		this.name = value;
	}

	public double getMultiplierToData() {

		if(multiplierToData == null) {
			return 1.0D;
		} else {
			return multiplierToData;
		}
	}

	public void setMultiplierToData(Double value) {

		this.multiplierToData = value;
	}
}
