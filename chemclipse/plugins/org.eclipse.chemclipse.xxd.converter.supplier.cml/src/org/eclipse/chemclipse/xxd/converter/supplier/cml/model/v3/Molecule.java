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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "molecule")
@XmlAccessorType(XmlAccessType.FIELD)
public class Molecule {

	@XmlElement
	private Formula formula;
	@XmlElement
	private Name name;

	public Formula getFormula() {

		return formula;
	}

	public void setFormula(Formula formula) {

		this.formula = formula;
	}

	public Name getName() {

		return name;
	}

	public void setName(Name name) {

		this.name = name;
	}
}