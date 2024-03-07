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
public class MeasurementCorrection {

	@XmlElement(name = "qualificationTimeStamp")
	private TimeStamp qualificationTimeStamp;
	@XmlElement(name = "qualificationReference")
	private String qualificationReference;
	@XmlElement(name = "proficiencyTimeStamp")
	private TimeStamp proficiencyTimeStamp;
	@XmlElement(name = "proficiencyReference")
	private String proficiencyReference;
	@XmlElement(name = "transmittanceTimeStamp")
	private TimeStamp transmittanceTimeStamp;
	@XmlElement(name = "transmittanceReference")
	private String transmittanceReference;
	@XmlElement(name = "wavelengthTimeStamp")
	private TimeStamp wavelengthTimeStamp;
	@XmlElement(name = "wavelengthReference")
	private String wavelengthReference;
	@XmlElement(name = "comment")
	private String comment;
}
