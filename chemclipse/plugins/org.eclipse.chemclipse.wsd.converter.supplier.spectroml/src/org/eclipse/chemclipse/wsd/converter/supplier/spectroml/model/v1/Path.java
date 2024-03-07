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
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Path {

	@XmlAttribute(name = "pathId")
	private String pathId;
	@XmlAttribute(name = "instrumentDescriptionLink")
	private String instrumentDescriptionLink;
	@XmlAttribute(name = "instrumentPropertyLink")
	private String instrumentPropertyLink;
	@XmlAttribute(name = "sampleDescriptionLink")
	private String sampleDescriptionLink;
	@XmlAttribute(name = "samplePropertyLink")
	private String samplePropertyLink;
	@XmlAttribute(name = "measurementDescriptionLink")
	private String measurementDescriptionLink;
	@XmlAttribute(name = "measurementPropertyLink")
	private String measurementPropertyLink;
	@XmlAttribute(name = "dataPropertyLink")
	private String dataPropertyLink;
	@XmlAttribute(name = "dataCoreLink")
	private String dataCoreLink;

	public String getPathId() {

		return pathId;
	}

	public void setPathId(String pathId) {

		this.pathId = pathId;
	}

	public String getInstrumentDescriptionLink() {

		return instrumentDescriptionLink;
	}

	public void setInstrumentDescriptionLink(String instrumentDescriptionLink) {

		this.instrumentDescriptionLink = instrumentDescriptionLink;
	}

	public String getInstrumentPropertyLink() {

		return instrumentPropertyLink;
	}

	public void setInstrumentPropertyLink(String instrumentPropertyLink) {

		this.instrumentPropertyLink = instrumentPropertyLink;
	}

	public String getSampleDescriptionLink() {

		return sampleDescriptionLink;
	}

	public void setSampleDescriptionLink(String sampleDescriptionLink) {

		this.sampleDescriptionLink = sampleDescriptionLink;
	}

	public String getSamplePropertyLink() {

		return samplePropertyLink;
	}

	public void setSamplePropertyLink(String samplePropertyLink) {

		this.samplePropertyLink = samplePropertyLink;
	}

	public String getMeasurementDescriptionLink() {

		return measurementDescriptionLink;
	}

	public void setMeasurementDescriptionLink(String measurementDescriptionLink) {

		this.measurementDescriptionLink = measurementDescriptionLink;
	}

	public String getMeasurementPropertyLink() {

		return measurementPropertyLink;
	}

	public void setMeasurementPropertyLink(String measurementPropertyLink) {

		this.measurementPropertyLink = measurementPropertyLink;
	}

	public String getDataPropertyLink() {

		return dataPropertyLink;
	}

	public void setDataPropertyLink(String dataPropertyLink) {

		this.dataPropertyLink = dataPropertyLink;
	}

	public String getDataCoreLink() {

		return dataCoreLink;
	}

	public void setDataCoreLink(String dataCoreLink) {

		this.dataCoreLink = dataCoreLink;
	}
}
