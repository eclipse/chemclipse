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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "supDescType", propOrder = {"supDataDesc", "sourceFile"})
public class SupDescType {

	protected DescriptionType supDataDesc;
	protected List<SourceFileType> sourceFile;
	@XmlAttribute(name = "supDataArrayRef", required = true)
	protected int supDataArrayRef;

	public DescriptionType getSupDataDesc() {

		return supDataDesc;
	}

	public void setSupDataDesc(DescriptionType value) {

		this.supDataDesc = value;
	}

	public List<SourceFileType> getSourceFile() {

		if(sourceFile == null) {
			sourceFile = new ArrayList<SourceFileType>();
		}
		return this.sourceFile;
	}

	public int getSupDataArrayRef() {

		return supDataArrayRef;
	}

	public void setSupDataArrayRef(int value) {

		this.supDataArrayRef = value;
	}
}
