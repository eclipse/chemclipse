/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.impl;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.AbstractBaselineDetectorSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeleteSettings extends AbstractBaselineDetectorSettings {

	@JsonProperty(value = "Delete Completely", defaultValue = "false")
	@JsonPropertyDescription(value = "If false, the baseline of the selected range will be deleted.")
	private boolean deleteCompletely;

	public boolean isDeleteCompletely() {

		return deleteCompletely;
	}

	public void setDeleteCompletely(boolean deleteCompletely) {

		this.deleteCompletely = deleteCompletely;
	}
}
