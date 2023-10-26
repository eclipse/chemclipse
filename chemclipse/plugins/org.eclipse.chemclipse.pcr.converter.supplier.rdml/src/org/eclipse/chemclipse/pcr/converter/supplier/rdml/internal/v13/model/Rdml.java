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

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"dateMade", "dateUpdated", "id", "experimenter", "documentation", "dye", "sample", "target", "thermalCyclingConditions", "experiment"})
@XmlRootElement(name = "rdml")
public class Rdml {

	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar dateMade;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar dateUpdated;
	protected List<RdmlIdType> id;
	protected List<ExperimenterType> experimenter;
	protected List<DocumentationType> documentation;
	protected List<DyeType> dye;
	protected List<SampleType> sample;
	protected List<TargetType> target;
	protected List<ThermalCyclingConditionsType> thermalCyclingConditions;
	protected List<ExperimentType> experiment;
	@XmlAttribute(name = "version", required = true)
	protected String version;

	public XMLGregorianCalendar getDateMade() {

		return dateMade;
	}

	public void setDateMade(XMLGregorianCalendar value) {

		this.dateMade = value;
	}

	public XMLGregorianCalendar getDateUpdated() {

		return dateUpdated;
	}

	public void setDateUpdated(XMLGregorianCalendar value) {

		this.dateUpdated = value;
	}

	public List<RdmlIdType> getId() {

		if(id == null) {
			id = new ArrayList<>();
		}
		return this.id;
	}

	public List<ExperimenterType> getExperimenter() {

		if(experimenter == null) {
			experimenter = new ArrayList<>();
		}
		return this.experimenter;
	}

	public List<DocumentationType> getDocumentation() {

		if(documentation == null) {
			documentation = new ArrayList<>();
		}
		return this.documentation;
	}

	public List<DyeType> getDye() {

		if(dye == null) {
			dye = new ArrayList<>();
		}
		return this.dye;
	}

	public List<SampleType> getSample() {

		if(sample == null) {
			sample = new ArrayList<>();
		}
		return this.sample;
	}

	public List<TargetType> getTarget() {

		if(target == null) {
			target = new ArrayList<>();
		}
		return this.target;
	}

	public List<ThermalCyclingConditionsType> getThermalCyclingConditions() {

		if(thermalCyclingConditions == null) {
			thermalCyclingConditions = new ArrayList<>();
		}
		return this.thermalCyclingConditions;
	}

	public List<ExperimentType> getExperiment() {

		if(experiment == null) {
			experiment = new ArrayList<>();
		}
		return this.experiment;
	}

	public String getVersion() {

		if(version == null) {
			return "1.3";
		} else {
			return version;
		}
	}

	public void setVersion(String value) {

		this.version = value;
	}
}
