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
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v10.model;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A run in mzML should correspond to a single, consecutive and coherent set of scans on an instrument.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RunType", propOrder = {"sourceFileRefList", "spectrumList", "chromatogramList"})
public class RunType extends ParamGroupType {

	protected SourceFileRefListType sourceFileRefList;
	@XmlElement(required = true)
	protected SpectrumListType spectrumList;
	protected ChromatogramListType chromatogramList;
	@XmlAttribute(name = "id", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;
	@XmlAttribute(name = "defaultInstrumentConfigurationRef", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object defaultInstrumentConfigurationRef;
	@XmlAttribute(name = "sampleRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object sampleRef;
	@XmlAttribute(name = "startTimeStamp")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar startTimeStamp;

	public SourceFileRefListType getSourceFileRefList() {

		return sourceFileRefList;
	}

	public void setSourceFileRefList(SourceFileRefListType value) {

		this.sourceFileRefList = value;
	}

	public SpectrumListType getSpectrumList() {

		return spectrumList;
	}

	public void setSpectrumList(SpectrumListType value) {

		this.spectrumList = value;
	}

	public ChromatogramListType getChromatogramList() {

		return chromatogramList;
	}

	public void setChromatogramList(ChromatogramListType value) {

		this.chromatogramList = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public Object getDefaultInstrumentConfigurationRef() {

		return defaultInstrumentConfigurationRef;
	}

	public void setDefaultInstrumentConfigurationRef(Object value) {

		this.defaultInstrumentConfigurationRef = value;
	}

	public Object getSampleRef() {

		return sampleRef;
	}

	public void setSampleRef(Object value) {

		this.sampleRef = value;
	}

	public XMLGregorianCalendar getStartTimeStamp() {

		return startTimeStamp;
	}

	public void setStartTimeStamp(XMLGregorianCalendar value) {

		this.startTimeStamp = value;
	}
}
