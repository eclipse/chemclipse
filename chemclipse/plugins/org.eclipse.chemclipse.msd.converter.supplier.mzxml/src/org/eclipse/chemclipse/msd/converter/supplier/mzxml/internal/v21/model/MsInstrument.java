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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v21.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"msManufacturer", "msModel", "msIonisation", "msMassAnalyzer", "msDetector", "software", "msResolution", "operator", "nameValueAndComment"})
public class MsInstrument implements Serializable {

	private final static long serialVersionUID = 210L;
	@XmlElement(required = true)
	private MsManufacturer msManufacturer;
	@XmlElement(required = true)
	private OntologyEntry msModel;
	@XmlElement(required = true)
	private OntologyEntry msIonisation;
	@XmlElement(required = true)
	private MsMassAnalyzer msMassAnalyzer;
	@XmlElement(required = true)
	private OntologyEntry msDetector;
	@XmlElement(required = true)
	private Software software;
	private OntologyEntry msResolution;
	private Operator operator;
	@XmlElements({@XmlElement(name = "nameValue", type = NameValue.class), @XmlElement(name = "comment", type = String.class)})
	private List<Serializable> nameValueAndComment;

	public MsManufacturer getMsManufacturer() {

		return msManufacturer;
	}

	public void setMsManufacturer(MsManufacturer value) {

		this.msManufacturer = value;
	}

	public OntologyEntry getMsModel() {

		return msModel;
	}

	public void setMsModel(OntologyEntry value) {

		this.msModel = value;
	}

	public OntologyEntry getMsIonisation() {

		return msIonisation;
	}

	public void setMsIonisation(OntologyEntry value) {

		this.msIonisation = value;
	}

	public MsMassAnalyzer getMsMassAnalyzer() {

		return msMassAnalyzer;
	}

	public void setMsMassAnalyzer(MsMassAnalyzer value) {

		this.msMassAnalyzer = value;
	}

	public OntologyEntry getMsDetector() {

		return msDetector;
	}

	public void setMsDetector(OntologyEntry value) {

		this.msDetector = value;
	}

	public Software getSoftware() {

		return software;
	}

	public void setSoftware(Software value) {

		this.software = value;
	}

	public OntologyEntry getMsResolution() {

		return msResolution;
	}

	public void setMsResolution(OntologyEntry value) {

		this.msResolution = value;
	}

	public Operator getOperator() {

		return operator;
	}

	public void setOperator(Operator value) {

		this.operator = value;
	}

	public List<Serializable> getNameValueAndComment() {

		if(nameValueAndComment == null) {
			nameValueAndComment = new ArrayList<Serializable>();
		}
		return this.nameValueAndComment;
	}
}
