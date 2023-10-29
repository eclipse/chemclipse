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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v11.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A cycling program for PCR or to amplify cDNA.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "thermalCyclingConditionsType", propOrder = {"description", "documentation", "lidTemperature", "experimenter", "step"})
public class ThermalCyclingConditionsType {

	protected String description;
	protected List<IdReferencesType> documentation;
	protected Float lidTemperature;
	protected List<IdReferencesType> experimenter;
	@XmlElement(required = true)
	protected List<StepType> step;
	@XmlAttribute(name = "id", required = true)
	protected String id;

	public String getDescription() {

		return description;
	}

	public void setDescription(String value) {

		this.description = value;
	}

	public List<IdReferencesType> getDocumentation() {

		if(documentation == null) {
			documentation = new ArrayList<>();
		}
		return this.documentation;
	}

	public Float getLidTemperature() {

		return lidTemperature;
	}

	public void setLidTemperature(Float value) {

		this.lidTemperature = value;
	}

	public List<IdReferencesType> getExperimenter() {

		if(experimenter == null) {
			experimenter = new ArrayList<>();
		}
		return this.experimenter;
	}

	public List<StepType> getStep() {

		if(step == null) {
			step = new ArrayList<>();
		}
		return this.step;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
