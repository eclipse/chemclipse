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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * This is the root element for the Proteomics Standards Initiative (PSI) mzML schema, which is intended to capture the use of a mass spectrometer, the data generated, and the initial processing of that data (to the level of the peak list).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cvList", "fileDescription", "referenceableParamGroupList", "sampleList", "instrumentConfigurationList", "softwareList", "dataProcessingList", "acquisitionSettingsList", "run"})
@XmlRootElement(name = "mzML")
public class MzML {

	@XmlElement(required = true)
	protected CVListType cvList;
	@XmlElement(required = true)
	protected FileDescriptionType fileDescription;
	protected ReferenceableParamGroupListType referenceableParamGroupList;
	protected SampleListType sampleList;
	@XmlElement(required = true)
	protected InstrumentConfigurationListType instrumentConfigurationList;
	@XmlElement(required = true)
	protected SoftwareListType softwareList;
	@XmlElement(required = true)
	protected DataProcessingListType dataProcessingList;
	protected AcquisitionSettingsListType acquisitionSettingsList;
	@XmlElement(required = true)
	protected RunType run;
	@XmlAttribute(name = "accession")
	protected String accession;
	@XmlAttribute(name = "version", required = true)
	protected String version;
	@XmlAttribute(name = "id")
	protected String id;

	public CVListType getCvList() {

		return cvList;
	}

	public void setCvList(CVListType value) {

		this.cvList = value;
	}

	public FileDescriptionType getFileDescription() {

		return fileDescription;
	}

	public void setFileDescription(FileDescriptionType value) {

		this.fileDescription = value;
	}

	public ReferenceableParamGroupListType getReferenceableParamGroupList() {

		return referenceableParamGroupList;
	}

	public void setReferenceableParamGroupList(ReferenceableParamGroupListType value) {

		this.referenceableParamGroupList = value;
	}

	public SampleListType getSampleList() {

		return sampleList;
	}

	public void setSampleList(SampleListType value) {

		this.sampleList = value;
	}

	public InstrumentConfigurationListType getInstrumentConfigurationList() {

		return instrumentConfigurationList;
	}

	public void setInstrumentConfigurationList(InstrumentConfigurationListType value) {

		this.instrumentConfigurationList = value;
	}

	public SoftwareListType getSoftwareList() {

		return softwareList;
	}

	public void setSoftwareList(SoftwareListType value) {

		this.softwareList = value;
	}

	public DataProcessingListType getDataProcessingList() {

		return dataProcessingList;
	}

	public void setDataProcessingList(DataProcessingListType value) {

		this.dataProcessingList = value;
	}

	public AcquisitionSettingsListType getAcquisitionSettingsList() {

		return acquisitionSettingsList;
	}

	public void setAcquisitionSettingsList(AcquisitionSettingsListType value) {

		this.acquisitionSettingsList = value;
	}

	public RunType getRun() {

		return run;
	}

	public void setRun(RunType value) {

		this.run = value;
	}

	public String getAccession() {

		return accession;
	}

	public void setAccession(String value) {

		this.accession = value;
	}

	public String getVersion() {

		return version;
	}

	public void setVersion(String value) {

		this.version = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}
}
