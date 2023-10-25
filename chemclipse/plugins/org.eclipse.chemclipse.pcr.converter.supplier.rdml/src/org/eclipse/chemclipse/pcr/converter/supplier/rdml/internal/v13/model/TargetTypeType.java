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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "targetTypeType")
@XmlEnum
public enum TargetTypeType {

	@XmlEnumValue("ref")
	REF("ref"), //
	@XmlEnumValue("toi")
	TOI("toi");

	private final String value;

	TargetTypeType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static TargetTypeType fromValue(String v) {

		for(TargetTypeType c : TargetTypeType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
