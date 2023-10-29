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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * For some commercial assays the primer sequences may be unknown. This element
 * allows to describe commercial assays.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "commercialAssayType", propOrder = {"company", "orderNumber"})
public class CommercialAssayType {

	@XmlElement(required = true)
	protected String company;
	@XmlElement(required = true)
	protected String orderNumber;

	public String getCompany() {

		return company;
	}

	public void setCompany(String value) {

		this.company = value;
	}

	public String getOrderNumber() {

		return orderNumber;
	}

	public void setOrderNumber(String value) {

		this.orderNumber = value;
	}
}
