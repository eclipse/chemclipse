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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"software", "processingOperationAndComment"})
public class DataProcessing implements Serializable {

	private final static long serialVersionUID = 320L;
	@XmlElement(required = true)
	private Software software;
	@XmlElements({@XmlElement(name = "processingOperation", type = NameValue.class), @XmlElement(name = "comment", type = String.class)})
	private List<Serializable> processingOperationAndComment;
	@XmlAttribute(name = "intensityCutoff")
	private Float intensityCutoff;
	@XmlAttribute(name = "centroided")
	private Boolean centroided;
	@XmlAttribute(name = "deisotoped")
	private Boolean deisotoped;
	@XmlAttribute(name = "chargeDeconvoluted")
	private Boolean chargeDeconvoluted;
	@XmlAttribute(name = "spotIntegration")
	private Boolean spotIntegration;

	public Software getSoftware() {

		return software;
	}

	public void setSoftware(Software value) {

		this.software = value;
	}

	public List<Serializable> getProcessingOperationAndComment() {

		if(processingOperationAndComment == null) {
			processingOperationAndComment = new ArrayList<Serializable>();
		}
		return this.processingOperationAndComment;
	}

	public Float getIntensityCutoff() {

		return intensityCutoff;
	}

	public void setIntensityCutoff(Float value) {

		this.intensityCutoff = value;
	}

	public Boolean isCentroided() {

		return centroided;
	}

	public void setCentroided(Boolean value) {

		this.centroided = value;
	}

	public Boolean isDeisotoped() {

		return deisotoped;
	}

	public void setDeisotoped(Boolean value) {

		this.deisotoped = value;
	}

	public Boolean isChargeDeconvoluted() {

		return chargeDeconvoluted;
	}

	public void setChargeDeconvoluted(Boolean value) {

		this.chargeDeconvoluted = value;
	}

	public Boolean isSpotIntegration() {

		return spotIntegration;
	}

	public void setSpotIntegration(Boolean value) {

		this.spotIntegration = value;
	}
}
