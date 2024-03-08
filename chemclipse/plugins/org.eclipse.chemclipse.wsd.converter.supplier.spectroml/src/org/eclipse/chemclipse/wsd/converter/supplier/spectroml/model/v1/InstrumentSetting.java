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
public class InstrumentSetting {

	@XmlElement(name = "resolution")
	private String resolution;
	@XmlElement(name = "linearDispersion")
	private String linearDispersion;
	@XmlElement(name = "spectralBandWidthRange")
	private SpectralBandWidthRange spectralBandWidthRange;
	@XmlElement(name = "wavelengthRange")
	private WavelengthRange wavelengthRange;
	@XmlElement(name = "absorbanceRange")
	private AbsorbanceRange absorbanceRange;
	@XmlElement(name = "detectorTypes")
	private String detectorTypes;
	@XmlElement(name = "sourceTypes")
	private String sourceTypes;
	@XmlElement(name = "comment")
	private String comment;

	public String getResolution() {

		return resolution;
	}

	public void setResolution(String resolution) {

		this.resolution = resolution;
	}

	public String getLinearDispersion() {

		return linearDispersion;
	}

	public void setLinearDispersion(String linearDispersion) {

		this.linearDispersion = linearDispersion;
	}

	public SpectralBandWidthRange getSpectralBandWidthRange() {

		return spectralBandWidthRange;
	}

	public void setSpectralBandWidthRange(SpectralBandWidthRange spectralBandWidthRange) {

		this.spectralBandWidthRange = spectralBandWidthRange;
	}

	public WavelengthRange getWavelengthRange() {

		return wavelengthRange;
	}

	public void setWavelengthRange(WavelengthRange wavelengthRange) {

		this.wavelengthRange = wavelengthRange;
	}

	public AbsorbanceRange getAbsorbanceRange() {

		return absorbanceRange;
	}

	public void setAbsorbanceRange(AbsorbanceRange absorbanceRange) {

		this.absorbanceRange = absorbanceRange;
	}

	public String getDetectorTypes() {

		return detectorTypes;
	}

	public void setDetectorTypes(String detectorTypes) {

		this.detectorTypes = detectorTypes;
	}

	public String getSourceTypes() {

		return sourceTypes;
	}

	public void setSourceTypes(String sourceTypes) {

		this.sourceTypes = sourceTypes;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}