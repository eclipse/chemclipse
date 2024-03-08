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
public class SampleParameter {

	@XmlElement(name = "state")
	private String state;
	@XmlElement(name = "pathLength")
	private PathLength pathLength;
	@XmlElement(name = "amount")
	private Amount amount;
	@XmlElement(name = "pressure")
	private Pressure pressure;
	@XmlElement(name = "temperature")
	private Temperature temperature;
	@XmlElement(name = "humidity")
	private Humidity humidity;
	@XmlElement(name = "comment")
	private String comment;

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	public PathLength getPathLength() {

		return pathLength;
	}

	public void setPathLength(PathLength pathLength) {

		this.pathLength = pathLength;
	}

	public Amount getAmount() {

		return amount;
	}

	public void setAmount(Amount amount) {

		this.amount = amount;
	}

	public Pressure getPressure() {

		return pressure;
	}

	public void setPressure(Pressure pressure) {

		this.pressure = pressure;
	}

	public Temperature getTemperature() {

		return temperature;
	}

	public void setTemperature(Temperature temperature) {

		this.temperature = temperature;
	}

	public Humidity getHumidity() {

		return humidity;
	}

	public void setHumidity(Humidity humidity) {

		this.humidity = humidity;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}