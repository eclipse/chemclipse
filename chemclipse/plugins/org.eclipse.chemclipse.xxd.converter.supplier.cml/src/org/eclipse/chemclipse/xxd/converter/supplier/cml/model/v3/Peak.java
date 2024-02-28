/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Peak {

	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "title")
	protected String title;
	@XmlAttribute(name = "peakMultiplicity")
	protected PeakMultiplicityType peakMultiplicity;
	@XmlAttribute(name = "peakShape")
	protected PeakShapeType peakShape;
	@XmlAttribute(name = "xUnits")
	protected String xUnits;
	@XmlAttribute(name = "xValue")
	protected Double xValue;
	@XmlAttribute(name = "yUnits")
	protected String yUnits;
	@XmlAttribute(name = "yValue")
	protected Double yValue;
	@XmlAttribute(name = "integral")
	protected String integral;
	@XmlAttribute(name = "peakHeight")
	protected Double peakHeight;
	@XmlAttribute(name = "xWidth")
	protected Double xWidth;
	@XmlAttribute(name = "yMax")
	protected Double yMax;

	public String getTitle() {

		return title;
	}

	public void setTitle(String value) {

		this.title = value;
	}

	public String getIntegral() {

		return integral;
	}

	public void setIntegral(String value) {

		this.integral = value;
	}

	public PeakShapeType getPeakShape() {

		return peakShape;
	}

	public void setPeakShape(PeakShapeType value) {

		this.peakShape = value;
	}

	public String getYUnits() {

		return yUnits;
	}

	public void setYUnits(String value) {

		this.yUnits = value;
	}

	public PeakMultiplicityType getPeakMultiplicity() {

		return peakMultiplicity;
	}

	public void setPeakMultiplicity(PeakMultiplicityType value) {

		this.peakMultiplicity = value;
	}

	public String getXUnits() {

		return xUnits;
	}

	public void setXUnits(String value) {

		this.xUnits = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public Double getPeakHeight() {

		return peakHeight;
	}

	public void setPeakHeight(Double value) {

		this.peakHeight = value;
	}

	public Double getXValue() {

		return xValue;
	}

	public void setXValue(Double value) {

		this.xValue = value;
	}

	public Double getXWidth() {

		return xWidth;
	}

	public void setXWidth(Double value) {

		this.xWidth = value;
	}

	public Double getYMax() {

		return yMax;
	}

	public void setYMax(Double value) {

		this.yMax = value;
	}

	public Double getYValue() {

		return yValue;
	}

	public void setYValue(Double value) {

		this.yValue = value;
	}
}
