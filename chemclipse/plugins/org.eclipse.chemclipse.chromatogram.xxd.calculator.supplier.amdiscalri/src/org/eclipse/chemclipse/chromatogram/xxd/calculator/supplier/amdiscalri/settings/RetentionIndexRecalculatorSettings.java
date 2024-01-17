/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
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

public class RetentionIndexRecalculatorSettings {

	@JsonProperty(value = "Reference Chromatograms", defaultValue = "true")
	@JsonPropertyDescription("Process all referenced chromatograms.")
	private boolean processReferenceChromatograms = true;

	public boolean isProcessReferenceChromatograms() {

		return processReferenceChromatograms;
	}

	public void setProcessReferenceChromatograms(boolean processReferenceChromatograms) {

		this.processReferenceChromatograms = processReferenceChromatograms;
	}
}