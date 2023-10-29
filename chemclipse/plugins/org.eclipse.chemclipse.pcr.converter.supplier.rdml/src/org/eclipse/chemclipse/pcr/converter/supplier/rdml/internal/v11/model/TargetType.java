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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v11.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A target is a defined PCR reaction. PCR reactions for the same gene
 * which differ in primer sequences are considered different samples.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "targetType", propOrder = {"description", "documentation", "xRef", "type", "amplificationEfficiencyMethod", "amplificationEfficiency", "detectionLimit", "dyeId", "sequences", "commercialAssay"})
public class TargetType {

	protected String description;
	protected List<IdReferencesType> documentation;
	protected List<XRefType> xRef;
	@XmlElement(required = true)
	@XmlSchemaType(name = "string")
	protected TargetTypeType type;
	protected String amplificationEfficiencyMethod;
	protected Float amplificationEfficiency;
	protected Float detectionLimit;
	@XmlElement(required = true)
	protected IdReferencesType dyeId;
	protected SequencesType sequences;
	protected CommercialAssayType commercialAssay;
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

	public TargetTypeType getType() {

		return type;
	}

	public void setType(TargetTypeType value) {

		this.type = value;
	}

	public String getAmplificationEfficiencyMethod() {

		return amplificationEfficiencyMethod;
	}

	public void setAmplificationEfficiencyMethod(String value) {

		this.amplificationEfficiencyMethod = value;
	}

	public Float getAmplificationEfficiency() {

		return amplificationEfficiency;
	}

	public void setAmplificationEfficiency(Float value) {

		this.amplificationEfficiency = value;
	}

	public Float getDetectionLimit() {

		return detectionLimit;
	}

	public void setDetectionLimit(Float value) {

		this.detectionLimit = value;
	}

	public IdReferencesType getDyeId() {

		return dyeId;
	}

	public void setDyeId(IdReferencesType value) {

		this.dyeId = value;
	}

	public SequencesType getSequences() {

		return sequences;
	}

	public void setSequences(SequencesType value) {

		this.sequences = value;
	}

	public CommercialAssayType getCommercialAssay() {

		return commercialAssay;
	}

	public void setCommercialAssay(CommercialAssayType value) {

		this.commercialAssay = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
