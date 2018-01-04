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
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "instrumentDescriptionType", propOrder = {"instrumentName", "source", "analyzerList", "detector", "additional"})
public class InstrumentDescriptionType {

	@XmlElement(required = true)
	private String instrumentName;
	@XmlElement(required = true)
	private ParamType source;
	@XmlElement(required = true)
	private InstrumentDescriptionType.AnalyzerList analyzerList;
	@XmlElement(required = true)
	private ParamType detector;
	private ParamType additional;

	public String getInstrumentName() {

		return instrumentName;
	}

	public void setInstrumentName(String value) {

		this.instrumentName = value;
	}

	public ParamType getSource() {

		return source;
	}

	public void setSource(ParamType value) {

		this.source = value;
	}

	public InstrumentDescriptionType.AnalyzerList getAnalyzerList() {

		return analyzerList;
	}

	public void setAnalyzerList(InstrumentDescriptionType.AnalyzerList value) {

		this.analyzerList = value;
	}

	public ParamType getDetector() {

		return detector;
	}

	public void setDetector(ParamType value) {

		this.detector = value;
	}

	public ParamType getAdditional() {

		return additional;
	}

	public void setAdditional(ParamType value) {

		this.additional = value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"analyzer"})
	public static class AnalyzerList {

		@XmlElement(required = true)
		private List<ParamType> analyzer;
		@XmlAttribute(name = "count", required = true)
		private int count;

		public List<ParamType> getAnalyzer() {

			if(analyzer == null) {
				analyzer = new ArrayList<ParamType>();
			}
			return this.analyzer;
		}

		public int getCount() {

			return count;
		}

		public void setCount(int value) {

			this.count = value;
		}
	}
}
