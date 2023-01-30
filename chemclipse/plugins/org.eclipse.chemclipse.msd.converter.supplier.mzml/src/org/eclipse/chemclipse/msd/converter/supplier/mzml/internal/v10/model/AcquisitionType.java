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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Scan or acquisition from original raw file used to create this peak list, as specified in sourceFile.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcquisitionType")
public class AcquisitionType extends ParamGroupType {

	@XmlAttribute(name = "number", required = true)
	protected int number;
	@XmlAttribute(name = "spectrumRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object spectrumRef;
	@XmlAttribute(name = "sourceFileRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object sourceFileRef;
	@XmlAttribute(name = "externalNativeID")
	protected String externalNativeID;
	@XmlAttribute(name = "externalSpectrumID")
	protected String externalSpectrumID;

	public int getNumber() {

		return number;
	}

	public void setNumber(int value) {

		this.number = value;
	}

	public Object getSpectrumRef() {

		return spectrumRef;
	}

	public void setSpectrumRef(Object value) {

		this.spectrumRef = value;
	}

	public Object getSourceFileRef() {

		return sourceFileRef;
	}

	public void setSourceFileRef(Object value) {

		this.sourceFileRef = value;
	}

	public String getExternalNativeID() {

		return externalNativeID;
	}

	public void setExternalNativeID(String value) {

		this.externalNativeID = value;
	}

	public String getExternalSpectrumID() {

		return externalSpectrumID;
	}

	public void setExternalSpectrumID(String value) {

		this.externalSpectrumID = value;
	}
}
