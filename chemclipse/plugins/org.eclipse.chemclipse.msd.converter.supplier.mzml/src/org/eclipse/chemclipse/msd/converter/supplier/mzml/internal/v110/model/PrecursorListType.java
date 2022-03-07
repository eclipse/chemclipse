/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrecursorListType", propOrder = {"precursor"})
public class PrecursorListType {

	@XmlElement(required = true)
	private List<PrecursorType> precursor;
	@XmlAttribute(name = "count", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger count;

	public List<PrecursorType> getPrecursor() {

		if(precursor == null) {
			precursor = new ArrayList<PrecursorType>();
		}
		return this.precursor;
	}

	public BigInteger getCount() {

		return count;
	}

	public void setCount(BigInteger value) {

		this.count = value;
	}
}
