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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v11.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * This step allows to pause at a certain temperature. It is typically the
 * last step in an amplification protocol.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pauseType", propOrder = {"temperature"})
public class PauseType {

	protected float temperature;

	public float getTemperature() {

		return temperature;
	}

	public void setTemperature(float value) {

		this.temperature = value;
	}
}
