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

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScanWindowListType", propOrder = {"scanWindow"})
public class ScanWindowListType {

	@XmlElement(required = true)
	protected List<ScanWindowType> scanWindow;
	@XmlAttribute(name = "count", required = true)
	protected int count;

	public List<ScanWindowType> getScanWindow() {

		if(scanWindow == null) {
			scanWindow = new ArrayList<>();
		}
		return this.scanWindow;
	}

	public int getCount() {

		return count;
	}

	public void setCount(int value) {

		this.count = value;
	}
}
