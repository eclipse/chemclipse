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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v10.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Description of the cDNA synthesis method.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cdnaSynthesisMethodType", propOrder = {"enzyme", "primingMethod", "dnaseTreatment", "thermalCyclingConditions"})
public class CdnaSynthesisMethodType {

	protected String enzyme;
	@XmlSchemaType(name = "string")
	protected PrimingMethodType primingMethod;
	protected Boolean dnaseTreatment;
	protected IdReferencesType thermalCyclingConditions;

	public String getEnzyme() {

		return enzyme;
	}

	public void setEnzyme(String value) {

		this.enzyme = value;
	}

	public PrimingMethodType getPrimingMethod() {

		return primingMethod;
	}

	public void setPrimingMethod(PrimingMethodType value) {

		this.primingMethod = value;
	}

	public Boolean isDnaseTreatment() {

		return dnaseTreatment;
	}

	public void setDnaseTreatment(Boolean value) {

		this.dnaseTreatment = value;
	}

	public IdReferencesType getThermalCyclingConditions() {

		return thermalCyclingConditions;
	}

	public void setThermalCyclingConditions(IdReferencesType value) {

		this.thermalCyclingConditions = value;
	}
}
