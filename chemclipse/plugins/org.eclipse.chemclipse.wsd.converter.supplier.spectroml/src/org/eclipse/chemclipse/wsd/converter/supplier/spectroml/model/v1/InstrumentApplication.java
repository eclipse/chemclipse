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
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class InstrumentApplication {

	@XmlElement(name = "software")
	private String software;
	@XmlElement(name = "version")
	private String version;
	@XmlElement(name = "operatingSystem")
	private String operatingSystem;
	@XmlElement(name = "firmware")
	private String firmware;
	@XmlElement(name = "operator")
	private Operator operator;
	@XmlElement(name = "comment")
	private String comment;

	public String getSoftware() {

		return software;
	}

	public void setSoftware(String software) {

		this.software = software;
	}

	public String getVersion() {

		return version;
	}

	public void setVersion(String version) {

		this.version = version;
	}

	public String getOperatingSystem() {

		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {

		this.operatingSystem = operatingSystem;
	}

	public String getFirmware() {

		return firmware;
	}

	public void setFirmware(String firmware) {

		this.firmware = firmware;
	}

	public Operator getOperator() {

		return operator;
	}

	public void setOperator(Operator operator) {

		this.operator = operator;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}
