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

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "peakStructureTypeType", namespace = "http://www.xml-cml.org/schema")
@XmlEnum
public enum PeakStructureType {

	@XmlEnumValue("coupling")
	COUPLING("coupling"), //
	@XmlEnumValue("splitting")
	SPLITTING("splitting"), //
	@XmlEnumValue("other")
	OTHER("other");

	private final String value;

	PeakStructureType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static PeakStructureType fromValue(String v) {

		for(PeakStructureType c : PeakStructureType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
