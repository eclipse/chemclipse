/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.system;

import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SettingsRetentionIndexQC implements ISystemProcessSettings {

	@JsonProperty(value = "QC: Use Retention Index", defaultValue = "false")
	@JsonPropertyDescription(value = "Set this system value to use retention indices for quality control purposes.")
	private boolean useRetentionIndexQC = false;

	public boolean isUseRetentionIndexQC() {

		return useRetentionIndexQC;
	}

	public void setUseRetentionIndexQC(boolean useRetentionIndexQC) {

		this.useRetentionIndexQC = useRetentionIndexQC;
	}
}