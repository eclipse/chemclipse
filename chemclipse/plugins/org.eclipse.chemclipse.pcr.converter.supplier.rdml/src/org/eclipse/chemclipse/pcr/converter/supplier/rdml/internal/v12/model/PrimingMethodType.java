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

@XmlType(name = "primingMethodType")
@XmlEnum
public enum PrimingMethodType {

	@XmlEnumValue("oligo-dt")
	OLIGO_DT("oligo-dt"), //
	@XmlEnumValue("random")
	RANDOM("random"), //
	@XmlEnumValue("target-specific")
	TARGET_SPECIFIC("target-specific"), //
	@XmlEnumValue("oligo-dt and random")
	OLIGO_DT_AND_RANDOM("oligo-dt and random"), //
	@XmlEnumValue("other")
	OTHER("other");

	private final String value;

	PrimingMethodType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static PrimingMethodType fromValue(String v) {

		for(PrimingMethodType c : PrimingMethodType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
