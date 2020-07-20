/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PeakDetectorELUSettings extends PeakDetectorSettings {

	@JsonProperty(value = "Path of the ELU file.", defaultValue = "")
	private String pathFileELU = "";

	public String getPathFileELU() {

		return pathFileELU;
	}

	public void setPathFileELU(String pathFileELU) {

		this.pathFileELU = pathFileELU;
	}
}