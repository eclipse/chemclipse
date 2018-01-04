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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v31.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"parentFile", "msInstrument", "dataProcessing", "separation", "spotting", "scan", "sha1"})
@XmlRootElement(name = "msRun")
public class MsRun implements Serializable {

	private final static long serialVersionUID = 310L;
	@XmlElement(required = true)
	private List<ParentFile> parentFile;
	private List<MsInstrument> msInstrument;
	@XmlElement(required = true)
	private List<DataProcessing> dataProcessing;
	private Separation separation;
	private Spotting spotting;
	@XmlElement(required = true)
	private List<Scan> scan;
	private String sha1;
	@XmlAttribute(name = "scanCount")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger scanCount;
	@XmlAttribute(name = "startTime")
	private Duration startTime;
	@XmlAttribute(name = "endTime")
	private Duration endTime;

	public List<ParentFile> getParentFile() {

		if(parentFile == null) {
			parentFile = new ArrayList<ParentFile>();
		}
		return this.parentFile;
	}

	public List<MsInstrument> getMsInstrument() {

		if(msInstrument == null) {
			msInstrument = new ArrayList<MsInstrument>();
		}
		return this.msInstrument;
	}

	public List<DataProcessing> getDataProcessing() {

		if(dataProcessing == null) {
			dataProcessing = new ArrayList<DataProcessing>();
		}
		return this.dataProcessing;
	}

	public Separation getSeparation() {

		return separation;
	}

	public void setSeparation(Separation value) {

		this.separation = value;
	}

	public Spotting getSpotting() {

		return spotting;
	}

	public void setSpotting(Spotting value) {

		this.spotting = value;
	}

	public List<Scan> getScan() {

		if(scan == null) {
			scan = new ArrayList<Scan>();
		}
		return this.scan;
	}

	public String getSha1() {

		return sha1;
	}

	public void setSha1(String value) {

		this.sha1 = value;
	}

	public BigInteger getScanCount() {

		return scanCount;
	}

	public void setScanCount(BigInteger value) {

		this.scanCount = value;
	}

	public Duration getStartTime() {

		return startTime;
	}

	public void setStartTime(Duration value) {

		this.startTime = value;
	}

	public Duration getEndTime() {

		return endTime;
	}

	public void setEndTime(Duration value) {

		this.endTime = value;
	}
}
