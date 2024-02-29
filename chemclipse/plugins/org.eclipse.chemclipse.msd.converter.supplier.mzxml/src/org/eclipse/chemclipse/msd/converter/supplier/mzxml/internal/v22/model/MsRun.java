/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v22.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"parentFile", "msInstrument", "dataProcessing", "separation", "spotting", "scan", "sha1"})
@XmlRootElement(name = "msRun")
public class MsRun implements Serializable {

	private static final long serialVersionUID = 220L;
	@XmlElement(required = true)
	private List<ParentFile> parentFile;
	private MsInstrument msInstrument;
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
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar startTime;
	@XmlAttribute(name = "endTime")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar endTime;

	public List<ParentFile> getParentFile() {

		if(parentFile == null) {
			parentFile = new ArrayList<ParentFile>();
		}
		return this.parentFile;
	}

	public MsInstrument getMsInstrument() {

		return msInstrument;
	}

	public void setMsInstrument(MsInstrument value) {

		this.msInstrument = value;
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

	public XMLGregorianCalendar getStartTime() {

		return startTime;
	}

	public void setStartTime(XMLGregorianCalendar value) {

		this.startTime = value;
	}

	public XMLGregorianCalendar getEndTime() {

		return endTime;
	}

	public void setEndTime(XMLGregorianCalendar value) {

		this.endTime = value;
	}
}
