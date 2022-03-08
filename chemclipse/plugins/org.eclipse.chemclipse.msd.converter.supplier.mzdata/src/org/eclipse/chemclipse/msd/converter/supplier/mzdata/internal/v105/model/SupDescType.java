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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "supDescType", propOrder = {"supDataDesc", "supSourceFile"})
public class SupDescType {

	private DescriptionType supDataDesc;
	private List<SourceFileType> supSourceFile;
	@XmlAttribute(name = "supDataArrayRef", required = true)
	private int supDataArrayRef;

	public DescriptionType getSupDataDesc() {

		return supDataDesc;
	}

	public void setSupDataDesc(DescriptionType value) {

		this.supDataDesc = value;
	}

	public List<SourceFileType> getSupSourceFile() {

		if(supSourceFile == null) {
			supSourceFile = new ArrayList<SourceFileType>();
		}
		return this.supSourceFile;
	}

	public int getSupDataArrayRef() {

		return supDataArrayRef;
	}

	public void setSupDataArrayRef(int value) {

		this.supDataArrayRef = value;
	}
}
