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
@XmlType(name = "ComponentListType", propOrder = {"source", "analyzer", "detector"})
public class ComponentListType {

	@XmlElement(required = true)
	private List<SourceComponentType> source;
	@XmlElement(required = true)
	private List<AnalyzerComponentType> analyzer;
	@XmlElement(required = true)
	private List<DetectorComponentType> detector;
	@XmlAttribute(name = "count", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger count;

	public List<SourceComponentType> getSource() {

		if(source == null) {
			source = new ArrayList<SourceComponentType>();
		}
		return this.source;
	}

	public List<AnalyzerComponentType> getAnalyzer() {

		if(analyzer == null) {
			analyzer = new ArrayList<AnalyzerComponentType>();
		}
		return this.analyzer;
	}

	public List<DetectorComponentType> getDetector() {

		if(detector == null) {
			detector = new ArrayList<DetectorComponentType>();
		}
		return this.detector;
	}

	public BigInteger getCount() {

		return count;
	}

	public void setCount(BigInteger value) {

		this.count = value;
	}
}
