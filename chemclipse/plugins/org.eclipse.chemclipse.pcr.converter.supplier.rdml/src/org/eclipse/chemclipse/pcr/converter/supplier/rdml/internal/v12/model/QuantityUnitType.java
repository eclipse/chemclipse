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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "quantityUnitType")
@XmlEnum
public enum QuantityUnitType {

	@XmlEnumValue("cop")
	COP("cop"), //
	@XmlEnumValue("fold")
	FOLD("fold"), //
	@XmlEnumValue("dil")
	DIL("dil"), //
	@XmlEnumValue("ng")
	NG("ng"), //
	@XmlEnumValue("nMol")
	N_MOL("nMol"), //
	@XmlEnumValue("other")
	OTHER("other");

	private final String value;

	QuantityUnitType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static QuantityUnitType fromValue(String v) {

		for(QuantityUnitType c : QuantityUnitType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
