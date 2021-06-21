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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sourceFileType", propOrder = {"nameOfFile", "pathToFile", "fileType"})
public class SourceFileType {

	@XmlElement(required = true)
	protected String nameOfFile;
	@XmlElement(required = true)
	@XmlSchemaType(name = "anyURI")
	protected String pathToFile;
	protected String fileType;

	public String getNameOfFile() {

		return nameOfFile;
	}

	public void setNameOfFile(String value) {

		this.nameOfFile = value;
	}

	public String getPathToFile() {

		return pathToFile;
	}

	public void setPathToFile(String value) {

		this.pathToFile = value;
	}

	public String getFileType() {

		return fileType;
	}

	public void setFileType(String value) {

		this.fileType = value;
	}
}
