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
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Description of a particular hardware configuration of a mass spectrometer. Each configuration must have one (and only one) of the three different components used for an analysis. For hybrid instruments, such as an LTQ-FT, there must be one configuration for each permutation of the components that is used in the document. For software configuration, use a ReferenceableParamGroup element.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstrumentConfigurationType", propOrder = {"componentList", "softwareRef"})
public class InstrumentConfigurationType extends ParamGroupType {

	@XmlElement(required = true)
	protected ComponentListType componentList;
	protected SoftwareRefType softwareRef;
	@XmlAttribute(name = "id", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;

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
}
