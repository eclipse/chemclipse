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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstrumentConfigurationType", propOrder = {"componentList", "softwareRef"})
public class InstrumentConfigurationType extends ParamGroupType {

	private ComponentListType componentList;
	private SoftwareRefType softwareRef;
	@XmlAttribute(name = "id", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	private String id;
	@XmlAttribute(name = "scanSettingsRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object scanSettingsRef;

	public ComponentListType getComponentList() {

		return componentList;
	}

	public void setComponentList(ComponentListType value) {

		this.componentList = value;
	}

	public SoftwareRefType getSoftwareRef() {

		return softwareRef;
	}

	public void setSoftwareRef(SoftwareRefType value) {

		this.softwareRef = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public Object getScanSettingsRef() {

		return scanSettingsRef;
	}

	public void setScanSettingsRef(Object value) {

		this.scanSettingsRef = value;
	}
}
