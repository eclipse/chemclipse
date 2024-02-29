/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v22.model;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"spottingPattern", "orientation"})
public class Pattern implements Serializable {

	private static final long serialVersionUID = 220L;
	@XmlElement(required = true)
	private OntologyEntry spottingPattern;
	@XmlElement(required = true)
	private Orientation orientation;

	public OntologyEntry getSpottingPattern() {

		return spottingPattern;
	}

	public void setSpottingPattern(OntologyEntry value) {

		this.spottingPattern = value;
	}

	public Orientation getOrientation() {

		return orientation;
	}

	public void setOrientation(Orientation value) {

		this.orientation = value;
	}
}
