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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spectrumType", propOrder = {"acqDesc", "supDesc", "mzArrayBinary", "intenArrayBinary", "supDataArrayBinaryOrSupDataArray"})
@XmlSeeAlso({MzData.SpectrumList.Spectrum.class})
public class SpectrumType {

	@XmlElement(required = true)
	protected AcqDescType acqDesc;
	protected List<SupDescType> supDesc;
	@XmlElement(required = true)
	protected PeakListBinaryType mzArrayBinary;
	@XmlElement(required = true)
	protected PeakListBinaryType intenArrayBinary;
	@XmlElements({@XmlElement(name = "supDataArrayBinary", type = SupDataBinaryType.class), @XmlElement(name = "supDataArray", type = SupDataType.class)})
	protected List<Object> supDataArrayBinaryOrSupDataArray;
	@XmlAttribute(name = "id", required = true)
	protected int id;

	public AcqDescType getAcqDesc() {

		return acqDesc;
	}

	public void setAcqDesc(AcqDescType value) {

		this.acqDesc = value;
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
