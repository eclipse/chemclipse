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
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParamGroupType", propOrder = {"referenceableParamGroupRef", "cvParam", "userParam"})
@XmlSeeAlso({SoftwareType.class, RunType.class, ScanSettingsType.class, SourceFileType.class, ScanListType.class, ComponentType.class, BinaryDataArrayType.class, ChromatogramType.class, ScanType.class, ProcessingMethodType.class, SampleType.class, SpectrumType.class, InstrumentConfigurationType.class})
public class ParamGroupType {

	private List<ReferenceableParamGroupRefType> referenceableParamGroupRef;
	private List<CVParamType> cvParam;
	private List<UserParamType> userParam;

	public List<ReferenceableParamGroupRefType> getReferenceableParamGroupRef() {

		if(referenceableParamGroupRef == null) {
			referenceableParamGroupRef = new ArrayList<ReferenceableParamGroupRefType>();
		}
		return this.referenceableParamGroupRef;
	}

	public List<CVParamType> getCvParam() {

		if(cvParam == null) {
			cvParam = new ArrayList<CVParamType>();
		}
		return this.cvParam;
	}

	public List<UserParamType> getUserParam() {

		if(userParam == null) {
			userParam = new ArrayList<UserParamType>();
		}
		return this.userParam;
	}
}
