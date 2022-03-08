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
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spectrumType", propOrder = {"spectrumDesc", "supDesc", "mzArrayBinary", "intenArrayBinary", "supDataArrayBinaryOrSupDataArray"})
@XmlSeeAlso({org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData.SpectrumList.Spectrum.class})
public class SpectrumType {

	@XmlElement(required = true)
	private SpectrumDescType spectrumDesc;
	private List<SupDescType> supDesc;
	@XmlElement(required = true)
	private PeakListBinaryType mzArrayBinary;
	@XmlElement(required = true)
	private PeakListBinaryType intenArrayBinary;
	@XmlElements({@XmlElement(name = "supDataArrayBinary", type = SupDataBinaryType.class), @XmlElement(name = "supDataArray", type = SupDataType.class)})
	private List<Object> supDataArrayBinaryOrSupDataArray;
	@XmlAttribute(name = "id", required = true)
	private int id;

	public SpectrumDescType getSpectrumDesc() {

		return spectrumDesc;
	}

	public void setSpectrumDesc(SpectrumDescType value) {

		this.spectrumDesc = value;
	}

	public List<SupDescType> getSupDesc() {

		if(supDesc == null) {
			supDesc = new ArrayList<SupDescType>();
		}
		return this.supDesc;
	}

	public PeakListBinaryType getMzArrayBinary() {

		return mzArrayBinary;
	}

	public void setMzArrayBinary(PeakListBinaryType value) {

		this.mzArrayBinary = value;
	}

	public PeakListBinaryType getIntenArrayBinary() {

		return intenArrayBinary;
	}

	public void setIntenArrayBinary(PeakListBinaryType value) {

		this.intenArrayBinary = value;
	}

	public List<Object> getSupDataArrayBinaryOrSupDataArray() {

		if(supDataArrayBinaryOrSupDataArray == null) {
			supDataArrayBinaryOrSupDataArray = new ArrayList<Object>();
		}
		return this.supDataArrayBinaryOrSupDataArray;
	}

	public int getId() {

		return id;
	}

	public void setId(int value) {

		this.id = value;
	}
}
