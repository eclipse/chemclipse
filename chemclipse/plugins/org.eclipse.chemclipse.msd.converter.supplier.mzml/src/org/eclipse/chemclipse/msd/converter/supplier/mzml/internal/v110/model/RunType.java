/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RunType", propOrder = {"spectrumList", "chromatogramList"})
public class RunType extends ParamGroupType {

	private SpectrumListType spectrumList;
	private ChromatogramListType chromatogramList;
	@XmlAttribute(name = "id", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	private String id;
	@XmlAttribute(name = "defaultInstrumentConfigurationRef", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object defaultInstrumentConfigurationRef;
	@XmlAttribute(name = "defaultSourceFileRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object defaultSourceFileRef;
	@XmlAttribute(name = "sampleRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object sampleRef;
	@XmlAttribute(name = "startTimeStamp")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar startTimeStamp;

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

	public Object getDefaultSourceFileRef() {

		return defaultSourceFileRef;
	}

	public void setDefaultSourceFileRef(Object value) {

		this.defaultSourceFileRef = value;
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
