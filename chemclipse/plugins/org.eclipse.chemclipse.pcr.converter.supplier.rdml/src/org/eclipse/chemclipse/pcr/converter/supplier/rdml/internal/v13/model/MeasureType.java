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

@XmlType(name = "measureType")
@XmlEnum
public enum MeasureType {

	@XmlEnumValue("real time")
	REAL_TIME("real time"), //
	@XmlEnumValue("meltcurve")
	MELTCURVE("meltcurve");

	private final String value;

	MeasureType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static MeasureType fromValue(String v) {

		for(MeasureType c : MeasureType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
