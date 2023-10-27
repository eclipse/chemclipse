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

import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * This step allows to form a loop or to exclude some steps. It allows to
 * jump to a certain "goto" step for "repeat" times. If the "goto" step is
 * higher than the step of the loop, "repeat" must be "0".
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loopType", propOrder = {"gotoStep", "repeat"})
public class LoopType {

	@XmlElement(name = "goto", required = true)
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger gotoStep;
	@XmlElement(required = true)
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger repeat;

	public BigInteger getGoto() {

		return gotoStep;
	}

	public void setGoto(BigInteger value) {

		this.gotoStep = value;
	}

	public BigInteger getRepeat() {

		return repeat;
	}

	public void setRepeat(BigInteger value) {

		this.repeat = value;
	}
}
