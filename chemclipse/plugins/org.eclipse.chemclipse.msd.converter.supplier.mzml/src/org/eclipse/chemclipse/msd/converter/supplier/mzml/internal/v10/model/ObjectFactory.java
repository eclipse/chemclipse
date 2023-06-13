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

import javax.xml.namespace.QName;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	private final static QName _MzML_QNAME = new QName("http://psi.hupo.org/schema_revision/mzML_1.0.0", "mzML");
	private final static QName _ComponentListTypeDetector_QNAME = new QName("http://psi.hupo.org/schema_revision/mzML_1.0.0", "detector");
	private final static QName _ComponentListTypeSource_QNAME = new QName("http://psi.hupo.org/schema_revision/mzML_1.0.0", "source");
	private final static QName _ComponentListTypeAnalyzer_QNAME = new QName("http://psi.hupo.org/schema_revision/mzML_1.0.0", "analyzer");

	public ObjectFactory() {

	}

	public MzMLType createMzMLType() {

		return new MzMLType();
	}

	public DataProcessingListType createDataProcessingListType() {

		return new DataProcessingListType();
	}

	public SpectrumListType createSpectrumListType() {

		return new SpectrumListType();
	}

	public AcquisitionListType createAcquisitionListType() {

		return new AcquisitionListType();
	}

	public BinaryDataArrayListType createBinaryDataArrayListType() {

		return new BinaryDataArrayListType();
	}

	public PrecursorListType createPrecursorListType() {

		return new PrecursorListType();
	}

	public SourceFileRefType createSourceFileRefType() {

		return new SourceFileRefType();
	}

	public ScanWindowListType createScanWindowListType() {

		return new ScanWindowListType();
	}

	public CVListType createCVListType() {

		return new CVListType();
	}

	public ReferenceableParamGroupListType createReferenceableParamGroupListType() {

		return new ReferenceableParamGroupListType();
	}

	public SoftwareType createSoftwareType() {

		return new SoftwareType();
	}

	public SoftwareParamType createSoftwareParamType() {

		return new SoftwareParamType();
	}

	public ReferenceableParamGroupType createReferenceableParamGroupType() {

		return new ReferenceableParamGroupType();
	}

	public ChromatogramListType createChromatogramListType() {

		return new ChromatogramListType();
	}

	public DataProcessingType createDataProcessingType() {

		return new DataProcessingType();
	}

	public RunType createRunType() {

		return new RunType();
	}

	public SourceFileListType createSourceFileListType() {

		return new SourceFileListType();
	}

	public UserParamType createUserParamType() {

		return new UserParamType();
	}

	public SourceFileType createSourceFileType() {

		return new SourceFileType();
	}

	public CVParamType createCVParamType() {

		return new CVParamType();
	}

	public ComponentListType createComponentListType() {

		return new ComponentListType();
	}

	public SelectedIonListType createSelectedIonListType() {

		return new SelectedIonListType();
	}

	public AcquisitionSettingsType createAcquisitionSettingsType() {

		return new AcquisitionSettingsType();
	}

	public TargetListType createTargetListType() {

		return new TargetListType();
	}

	public FileDescriptionType createFileDescriptionType() {

		return new FileDescriptionType();
	}

	public ComponentType createComponentType() {

		return new ComponentType();
	}

	public InstrumentConfigurationListType createInstrumentConfigurationListType() {

		return new InstrumentConfigurationListType();
	}

	public ParamGroupType createParamGroupType() {

		return new ParamGroupType();
	}

	public SpectrumDescriptionType createSpectrumDescriptionType() {

		return new SpectrumDescriptionType();
	}

	public SampleListType createSampleListType() {

		return new SampleListType();
	}

	public SourceFileRefListType createSourceFileRefListType() {

		return new SourceFileRefListType();
	}

	public BinaryDataArrayType createBinaryDataArrayType() {

		return new BinaryDataArrayType();
	}

	public ChromatogramType createChromatogramType() {

		return new ChromatogramType();
	}

	public ScanType createScanType() {

		return new ScanType();
	}

	public AcquisitionSettingsListType createAcquisitionSettingsListType() {

		return new AcquisitionSettingsListType();
	}

	public AcquisitionType createAcquisitionType() {

		return new AcquisitionType();
	}

	public PrecursorType createPrecursorType() {

		return new PrecursorType();
	}

	public ReferenceableParamGroupRefType createReferenceableParamGroupRefType() {

		return new ReferenceableParamGroupRefType();
	}

	public SoftwareRefType createSoftwareRefType() {

		return new SoftwareRefType();
	}

	public CVType createCVType() {

		return new CVType();
	}

	public SoftwareListType createSoftwareListType() {

		return new SoftwareListType();
	}

	public ScanWindowType createScanWindowType() {

		return new ScanWindowType();
	}

	public ProcessingMethodType createProcessingMethodType() {

		return new ProcessingMethodType();
	}

	public SampleType createSampleType() {

		return new SampleType();
	}

	public SpectrumType createSpectrumType() {

		return new SpectrumType();
	}

	public InstrumentConfigurationType createInstrumentConfigurationType() {

		return new InstrumentConfigurationType();
	}

	@XmlElementDecl(namespace = "http://psi.hupo.org/schema_revision/mzML_1.0.0", name = "mzML")
	public JAXBElement<MzMLType> createMzML(MzMLType value) {

		return new JAXBElement<>(_MzML_QNAME, MzMLType.class, null, value);
	}

	@XmlElementDecl(namespace = "http://psi.hupo.org/schema_revision/mzML_1.0.0", name = "detector", scope = ComponentListType.class)
	public JAXBElement<ComponentType> createComponentListTypeDetector(ComponentType value) {

		return new JAXBElement<>(_ComponentListTypeDetector_QNAME, ComponentType.class, ComponentListType.class, value);
	}

	@XmlElementDecl(namespace = "http://psi.hupo.org/schema_revision/mzML_1.0.0", name = "source", scope = ComponentListType.class)
	public JAXBElement<ComponentType> createComponentListTypeSource(ComponentType value) {

		return new JAXBElement<>(_ComponentListTypeSource_QNAME, ComponentType.class, ComponentListType.class, value);
	}

	@XmlElementDecl(namespace = "http://psi.hupo.org/schema_revision/mzML_1.0.0", name = "analyzer", scope = ComponentListType.class)
	public JAXBElement<ComponentType> createComponentListTypeAnalyzer(ComponentType value) {

		return new JAXBElement<>(_ComponentListTypeAnalyzer_QNAME, ComponentType.class, ComponentListType.class, value);
	}
}
