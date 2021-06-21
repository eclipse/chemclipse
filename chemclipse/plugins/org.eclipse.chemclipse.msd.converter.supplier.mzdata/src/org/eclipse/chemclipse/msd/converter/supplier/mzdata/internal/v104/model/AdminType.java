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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminType", propOrder = {"sampleName", "sampleDescription", "sourceFile", "contact"})
public class AdminType {

	@XmlElement(required = true)
	protected String sampleName;
	protected DescriptionType sampleDescription;
	protected SourceFileType sourceFile;
	@XmlElement(required = true)
	protected List<PersonType> contact;

	public String getSampleName() {

		return sampleName;
	}

	public void setSampleName(String value) {

		this.sampleName = value;
	}

	public DescriptionType getSampleDescription() {

		return sampleDescription;
	}

	public void setSampleDescription(DescriptionType value) {

		this.sampleDescription = value;
	}

	public SourceFileType getSourceFile() {

		return sourceFile;
	}

	public void setSourceFile(SourceFileType value) {

		this.sourceFile = value;
	}

	public List<PersonType> getContact() {

		if(contact == null) {
			contact = new ArrayList<PersonType>();
		}
		return this.contact;
	}
}
