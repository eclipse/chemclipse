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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "spectrum")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"metadataList", "parameterList", "sample", "conditionList", "spectrumData", "peakList"})
public class Spectrum {

	@XmlAttribute(name = "ref")
	protected String ref;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "type")
	protected SpectrumType type;
	@XmlAttribute(name = "ft")
	protected FtType ft;
	@XmlAttribute(name = "measurement")
	protected MeasurementType measurement;
	@XmlAttribute(name = "title")
	protected String title;
	@XmlAttribute(name = "state")
	protected StateType state;
	@XmlAttribute(name = "convention")
	protected String convention;
	@XmlElement(name = "metadataList")
	protected MetadataList metadataList;
	@XmlElement(name = "parameterList")
	protected ParameterList parameterList;
	@XmlElement(name = "sample")
	protected Sample sample;
	@XmlElement(name = "conditionList")
	protected ConditionList conditionList;
	@XmlElement(name = "spectrumData")
	protected SpectrumData spectrumData;
	@XmlElement(name = "peakList")
	protected PeakList peakList;

	public String getRef() {

		return ref;
	}

	public void setRef(String value) {

		this.ref = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public SpectrumType getType() {

		return type;
	}

	public void setType(SpectrumType value) {

		this.type = value;
	}

	public FtType getFt() {

		if(ft == null) {
			return FtType.NONE;
		} else {
			return ft;
		}
	}

	public void setFt(FtType value) {

		this.ft = value;
	}

	public MeasurementType getMeasurement() {

		return measurement;
	}

	public void setMeasurement(MeasurementType value) {

		this.measurement = value;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String value) {

		this.title = value;
	}

	public StateType getState() {

		return state;
	}

	public void setState(StateType value) {

		this.state = value;
	}

	public String getConvention() {

		return convention;
	}

	public void setConvention(String value) {

		this.convention = value;
	}

	public MetadataList getMetadataList() {

		return metadataList;
	}

	public void setMetadataList(MetadataList metadataList) {

		this.metadataList = metadataList;
	}

	public ParameterList getParameterList() {

		return parameterList;
	}

	public void setParameterList(ParameterList parameterList) {

		this.parameterList = parameterList;
	}

	public Sample getSample() {

		return sample;
	}

	public void setSample(Sample sample) {

		this.sample = sample;
	}

	public ConditionList getConditionList() {

		return conditionList;
	}

	public void setConditionList(ConditionList conditionList) {

		this.conditionList = conditionList;
	}

	public SpectrumData getSpectrumData() {

		return spectrumData;
	}

	public void setSpectrumData(SpectrumData spectrumData) {

		this.spectrumData = spectrumData;
	}

	public PeakList getPeakList() {

		return peakList;
	}

	public void setPeakList(PeakList peakList) {

		this.peakList = peakList;
	}
}
