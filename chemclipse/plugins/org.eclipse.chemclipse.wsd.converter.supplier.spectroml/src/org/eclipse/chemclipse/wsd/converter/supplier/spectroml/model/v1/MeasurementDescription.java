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
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurementDescription {

	@XmlElement(name = "measurementDesignation")
	private String measurementDesignation;
	@XmlElement(name = "measurementExecution")
	private String measurementExecution;
	@XmlAttribute(name = "measurementDescriptionId")
	private String measurementDescriptionId;

	public String getMeasurementDesignation() {

		return measurementDesignation;
	}

	public void setMeasurementDesignation(String measurementDesignation) {

		this.measurementDesignation = measurementDesignation;
	}

	public String getMeasurementExecution() {

		return measurementExecution;
	}

	public void setMeasurementExecution(String measurementExecution) {

		this.measurementExecution = measurementExecution;
	}

	public String getMeasurementDescriptionId() {

		return measurementDescriptionId;
	}

	public void setMeasurementDescriptionId(String measurementDescriptionId) {

		this.measurementDescriptionId = measurementDescriptionId;
	}
}