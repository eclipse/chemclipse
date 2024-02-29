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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v31.model;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Orientation implements Serializable {

	private static final long serialVersionUID = 310L;
	@XmlAttribute(name = "firstSpotID", required = true)
	private String firstSpotID;
	@XmlAttribute(name = "secondSpotID", required = true)
	private String secondSpotID;

	public String getFirstSpotID() {

		return firstSpotID;
	}

	public void setFirstSpotID(String value) {

		this.firstSpotID = value;
	}

	public String getSecondSpotID() {

		return secondSpotID;
	}

	public void setSecondSpotID(String value) {

		this.secondSpotID = value;
	}
}
