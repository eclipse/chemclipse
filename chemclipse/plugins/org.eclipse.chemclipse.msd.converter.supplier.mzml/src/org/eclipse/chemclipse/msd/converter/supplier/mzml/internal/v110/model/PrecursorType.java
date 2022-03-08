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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrecursorType", propOrder = {"isolationWindow", "selectedIonList", "activation"})
public class PrecursorType {

	private ParamGroupType isolationWindow;
	private SelectedIonListType selectedIonList;
	@XmlElement(required = true)
	private ParamGroupType activation;
	@XmlAttribute(name = "spectrumRef")
	private String spectrumRef;
	@XmlAttribute(name = "sourceFileRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object sourceFileRef;
	@XmlAttribute(name = "externalSpectrumID")
	private String externalSpectrumID;

	public ParamGroupType getIsolationWindow() {

		return isolationWindow;
	}

	public void setIsolationWindow(ParamGroupType value) {

		this.isolationWindow = value;
	}

	public SelectedIonListType getSelectedIonList() {

		return selectedIonList;
	}

	public void setSelectedIonList(SelectedIonListType value) {

		this.selectedIonList = value;
	}

	public ParamGroupType getActivation() {

		return activation;
	}

	public void setActivation(ParamGroupType value) {

		this.activation = value;
	}

	public String getSpectrumRef() {

		return spectrumRef;
	}

	public void setSpectrumRef(String value) {

		this.spectrumRef = value;
	}

	public Object getSourceFileRef() {

		return sourceFileRef;
	}

	public void setSourceFileRef(Object value) {

		this.sourceFileRef = value;
	}

	public String getExternalSpectrumID() {

		return externalSpectrumID;
	}

	public void setExternalSpectrumID(String value) {

		this.externalSpectrumID = value;
	}
}
