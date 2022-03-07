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
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cvLookup", "description", "spectrumList"})
@XmlRootElement(name = "mzData")
public class MzData {

	private List<CvLookupType> cvLookup;
	@XmlElement(required = true)
	private MzData.Description description;
	@XmlElement(required = true)
	private MzData.SpectrumList spectrumList;
	@XmlAttribute(name = "version", required = true)
	private String version;
	@XmlAttribute(name = "accessionNumber", required = true)
	private String accessionNumber;

	public List<CvLookupType> getCvLookup() {

		if(cvLookup == null) {
			cvLookup = new ArrayList<CvLookupType>();
		}
		return this.cvLookup;
	}

	public MzData.Description getDescription() {

		return description;
	}

	public void setDescription(MzData.Description value) {

		this.description = value;
	}

	public MzData.SpectrumList getSpectrumList() {

		return spectrumList;
	}

	public void setSpectrumList(MzData.SpectrumList value) {

		this.spectrumList = value;
	}

	public String getVersion() {

		if(version == null) {
			return "1.05";
		} else {
			return version;
		}
	}

	public void setVersion(String value) {

		this.version = value;
	}

	public String getAccessionNumber() {

		return accessionNumber;
	}

	public void setAccessionNumber(String value) {

		this.accessionNumber = value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"admin", "instrument", "dataProcessing"})
	public static class Description {

		@XmlElement(required = true)
		private AdminType admin;
		@XmlElement(required = true)
		private InstrumentDescriptionType instrument;
		@XmlElement(required = true)
		private DataProcessingType dataProcessing;

		public AdminType getAdmin() {

			return admin;
		}

		public void setAdmin(AdminType value) {

			this.admin = value;
		}

		public InstrumentDescriptionType getInstrument() {

			return instrument;
		}

		public void setInstrument(InstrumentDescriptionType value) {

			this.instrument = value;
		}

		public DataProcessingType getDataProcessing() {

			return dataProcessing;
		}

		public void setDataProcessing(DataProcessingType value) {

			this.dataProcessing = value;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"spectrum"})
	public static class SpectrumList {

		@XmlElement(required = true)
		private List<MzData.SpectrumList.Spectrum> spectrum;
		@XmlAttribute(name = "count", required = true)
		private int count;

		public List<MzData.SpectrumList.Spectrum> getSpectrum() {

			if(spectrum == null) {
				spectrum = new ArrayList<MzData.SpectrumList.Spectrum>();
			}
			return this.spectrum;
		}

		public int getCount() {

			return count;
		}

		public void setCount(int value) {

			this.count = value;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "")
		public static class Spectrum extends SpectrumType {
		}
	}
}
