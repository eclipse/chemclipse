/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v10.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Structure allowing the use of a controlled (cvParam) or uncontrolled vocabulary (userParam), or a reference to a predefined set of these in this mzML file (paramGroupRef).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParamGroupType", propOrder = {"referenceableParamGroupRef", "cvParam", "userParam"})
@XmlSeeAlso({AcquisitionListType.class, RunType.class, SourceFileType.class, AcquisitionSettingsType.class, ComponentType.class, SpectrumDescriptionType.class, BinaryDataArrayType.class, ChromatogramType.class, ScanType.class, AcquisitionType.class, ProcessingMethodType.class, SampleType.class, SpectrumType.class, InstrumentConfigurationType.class})
public class ParamGroupType {

	protected List<ReferenceableParamGroupRefType> referenceableParamGroupRef;
	protected List<CVParamType> cvParam;
	protected List<UserParamType> userParam;

	public List<ReferenceableParamGroupRefType> getReferenceableParamGroupRef() {

		if(referenceableParamGroupRef == null) {
			referenceableParamGroupRef = new ArrayList<>();
		}
		return this.referenceableParamGroupRef;
	}

	public List<CVParamType> getCvParam() {

		if(cvParam == null) {
			cvParam = new ArrayList<>();
		}
		return this.cvParam;
	}

	public List<UserParamType> getUserParam() {

		if(userParam == null) {
			userParam = new ArrayList<>();
		}
		return this.userParam;
	}
}
