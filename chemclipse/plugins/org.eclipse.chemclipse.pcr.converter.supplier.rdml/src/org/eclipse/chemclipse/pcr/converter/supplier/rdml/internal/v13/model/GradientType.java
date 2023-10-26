/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model;

import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * This step forms a temperature gradient across the PCR block.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gradientType", propOrder = {"highTemperature", "lowTemperature", "duration", "temperatureChange", "durationChange", "measure", "ramp"})
public class GradientType {

	protected float highTemperature;
	protected float lowTemperature;
	@XmlElement(required = true)
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger duration;
	protected Float temperatureChange;
	protected Integer durationChange;
	@XmlSchemaType(name = "string")
	protected MeasureType measure;
	protected Float ramp;

	public float getHighTemperature() {

		return highTemperature;
	}

	public void setHighTemperature(float value) {

		this.highTemperature = value;
	}

	public float getLowTemperature() {

		return lowTemperature;
	}

	public void setLowTemperature(float value) {

		this.lowTemperature = value;
	}

	public BigInteger getDuration() {

		return duration;
	}

	public void setDuration(BigInteger value) {

		this.duration = value;
	}

	public Float getTemperatureChange() {

		return temperatureChange;
	}

	public void setTemperatureChange(Float value) {

		this.temperatureChange = value;
	}

	public Integer getDurationChange() {

		return durationChange;
	}

	public void setDurationChange(Integer value) {

		this.durationChange = value;
	}

	public MeasureType getMeasure() {

		return measure;
	}

	public void setMeasure(MeasureType value) {

		this.measure = value;
	}

	public Float getRamp() {

		return ramp;
	}

	public void setRamp(Float value) {

		this.ramp = value;
	}
}
