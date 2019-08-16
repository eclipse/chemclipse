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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FileDescriptionType", propOrder = {"fileContent", "sourceFileList", "contact"})
public class FileDescriptionType {

	@XmlElement(required = true)
	private ParamGroupType fileContent;
	private SourceFileListType sourceFileList;
	private List<ParamGroupType> contact;

	public ParamGroupType getFileContent() {

		return fileContent;
	}

	public void setFileContent(ParamGroupType value) {

		this.fileContent = value;
	}

	public SourceFileListType getSourceFileList() {

		return sourceFileList;
	}

	public void setSourceFileList(SourceFileListType value) {

		this.sourceFileList = value;
	}

	public List<ParamGroupType> getContact() {

		if(contact == null) {
			contact = new ArrayList<ParamGroupType>();
		}
		return this.contact;
	}
}
