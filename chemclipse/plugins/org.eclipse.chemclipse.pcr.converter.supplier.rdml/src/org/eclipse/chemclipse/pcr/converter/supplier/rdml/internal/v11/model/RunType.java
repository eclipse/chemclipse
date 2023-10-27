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

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A run is a set of reactions performed in one "run", for example
 * one plate, one rotor, one array, one chip.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "runType", propOrder = {"description", "documentation", "experimenter", "instrument", "dataCollectionSoftware", "backgroundDeterminationMethod", "cqDetectionMethod", "thermalCyclingConditions", "pcrFormat", "runDate", "react"})
public class RunType {

	protected String description;
	protected List<IdReferencesType> documentation;
	protected List<IdReferencesType> experimenter;
	protected String instrument;
	protected DataCollectionSoftwareType dataCollectionSoftware;
	protected String backgroundDeterminationMethod;
	@XmlSchemaType(name = "string")
	protected CqDetectionMethodType cqDetectionMethod;
	protected IdReferencesType thermalCyclingConditions;
	@XmlElement(required = true)
	protected PcrFormatType pcrFormat;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar runDate;
	protected List<ReactType> react;
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

	public List<IdReferencesType> getExperimenter() {

		if(experimenter == null) {
			experimenter = new ArrayList<>();
		}
		return this.experimenter;
	}

	public String getInstrument() {

		return instrument;
	}

	public void setInstrument(String value) {

		this.instrument = value;
	}

	public DataCollectionSoftwareType getDataCollectionSoftware() {

		return dataCollectionSoftware;
	}

	public void setDataCollectionSoftware(DataCollectionSoftwareType value) {

		this.dataCollectionSoftware = value;
	}

	public String getBackgroundDeterminationMethod() {

		return backgroundDeterminationMethod;
	}

	public void setBackgroundDeterminationMethod(String value) {

		this.backgroundDeterminationMethod = value;
	}

	public CqDetectionMethodType getCqDetectionMethod() {

		return cqDetectionMethod;
	}

	public void setCqDetectionMethod(CqDetectionMethodType value) {

		this.cqDetectionMethod = value;
	}

	public IdReferencesType getThermalCyclingConditions() {

		return thermalCyclingConditions;
	}

	public void setThermalCyclingConditions(IdReferencesType value) {

		this.thermalCyclingConditions = value;
	}

	public PcrFormatType getPcrFormat() {

		return pcrFormat;
	}

	public void setPcrFormat(PcrFormatType value) {

		this.pcrFormat = value;
	}

	public XMLGregorianCalendar getRunDate() {

		return runDate;
	}

	public void setRunDate(XMLGregorianCalendar value) {

		this.runDate = value;
	}

	public List<ReactType> getReact() {

		if(react == null) {
			react = new ArrayList<>();
		}
		return this.react;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
