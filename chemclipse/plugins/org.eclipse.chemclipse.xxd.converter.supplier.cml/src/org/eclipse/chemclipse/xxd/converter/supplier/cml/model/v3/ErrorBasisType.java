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

@XmlType(name = "errorBasisType", namespace = "http://www.xml-cml.org/schema")
@XmlEnum
public enum ErrorBasisType {

	@XmlEnumValue("observedRange")
	OBSERVED_RANGE("observedRange"), //
	@XmlEnumValue("observedStandardDeviation")
	OBSERVED_STANDARD_DEVIATION("observedStandardDeviation"), //
	@XmlEnumValue("observedStandardError")
	OBSERVED_STANDARD_ERROR("observedStandardError"), //
	@XmlEnumValue("estimatedStandardDeviation")
	ESTIMATED_STANDARD_DEVIATION("estimatedStandardDeviation"), //
	@XmlEnumValue("estimatedStandardError")
	ESTIMATED_STANDARD_ERROR("estimatedStandardError"), //
	@XmlEnumValue("other")
	OTHER("other");

	private final String value;

	ErrorBasisType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static ErrorBasisType fromValue(String v) {

		for(ErrorBasisType c : ErrorBasisType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
