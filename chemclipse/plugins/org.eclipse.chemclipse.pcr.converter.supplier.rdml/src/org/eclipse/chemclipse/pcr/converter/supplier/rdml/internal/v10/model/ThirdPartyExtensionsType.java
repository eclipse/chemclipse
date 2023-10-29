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

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * This is the place to insert extensions not supported by RDML. Please use
 * a descriptive and unique root element to avoid interference with other
 * third party extensions.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "thirdPartyExtensionsType", propOrder = {"any"})
public class ThirdPartyExtensionsType {

	@XmlAnyElement(lax = true)
	protected List<Object> any;

	public List<Object> getAny() {

		if(any == null) {
			any = new ArrayList<>();
		}
		return this.any;
	}
}
