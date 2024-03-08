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
public class MeasurementProperty {

	@XmlElement(name = "measurementParameter")
	private MeasurementParameter measurementParameter;
	@XmlElement(name = "measurementCorrection")
	private MeasurementCorrection measurementCorrection;
	@XmlAttribute(name = "measurementPropertyId")
	private String measurementPropertyId;

	public MeasurementParameter getMeasurementParameter() {

		return measurementParameter;
	}

	public void setMeasurementParameter(MeasurementParameter measurementParameter) {

		this.measurementParameter = measurementParameter;
	}

	public MeasurementCorrection getMeasurementCorrection() {

		return measurementCorrection;
	}

	public void setMeasurementCorrection(MeasurementCorrection measurementCorrection) {

		this.measurementCorrection = measurementCorrection;
	}

	public String getMeasurementPropertyId() {

		return measurementPropertyId;
	}

	public void setMeasurementPropertyId(String measurementPropertyId) {

		this.measurementPropertyId = measurementPropertyId;
	}
}