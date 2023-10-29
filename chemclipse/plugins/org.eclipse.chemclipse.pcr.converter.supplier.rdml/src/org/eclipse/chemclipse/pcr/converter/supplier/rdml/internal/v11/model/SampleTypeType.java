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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v11.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "sampleTypeType")
@XmlEnum
public enum SampleTypeType {

	@XmlEnumValue("unkn")
	UNKN("unkn"), //
	@XmlEnumValue("ntc")
	NTC("ntc"), //
	@XmlEnumValue("nac")
	NAC("nac"), //
	@XmlEnumValue("std")
	STD("std"), //
	@XmlEnumValue("ntp")
	NTP("ntp"), //
	@XmlEnumValue("nrt")
	NRT("nrt"), //
	@XmlEnumValue("pos")
	POS("pos"), //
	@XmlEnumValue("opt")
	OPT("opt");

	private final String value;

	SampleTypeType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static SampleTypeType fromValue(String v) {

		for(SampleTypeType c : SampleTypeType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
