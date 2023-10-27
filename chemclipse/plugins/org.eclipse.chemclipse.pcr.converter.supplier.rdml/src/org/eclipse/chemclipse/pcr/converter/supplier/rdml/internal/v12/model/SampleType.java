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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A sample is a defined template solution. Dilutions of the same
 * material differ in concentration and are considered different samples.
 * A technical replicate samples should contain the same name (reactions
 * are performed on the same material), and biological replicates should
 * contain different names (the nucleic acids derived from the different
 * biological replicates are not the same). Serial dilutions in a standard
 * curve must have a different name.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sampleType", propOrder = {"description", "documentation", "xRef", "annotation", "type", "interRunCalibrator", "quantity", "calibratorSample", "cdnaSynthesisMethod", "templateQuantity"})
public class SampleType {

	protected String description;
	protected List<IdReferencesType> documentation;
	protected List<XRefType> xRef;
	protected List<AnnotationType> annotation;
	@XmlElement(required = true, defaultValue = "unkn")
	@XmlSchemaType(name = "string")
	protected SampleTypeType type;
	@XmlElement(defaultValue = "false")
	protected Boolean interRunCalibrator;
	protected QuantityType quantity;
	@XmlElement(defaultValue = "false")
	protected Boolean calibratorSample;
	protected CdnaSynthesisMethodType cdnaSynthesisMethod;
	protected TemplateQuantityType templateQuantity;
	@XmlAttribute(name = "id", required = true)
	protected String id;

	public String getDescription() {

		return description;
	}

	public void setDescription(String value) {

		this.description = value;
	}

	public List<IdReferencesType> getDocumentation() {

		if(documentation == null) {
			documentation = new ArrayList<>();
		}
		return this.documentation;
	}

	public List<XRefType> getXRef() {

		if(xRef == null) {
			xRef = new ArrayList<>();
		}
		return this.xRef;
	}

	public List<AnnotationType> getAnnotation() {

		if(annotation == null) {
			annotation = new ArrayList<>();
		}
		return this.annotation;
	}

	public SampleTypeType getType() {

		return type;
	}

	public void setType(SampleTypeType value) {

		this.type = value;
	}

	public Boolean isInterRunCalibrator() {

		return interRunCalibrator;
	}

	public void setInterRunCalibrator(Boolean value) {

		this.interRunCalibrator = value;
	}

	public QuantityType getQuantity() {

		return quantity;
	}

	public void setQuantity(QuantityType value) {

		this.quantity = value;
	}

	public Boolean isCalibratorSample() {

		return calibratorSample;
	}

	public void setCalibratorSample(Boolean value) {

		this.calibratorSample = value;
	}

	public CdnaSynthesisMethodType getCdnaSynthesisMethod() {

		return cdnaSynthesisMethod;
	}

	public void setCdnaSynthesisMethod(CdnaSynthesisMethodType value) {

		this.cdnaSynthesisMethod = value;
	}

	public TemplateQuantityType getTemplateQuantity() {

		return templateQuantity;
	}

	public void setTemplateQuantity(TemplateQuantityType value) {

		this.templateQuantity = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
