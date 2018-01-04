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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v31.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class ParentFile implements Serializable {

	private final static long serialVersionUID = 310L;
	@XmlValue
	@XmlSchemaType(name = "anySimpleType")
	private Object value;
	@XmlAttribute(name = "fileName", required = true)
	@XmlSchemaType(name = "anyURI")
	private String fileName;
	@XmlAttribute(name = "fileType", required = true)
	private String fileType;
	@XmlAttribute(name = "fileSha1", required = true)
	private String fileSha1;

	public Object getValue() {

		return value;
	}

	public void setValue(Object value) {

		this.value = value;
	}

	public String getFileName() {

		return fileName;
	}

	public void setFileName(String value) {

		this.fileName = value;
	}

	public String getFileType() {

		return fileType;
	}

	public void setFileType(String value) {

		this.fileType = value;
	}

	public String getFileSha1() {

		return fileSha1;
	}

	public void setFileSha1(String value) {

		this.fileSha1 = value;
	}
}
