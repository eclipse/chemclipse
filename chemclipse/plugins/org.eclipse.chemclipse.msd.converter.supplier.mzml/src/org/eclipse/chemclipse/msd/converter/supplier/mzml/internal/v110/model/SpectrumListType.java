/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
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
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpectrumListType", propOrder = {"spectrum"})
public class SpectrumListType {

	private List<SpectrumType> spectrum;
	@XmlAttribute(name = "count", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger count;
	@XmlAttribute(name = "defaultDataProcessingRef", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object defaultDataProcessingRef;

	public List<SpectrumType> getSpectrum() {

		if(spectrum == null) {
			spectrum = new ArrayList<>();
		}
		return this.spectrum;
	}

	public BigInteger getCount() {

		return count;
	}

	public void setCount(BigInteger value) {

		this.count = value;
	}

	public Object getDefaultDataProcessingRef() {

		return defaultDataProcessingRef;
	}

	public void setDefaultDataProcessingRef(Object value) {

		this.defaultDataProcessingRef = value;
	}
}
