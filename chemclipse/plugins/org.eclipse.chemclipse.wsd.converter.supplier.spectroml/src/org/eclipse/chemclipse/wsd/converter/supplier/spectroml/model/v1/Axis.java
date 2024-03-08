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
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Axis {

	@XmlAttribute(name = "dim")
	private String dimension;
	@XmlValue
	private String value;

	public String getDimension() {

		return dimension;
	}

	public void setDimension(String dimension) {

		this.dimension = dimension;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}
}
