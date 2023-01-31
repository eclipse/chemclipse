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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Description of the parameters for the mass spectrometer for a given acquisition (or list of acquisitions).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpectrumDescriptionType", propOrder = {"acquisitionList", "precursorList", "scan"})
@XmlSeeAlso({AcquisitionListType.class, PrecursorListType.class, ScanType.class})
public class SpectrumDescriptionType extends ParamGroupType {

	protected AcquisitionListType acquisitionList;
	protected PrecursorListType precursorList;
	protected ScanType scan;

	public AcquisitionListType getAcquisitionList() {

		return acquisitionList;
	}

	public void setAcquisitionList(AcquisitionListType value) {

		this.acquisitionList = value;
	}

	public PrecursorListType getPrecursorList() {

		return precursorList;
	}

	public void setPrecursorList(PrecursorListType value) {

		this.precursorList = value;
	}

	public ScanType getScan() {

		return scan;
	}

	public void setScan(ScanType value) {

		this.scan = value;
	}
}
