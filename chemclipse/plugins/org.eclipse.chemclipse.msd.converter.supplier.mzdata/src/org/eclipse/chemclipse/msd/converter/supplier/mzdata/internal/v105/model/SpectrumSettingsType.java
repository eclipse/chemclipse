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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spectrumSettingsType", propOrder = {"acqSpecification", "spectrumInstrument"})
public class SpectrumSettingsType {

	private SpectrumSettingsType.AcqSpecification acqSpecification;
	@XmlElement(required = true)
	private SpectrumSettingsType.SpectrumInstrument spectrumInstrument;

	public SpectrumSettingsType.AcqSpecification getAcqSpecification() {

		return acqSpecification;
	}

	public void setAcqSpecification(SpectrumSettingsType.AcqSpecification value) {

		this.acqSpecification = value;
	}

	public SpectrumSettingsType.SpectrumInstrument getSpectrumInstrument() {

		return spectrumInstrument;
	}

	public void setSpectrumInstrument(SpectrumSettingsType.SpectrumInstrument value) {

		this.spectrumInstrument = value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"acquisition"})
	public static class AcqSpecification {

		@XmlElement(required = true)
		private List<SpectrumSettingsType.AcqSpecification.Acquisition> acquisition;
		@XmlAttribute(name = "spectrumType", required = true)
		private String spectrumType;
		@XmlAttribute(name = "methodOfCombination", required = true)
		private String methodOfCombination;
		@XmlAttribute(name = "count", required = true)
		private int count;

		public List<SpectrumSettingsType.AcqSpecification.Acquisition> getAcquisition() {

			if(acquisition == null) {
				acquisition = new ArrayList<SpectrumSettingsType.AcqSpecification.Acquisition>();
			}
			return this.acquisition;
		}

		public String getSpectrumType() {

			return spectrumType;
		}

		public void setSpectrumType(String value) {

			this.spectrumType = value;
		}

		public String getMethodOfCombination() {

			return methodOfCombination;
		}

		public void setMethodOfCombination(String value) {

			this.methodOfCombination = value;
		}

		public int getCount() {

			return count;
		}

		public void setCount(int value) {

			this.count = value;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "")
		public static class Acquisition extends ParamType {

			@XmlAttribute(name = "acqNumber", required = true)
			private int acqNumber;

			public int getAcqNumber() {

				return acqNumber;
			}

			public void setAcqNumber(int value) {

				this.acqNumber = value;
			}
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class SpectrumInstrument extends ParamType {

		@XmlAttribute(name = "msLevel", required = true)
		private int msLevel;
		@XmlAttribute(name = "mzRangeStart")
		private Float mzRangeStart;
		@XmlAttribute(name = "mzRangeStop")
		private Float mzRangeStop;

		public int getMsLevel() {

			return msLevel;
		}

		public void setMsLevel(int value) {

			this.msLevel = value;
		}

		public Float getMzRangeStart() {

			return mzRangeStart;
		}

		public void setMzRangeStart(Float value) {

			this.mzRangeStart = value;
		}

		public Float getMzRangeStop() {

			return mzRangeStop;
		}

		public void setMzRangeStop(Float value) {

			this.mzRangeStop = value;
		}
	}
}
