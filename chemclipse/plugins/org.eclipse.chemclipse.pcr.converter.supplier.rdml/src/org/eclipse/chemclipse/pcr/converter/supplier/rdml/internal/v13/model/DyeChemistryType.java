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

@XmlType(name = "dyeChemistryType")
@XmlEnum
public enum DyeChemistryType {

	@XmlEnumValue("non-saturating DNA binding dye")
	NON_SATURATING_DNA_BINDING_DYE("non-saturating DNA binding dye"), //
	@XmlEnumValue("saturating DNA binding dye")
	SATURATING_DNA_BINDING_DYE("saturating DNA binding dye"), //
	@XmlEnumValue("hybridization probe")
	HYBRIDIZATION_PROBE("hybridization probe"), //
	@XmlEnumValue("hydrolysis probe")
	HYDROLYSIS_PROBE("hydrolysis probe"), //
	@XmlEnumValue("labelled forward primer")
	LABELLED_FORWARD_PRIMER("labelled forward primer"), //
	@XmlEnumValue("labelled reverse primer")
	LABELLED_REVERSE_PRIMER("labelled reverse primer"), //
	@XmlEnumValue("DNA-zyme probe")
	DNA_ZYME_PROBE("DNA-zyme probe");

	private final String value;

	DyeChemistryType(String v) {

		value = v;
	}

	public String value() {

		return value;
	}

	public static DyeChemistryType fromValue(String v) {

		for(DyeChemistryType c : DyeChemistryType.values()) {
			if(c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
