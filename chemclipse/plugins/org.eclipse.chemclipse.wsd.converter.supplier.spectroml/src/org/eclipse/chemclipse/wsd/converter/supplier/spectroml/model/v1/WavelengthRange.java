/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
class WavelengthRange {

	@XmlElement(name = "minimum")
	private Double minimum;
	@XmlElement(name = "maximum")
	private Double maximum;

	public Double getMinimum() {

		return minimum;
	}

	public void setMinimum(Double minimum) {

		this.minimum = minimum;
	}

	public Double getMaximum() {

		return maximum;
	}

	public void setMaximum(Double maximum) {

		this.maximum = maximum;
	}
}