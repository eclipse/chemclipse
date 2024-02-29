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
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"xaxis", "yaxis"})
public class SpectrumData {

	@XmlElement(name = "xaxis")
	protected Xaxis xaxis;
	@XmlElement(name = "yaxis")
	protected Yaxis yaxis;

	public Xaxis getXaxis() {

		return xaxis;
	}

	public void setXaxis(Xaxis xaxis) {

		this.xaxis = xaxis;
	}

	public Yaxis getYaxis() {

		return yaxis;
	}

	public void setYaxis(Yaxis yaxis) {

		this.yaxis = yaxis;
	}
}
