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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * The same sample could have the sampleTypeType pos for one target and ntp
 * for a different target. Therefor several entries are allowed and can be
 * linked to a target. If no target is given, only one entry should be
 * present valid for all targets in this run.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sampleTargetType", propOrder = {"value"})
public class SampleTargetType {

	@XmlValue
	protected SampleTypeType value;
	@XmlAttribute(name = "targetId")
	protected String targetId;

	/**
	 * unkn - unknown sample
	 * ntc - non template control
	 * nac - no amplification control
	 * std - standard sample
	 * ntp - no target present
	 * nrt - minusRT
	 * pos - positive control
	 * opt - optical calibrator sample
	 */
	public SampleTypeType getValue() {

		return value;
	}

	public void setValue(SampleTypeType value) {

		this.value = value;
	}

	public String getTargetId() {

		return targetId;
	}

	public void setTargetId(String value) {

		this.targetId = value;
	}
}
