/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataProcessingType", propOrder = {"software", "processingMethod"})
public class DataProcessingType {

	@XmlElement(required = true)
	protected DataProcessingType.Software software;
	protected ParamType processingMethod;

	public DataProcessingType.Software getSoftware() {

		return software;
	}

	public void setSoftware(DataProcessingType.Software value) {

		this.software = value;
	}

	public ParamType getProcessingMethod() {

		return processingMethod;
	}

	public void setProcessingMethod(ParamType value) {

		this.processingMethod = value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class Software extends SoftwareType {
	}
}
