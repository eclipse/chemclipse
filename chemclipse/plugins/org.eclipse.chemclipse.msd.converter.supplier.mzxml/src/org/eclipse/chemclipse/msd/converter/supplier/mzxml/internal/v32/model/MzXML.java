/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailände - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"msRun"})
@XmlRootElement(name = "mzXML", namespace = "http://sashimi.sourceforge.net/schema_revision/mzXML_3.2")
public class MzXML implements Serializable {

	private final static long serialVersionUID = 320L;
	@XmlElement(required = true)
	private MsRun msRun;

	public MsRun getMsRun() {

		return msRun;
	}

	public void setMsRun(MsRun value) {

		this.msRun = value;
	}
}
