/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurementParameter {

	@XmlElement(name = "measurementType")
	private String measurementType;
	@XmlElement(name = "scanMode")
	private String scanMode;
	@XmlElement(name = "referenceSample")
	private ReferenceSample referenceSample;
	@XmlElement(name = "filter")
	private String filter;
	@XmlElement(name = "signalNoise")
	private String signalNoise;
	@XmlElement(name = "scanNumbers")
	private long scanNumbers;
	@XmlElement(name = "scanDuration")
	private ScanDuration scanDuration;
	@XmlElement(name = "comment")
	private String comment;

	public String getMeasurementType() {

		return measurementType;
	}

	public void setMeasurementType(String measurementType) {

		this.measurementType = measurementType;
	}

	public String getScanMode() {

		return scanMode;
	}

	public void setScanMode(String scanMode) {

		this.scanMode = scanMode;
	}

	public ReferenceSample getReferenceSample() {

		return referenceSample;
	}

	public void setReferenceSample(ReferenceSample referenceSample) {

		this.referenceSample = referenceSample;
	}

	public String getFilter() {

		return filter;
	}

	public void setFilter(String filter) {

		this.filter = filter;
	}

	public String getSignalNoise() {

		return signalNoise;
	}

	public void setSignalNoise(String signalNoise) {

		this.signalNoise = signalNoise;
	}

	public long getScanNumbers() {

		return scanNumbers;
	}

	public void setScanNumbers(long scanNumbers) {

		this.scanNumbers = scanNumbers;
	}

	public ScanDuration getScanDuration() {

		return scanDuration;
	}

	public void setScanDuration(ScanDuration scanDuration) {

		this.scanDuration = scanDuration;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}
