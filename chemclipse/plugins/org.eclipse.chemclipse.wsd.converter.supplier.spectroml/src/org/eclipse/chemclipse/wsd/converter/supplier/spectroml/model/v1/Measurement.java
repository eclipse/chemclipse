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

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Measurement {

	@XmlElement(name = "measurementDescription")
	private List<MeasurementDescription> measurementDescriptionList;
	@XmlElement(name = "measurementProperty")
	private List<MeasurementProperty> measurementPropertyList;

	public List<MeasurementDescription> getMeasurementDescriptionList() {

		return measurementDescriptionList;
	}

	public void setMeasurementDescriptionList(List<MeasurementDescription> measurementDescriptionList) {

		this.measurementDescriptionList = measurementDescriptionList;
	}

	public List<MeasurementProperty> getMeasurementPropertyList() {

		return measurementPropertyList;
	}

	public void setMeasurementPropertyList(List<MeasurementProperty> measurementPropertyList) {

		this.measurementPropertyList = measurementPropertyList;
	}
}