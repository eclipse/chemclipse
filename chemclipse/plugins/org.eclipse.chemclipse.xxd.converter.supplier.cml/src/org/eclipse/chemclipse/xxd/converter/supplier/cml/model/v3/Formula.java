/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Formula {

	@XmlAttribute(name = "inline")
	private String inline;
	@XmlAttribute(name = "concise")
	private String concise;

	public String getInline() {

		return inline;
	}

	public void setInline(String inline) {

		this.inline = inline;
	}

	public String getConcise() {

		return concise;
	}

	public void setConcise(String concise) {

		this.concise = concise;
	}
}
