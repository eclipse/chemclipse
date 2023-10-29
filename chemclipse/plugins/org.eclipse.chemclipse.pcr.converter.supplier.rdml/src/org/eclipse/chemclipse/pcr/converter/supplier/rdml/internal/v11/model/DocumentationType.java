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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * These elements should be used if the same description applies to many samples,
 * targets or experiments.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentationType", propOrder = {})
public class DocumentationType {

	protected String text;
	@XmlAttribute(name = "id", required = true)
	protected String id;

	public String getText() {

		return text;
	}

	public void setText(String value) {

		this.text = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
