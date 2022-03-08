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

import jakarta.xml.bind.annotation.XmlRegistry;

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

	public AcqDescType createAcqDescType() {

		return new AcqDescType();
	}

	public InstrumentDescriptionType createInstrumentDescriptionType() {

		return new InstrumentDescriptionType();
	}

	public SupDataBinaryType createSupDataBinaryType() {

		return new SupDataBinaryType();
	}

	public AcqSettingsType createAcqSettingsType() {

		return new AcqSettingsType();
	}

	public AcqSettingsType.AcqSpecification createAcqSettingsTypeAcqSpecification() {

		return new AcqSettingsType.AcqSpecification();
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

	public AcqDescType.PrecursorList createAcqDescTypePrecursorList() {

		return new AcqDescType.PrecursorList();
	}

	public InstrumentDescriptionType.AnalyzerList createInstrumentDescriptionTypeAnalyzerList() {

		return new InstrumentDescriptionType.AnalyzerList();
	}

	public SupDataBinaryType.Data createSupDataBinaryTypeData() {

		return new SupDataBinaryType.Data();
	}

	public AcqSettingsType.AcqInstrument createAcqSettingsTypeAcqInstrument() {

		return new AcqSettingsType.AcqInstrument();
	}

	public AcqSettingsType.AcqSpecification.Acquisition createAcqSettingsTypeAcqSpecificationAcquisition() {

		return new AcqSettingsType.AcqSpecification.Acquisition();
	}

	public MzData.SpectrumList.Spectrum createMzDataSpectrumListSpectrum() {

		return new MzData.SpectrumList.Spectrum();
	}
}
