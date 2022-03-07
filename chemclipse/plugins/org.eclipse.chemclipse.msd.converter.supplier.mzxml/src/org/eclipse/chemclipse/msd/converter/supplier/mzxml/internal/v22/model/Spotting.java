/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"plate", "robot"})
public class Spotting implements Serializable {

	private final static long serialVersionUID = 220L;
	@XmlElement(required = true)
	private List<Plate> plate;
	private Robot robot;

	public List<Plate> getPlate() {

		if(plate == null) {
			plate = new ArrayList<Plate>();
		}
		return this.plate;
	}

	public Robot getRobot() {

		return robot;
	}

	public void setRobot(Robot value) {

		this.robot = value;
	}
}
