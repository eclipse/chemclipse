/*******************************************************************************
 * Copyright (c) 2015, 2021 Lablicate GmbH.
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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cvList", "fileDescription", "referenceableParamGroupList", "sampleList", "softwareList", "scanSettingsList", "instrumentConfigurationList", "dataProcessingList", "run"})
@XmlRootElement(name = "mzML") // namespace = "http://psi.hupo.org/ms/mzml"
public class MzML implements Serializable {

	private final static long serialVersionUID = 110L;
	@XmlElement(required = true)
	private CVListType cvList;
	@XmlElement(required = true)
	private FileDescriptionType fileDescription;
	private ReferenceableParamGroupListType referenceableParamGroupList;
	private SampleListType sampleList;
	@XmlElement(required = true)
	private SoftwareListType softwareList;
	private ScanSettingsListType scanSettingsList;
	@XmlElement(required = true)
	private InstrumentConfigurationListType instrumentConfigurationList;
	@XmlElement(required = true)
	private DataProcessingListType dataProcessingList;
	@XmlElement(required = true)
	private RunType run;
	@XmlAttribute(name = "accession")
	private String accession;
	@XmlAttribute(name = "version", required = true)
	private String version;
	@XmlAttribute(name = "id")
	private String id;

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

	public SoftwareListType getSoftwareList() {

		return softwareList;
	}

	public void setSoftwareList(SoftwareListType value) {

		this.softwareList = value;
	}

	public ScanSettingsListType getScanSettingsList() {

		return scanSettingsList;
	}

	public void setScanSettingsList(ScanSettingsListType value) {

		this.scanSettingsList = value;
	}

	public InstrumentConfigurationListType getInstrumentConfigurationList() {

		return instrumentConfigurationList;
	}

	public void setInstrumentConfigurationList(InstrumentConfigurationListType value) {

		this.instrumentConfigurationList = value;
	}

	public DataProcessingListType getDataProcessingList() {

		return dataProcessingList;
	}

	public void setDataProcessingList(DataProcessingListType value) {

		this.dataProcessingList = value;
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
