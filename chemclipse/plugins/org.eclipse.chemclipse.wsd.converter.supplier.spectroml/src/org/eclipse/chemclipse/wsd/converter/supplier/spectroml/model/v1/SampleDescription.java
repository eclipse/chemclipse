/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SampleDescription {

	@XmlElement(name = "sampleDesignation")
	private SampleDesignation sampleDesignation;
	@XmlElement(name = "samplePreparation")
	private SamplePreparation samplePreparation;
	@XmlAttribute(name = "sampleDescriptionId")
	private String sampleDescriptionId;

	public SampleDesignation getSampleDesignation() {

		return sampleDesignation;
	}

	public void setSampleDesignation(SampleDesignation sampleDesignation) {

		this.sampleDesignation = sampleDesignation;
	}

	public SamplePreparation getSamplePreparation() {

		return samplePreparation;
	}

	public void setSamplePreparation(SamplePreparation samplePreparation) {

		this.samplePreparation = samplePreparation;
	}

	public String getSampleDescriptionId() {

		return sampleDescriptionId;
	}

	public void setSampleDescriptionId(String sampleDescriptionId) {

		this.sampleDescriptionId = sampleDescriptionId;
	}
}
