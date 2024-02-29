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

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeakGroup", propOrder = {"peak"})
public class PeakGroup {

	@XmlElement(name = "peak")
	protected List<Peak> peak;
	@XmlAttribute(name = "yMax")
	protected Double yMax;
	@XmlAttribute(name = "yWidth")
	protected Double yWidth;
	@XmlAttribute(name = "yValue")
	protected Double yValue;
	@XmlAttribute(name = "yUnits")
	protected String yUnits;
	@XmlAttribute(name = "moleculeRefs")
	protected List<String> moleculeRefs;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "xMin")
	protected Double xMin;
	@XmlAttribute(name = "xUnits")
	protected String xUnits;
	@XmlAttribute(name = "yMin")
	protected Double yMin;
	@XmlAttribute(name = "peakUnits")
	protected String peakUnits;
	@XmlAttribute(name = "xMax")
	protected Double xMax;

	public Double getYMax() {

		return yMax;
	}

	public void setYMax(Double value) {

		this.yMax = value;
	}

	public Double getYWidth() {

		return yWidth;
	}

	public void setYWidth(Double value) {

		this.yWidth = value;
	}

	public Double getYValue() {

		return yValue;
	}

	public void setYValue(Double value) {

		this.yValue = value;
	}

	public String getYUnits() {

		return yUnits;
	}

	public void setYUnits(String value) {

		this.yUnits = value;
	}

	public java.util.List<String> getMoleculeRefs() {

		if(moleculeRefs == null) {
			moleculeRefs = new java.util.ArrayList<>();
		}
		return this.moleculeRefs;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public Double getXMin() {

		return xMin;
	}

	public void setXMin(Double value) {

		this.xMin = value;
	}

	public String getXUnits() {

		return xUnits;
	}

	public void setXUnits(String value) {

		this.xUnits = value;
	}

	public Double getYMin() {

		return yMin;
	}

	public void setYMin(Double value) {

		this.yMin = value;
	}

	public String getPeakUnits() {

		return peakUnits;
	}

	public void setPeakUnits(String value) {

		this.peakUnits = value;
	}

	public Double getXMax() {

		return xMax;
	}

	public void setXMax(Double value) {

		this.xMax = value;
	}
}
