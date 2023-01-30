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
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v10.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * List with the descriptions of the acquisition settings applied prior to the start of data acquisition.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcquisitionSettingsListType", propOrder = {"acquisitionSettings"})
public class AcquisitionSettingsListType {

	@XmlElement(required = true)
	protected List<AcquisitionSettingsType> acquisitionSettings;
	@XmlAttribute(name = "count", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	protected BigInteger count;

	public List<AcquisitionSettingsType> getAcquisitionSettings() {

		if(acquisitionSettings == null) {
			acquisitionSettings = new ArrayList<AcquisitionSettingsType>();
		}
		return this.acquisitionSettings;
	}

	public BigInteger getCount() {

		return count;
	}

	public void setCount(BigInteger value) {

		this.count = value;
	}
}
