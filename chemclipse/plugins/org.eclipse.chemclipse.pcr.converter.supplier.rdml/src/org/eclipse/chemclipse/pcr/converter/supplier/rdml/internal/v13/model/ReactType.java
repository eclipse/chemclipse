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
 * A reaction is an independent chemical reaction corresponding for example
 * to a well in a 96 well plate, a capillary in a rotor, a through-hole on
 * an array, etc.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reactType", propOrder = {"sample", "data", "partitions"})
public class ReactType {

	@XmlElement(required = true)
	protected IdReferencesType sample;
	protected List<DataType> data;
	protected PartitionsType partitions;
	@XmlAttribute(name = "id", required = true)
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger id;

	public IdReferencesType getSample() {

		return sample;
	}

	public void setSample(IdReferencesType value) {

		this.sample = value;
	}

	public List<DataType> getData() {

		if(data == null) {
			data = new ArrayList<>();
		}
		return this.data;
	}

	public PartitionsType getPartitions() {

		return partitions;
	}

	public void setPartitions(PartitionsType value) {

		this.partitions = value;
	}

	public BigInteger getId() {

		return id;
	}

	public void setId(BigInteger value) {

		this.id = value;
	}
}
