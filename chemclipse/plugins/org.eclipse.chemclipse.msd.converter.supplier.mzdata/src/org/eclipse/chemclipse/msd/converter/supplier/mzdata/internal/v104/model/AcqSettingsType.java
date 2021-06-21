/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "acqSettingsType", propOrder = {"acqSpecification", "acqInstrument"})
public class AcqSettingsType {

	protected AcqSettingsType.AcqSpecification acqSpecification;
	@XmlElement(required = true)
	protected AcqSettingsType.AcqInstrument acqInstrument;

	public AcqSettingsType.AcqSpecification getAcqSpecification() {

		return acqSpecification;
	}

	public void setAcqSpecification(AcqSettingsType.AcqSpecification value) {

		this.acqSpecification = value;
	}

	public AcqSettingsType.AcqInstrument getAcqInstrument() {

		return acqInstrument;
	}

	public void setAcqInstrument(AcqSettingsType.AcqInstrument value) {

		this.acqInstrument = value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class AcqInstrument extends ParamType {

		@XmlAttribute(name = "msLevel", required = true)
		protected int msLevel;
		@XmlAttribute(name = "mzRangeStart")
		protected Float mzRangeStart;
		@XmlAttribute(name = "mzRangeStop")
		protected Float mzRangeStop;

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

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"acquisition"})
	public static class AcqSpecification {

		@XmlElement(required = true)
		protected List<AcqSettingsType.AcqSpecification.Acquisition> acquisition;
		@XmlAttribute(name = "spectrumType", required = true)
		protected String spectrumType;
		@XmlAttribute(name = "methodOfCombination", required = true)
		protected String methodOfCombination;
		@XmlAttribute(name = "count", required = true)
		protected int count;

		public List<AcqSettingsType.AcqSpecification.Acquisition> getAcquisition() {

			if(acquisition == null) {
				acquisition = new ArrayList<AcqSettingsType.AcqSpecification.Acquisition>();
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
			protected int acqNumber;

			public int getAcqNumber() {

				return acqNumber;
			}

			public void setAcqNumber(int value) {

				this.acqNumber = value;
			}
		}
	}
}
