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
public class InstrumentParameter {

	@XmlElement(name = "slitWidth")
	private String slitWidth;
	@XmlElement(name = "spectralSlitWidth")
	private String spectralSlitWidth;
	@XmlElement(name = "beamChannel")
	private String beamChannel;
	@XmlElement(name = "sampleHolder")
	private String sampleHolder;
	@XmlElement(name = "samplePosition")
	private String samplePosition;
	@XmlElement(name = "scanSpeed")
	private String scanSpeed;
	@XmlElement(name = "pointSeparation")
	private String pointSeparation;
	@XmlElement(name = "comment")
	private String comment;

	public String getSlitWidth() {

		return slitWidth;
	}

	public void setSlitWidth(String slitWidth) {

		this.slitWidth = slitWidth;
	}

	public String getSpectralSlitWidth() {

		return spectralSlitWidth;
	}

	public void setSpectralSlitWidth(String spectralSlitWidth) {

		this.spectralSlitWidth = spectralSlitWidth;
	}

	public String getBeamChannel() {

		return beamChannel;
	}

	public void setBeamChannel(String beamChannel) {

		this.beamChannel = beamChannel;
	}

	public String getSampleHolder() {

		return sampleHolder;
	}

	public void setSampleHolder(String sampleHolder) {

		this.sampleHolder = sampleHolder;
	}

	public String getSamplePosition() {

		return samplePosition;
	}

	public void setSamplePosition(String samplePosition) {

		this.samplePosition = samplePosition;
	}

	public String getScanSpeed() {

		return scanSpeed;
	}

	public void setScanSpeed(String scanSpeed) {

		this.scanSpeed = scanSpeed;
	}

	public String getPointSeparation() {

		return pointSeparation;
	}

	public void setPointSeparation(String pointSeparation) {

		this.pointSeparation = pointSeparation;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}
