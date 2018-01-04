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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChromatogramListType", propOrder = {"chromatogram"})
public class ChromatogramListType {

	@XmlElement(required = true)
	private List<ChromatogramType> chromatogram;
	@XmlAttribute(name = "count", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger count;
	@XmlAttribute(name = "defaultDataProcessingRef", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object defaultDataProcessingRef;

	public List<ChromatogramType> getChromatogram() {

		if(chromatogram == null) {
			chromatogram = new ArrayList<ChromatogramType>();
		}
		return this.chromatogram;
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
