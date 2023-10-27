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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v10.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Due to the high numbers of this element, names are kept short.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dpMeltingCurveType", propOrder = {"tmp", "fluor"})
public class DpMeltingCurveType {

	protected float tmp;
	protected float fluor;

	public float getTmp() {

		return tmp;
	}

	public void setTmp(float value) {

		this.tmp = value;
	}

	public float getFluor() {

		return fluor;
	}

	public void setFluor(float value) {

		this.fluor = value;
	}
}
