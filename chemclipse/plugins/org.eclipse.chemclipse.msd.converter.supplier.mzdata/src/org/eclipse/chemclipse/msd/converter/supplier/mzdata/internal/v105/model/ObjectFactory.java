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
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	public ObjectFactory() {
	}

	public MzData createMzData() {

		return new MzData();
	}

	public DataProcessingType createDataProcessingType() {

		return new DataProcessingType();
	}

	public InstrumentDescriptionType createInstrumentDescriptionType() {

		return new InstrumentDescriptionType();
	}

	public SpectrumSettingsType createSpectrumSettingsType() {

		return new SpectrumSettingsType();
	}

	public SpectrumSettingsType.AcqSpecification createSpectrumSettingsTypeAcqSpecification() {

		return new SpectrumSettingsType.AcqSpecification();
	}

	public SupDataBinaryType createSupDataBinaryType() {

		return new SupDataBinaryType();
	}

	public SpectrumDescType createSpectrumDescType() {

		return new SpectrumDescType();
	}

	public MzData.SpectrumList createMzDataSpectrumList() {

		return new MzData.SpectrumList();
	}

	public CvLookupType createCvLookupType() {

		return new CvLookupType();
	}

	public MzData.Description createMzDataDescription() {

		return new MzData.Description();
	}

	public CvParamType createCvParamType() {

		return new CvParamType();
	}

	public SpectrumType createSpectrumType() {

		return new SpectrumType();
	}

	public SupDescType createSupDescType() {

		return new SupDescType();
	}

	public AdminType createAdminType() {

		return new AdminType();
	}

	public SoftwareType createSoftwareType() {

		return new SoftwareType();
	}

	public SupDataType createSupDataType() {

		return new SupDataType();
	}

	public ParamType createParamType() {

		return new ParamType();
	}

	public SourceFileType createSourceFileType() {

		return new SourceFileType();
	}

	public PrecursorType createPrecursorType() {

		return new PrecursorType();
	}

	public PeakListBinaryType createPeakListBinaryType() {

		return new PeakListBinaryType();
	}

	public DescriptionType createDescriptionType() {

		return new DescriptionType();
	}

	public UserParamType createUserParamType() {

		return new UserParamType();
	}

	public PersonType createPersonType() {

		return new PersonType();
	}

	public DataProcessingType.Software createDataProcessingTypeSoftware() {

		return new DataProcessingType.Software();
	}

	public InstrumentDescriptionType.AnalyzerList createInstrumentDescriptionTypeAnalyzerList() {

		return new InstrumentDescriptionType.AnalyzerList();
	}

	public SpectrumSettingsType.SpectrumInstrument createSpectrumSettingsTypeSpectrumInstrument() {

		return new SpectrumSettingsType.SpectrumInstrument();
	}

	public SpectrumSettingsType.AcqSpecification.Acquisition createSpectrumSettingsTypeAcqSpecificationAcquisition() {

		return new SpectrumSettingsType.AcqSpecification.Acquisition();
	}

	public SupDataBinaryType.Data createSupDataBinaryTypeData() {

		return new SupDataBinaryType.Data();
	}

	public SpectrumDescType.PrecursorList createSpectrumDescTypePrecursorList() {

		return new SpectrumDescType.PrecursorList();
	}

	public MzData.SpectrumList.Spectrum createMzDataSpectrumListSpectrum() {

		return new MzData.SpectrumList.Spectrum();
	}
}
