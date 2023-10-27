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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v10.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "cqDetectionMethodType")
@XmlEnum
public enum CqDetectionMethodType {

	@XmlEnumValue("automated threshold and baseline settings")
	AUTOMATED_THRESHOLD_AND_BASELINE_SETTINGS("automated threshold and baseline settings"), //
	@XmlEnumValue("manual threshold and baseline settings")
	MANUAL_THRESHOLD_AND_BASELINE_SETTINGS("manual threshold and baseline settings"), //
	@XmlEnumValue("second derivative maximum")
	SECOND_DERIVATIVE_MAXIMUM("second derivative maximum"), //
	@XmlEnumValue("other")
	OTHER("other");

	private final String value;

	CqDetectionMethodType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static CqDetectionMethodType fromValue(String v) {

		for(CqDetectionMethodType c : CqDetectionMethodType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
