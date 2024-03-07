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

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataCore {

	@XmlElement(name = "values")
	private List<Value> valuesList;
	@XmlAttribute(name = "dataCoreId")
	private String dataCoreId;

	public List<Value> getValuesList() {

		return valuesList;
	}

	public void setValuesList(List<Value> valuesList) {

		this.valuesList = valuesList;
	}

	public String getDataCoreId() {

		return dataCoreId;
	}

	public void setDataCoreId(String dataCoreId) {

		this.dataCoreId = dataCoreId;
	}
}