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

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paramType", propOrder = {"cvParamOrUserParam"})
@XmlSeeAlso({AcqSettingsType.AcqSpecification.Acquisition.class, AcqSettingsType.AcqInstrument.class, DescriptionType.class})
public class ParamType {

	@XmlElements({@XmlElement(name = "cvParam", type = CvParamType.class), @XmlElement(name = "userParam", type = UserParamType.class)})
	protected List<Object> cvParamOrUserParam;

	public List<Object> getCvParamOrUserParam() {

		if(cvParamOrUserParam == null) {
			cvParamOrUserParam = new ArrayList<Object>();
		}
		return this.cvParamOrUserParam;
	}
}
