/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class IndexCuratorSettings extends IndexExportSettings {

	@JsonProperty(value = "Extrapolate (Left)", defaultValue = "false")
	@JsonPropertyDescription("Extrapolate the index schema (left) to the start of the chromatogram.")
	private boolean extrapolateLeft = false;
	@JsonProperty(value = "Extrapolate (Right)", defaultValue = "false")
	@JsonPropertyDescription("Extrapolate the index schema (right) to the end of the chromatogram.")
	private boolean extrapolateRight = false;
	@JsonProperty(value = "Store In Chromatogram", defaultValue = "true")
	@JsonPropertyDescription("Store the retention index marker in the chromatogram.")
	private boolean storeInChromatogram = true;
	@JsonProperty(value = "Reference Chromatograms", defaultValue = "true")
	@JsonPropertyDescription("Process all referenced chromatograms.")
	private boolean processReferenceChromatograms = true;

	public boolean isExtrapolateLeft() {

		return extrapolateLeft;
	}

	public void setExtrapolateLeft(boolean extrapolateLeft) {

		this.extrapolateLeft = extrapolateLeft;
	}

	public boolean isExtrapolateRight() {

		return extrapolateRight;
	}

	public void setExtrapolateRight(boolean extrapolateRight) {

		this.extrapolateRight = extrapolateRight;
	}

	public boolean isStoreInChromatogram() {

		return storeInChromatogram;
	}

	public void setStoreInChromatogram(boolean storeInChromatogram) {

		this.storeInChromatogram = storeInChromatogram;
	}

	public boolean isProcessReferenceChromatograms() {

		return processReferenceChromatograms;
	}

	public void setProcessReferenceChromatograms(boolean processReferenceChromatograms) {

		this.processReferenceChromatograms = processReferenceChromatograms;
	}
}