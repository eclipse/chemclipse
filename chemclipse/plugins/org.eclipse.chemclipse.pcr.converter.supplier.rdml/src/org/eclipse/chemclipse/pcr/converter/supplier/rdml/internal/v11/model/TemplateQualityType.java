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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "templateQualityType", propOrder = {"method", "result"})
public class TemplateQualityType {

	@XmlElement(required = true)
	protected String method;
	protected float result;

	public String getMethod() {

		return method;
	}

	public void setMethod(String value) {

		this.method = value;
	}

	public float getResult() {

		return result;
	}

	public void setResult(float value) {

		this.result = value;
	}
}
