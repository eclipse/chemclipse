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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * The instrument's 'run time' parameters; common to the whole of this spectrum.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScanType", propOrder = {"scanWindowList"})
public class ScanType extends ParamGroupType {

	@XmlElement(required = true)
	protected ScanWindowListType scanWindowList;
	@XmlAttribute(name = "instrumentConfigurationRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object instrumentConfigurationRef;

	public ScanWindowListType getScanWindowList() {

		return scanWindowList;
	}

	public void setScanWindowList(ScanWindowListType value) {

		this.scanWindowList = value;
	}

	public Object getInstrumentConfigurationRef() {

		return instrumentConfigurationRef;
	}

	public void setInstrumentConfigurationRef(Object value) {

		this.instrumentConfigurationRef = value;
	}
}
