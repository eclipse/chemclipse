/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeletePeaksFilterSettings {

	@JsonProperty(value = "Delete Peak(s)", defaultValue = "false")
	@JsonPropertyDescription(value = "Confirm to delete the peak(s).")
	private boolean deletePeaks;
	@JsonProperty(value = "Unidentified Only", defaultValue = "false")
	@JsonPropertyDescription(value = "Only delete the unidentified peak(s).")
	private boolean deleteUnidentifiedOnly;

	public boolean isDeletePeaks() {

		return deletePeaks;
	}

	public void setDeletePeaks(boolean deletePeaks) {

		this.deletePeaks = deletePeaks;
	}

	public boolean isDeleteUnidentifiedOnly() {

		return deleteUnidentifiedOnly;
	}

	public void setDeleteUnidentifiedOnly(boolean deleteUnidentifiedOnly) {

		this.deleteUnidentifiedOnly = deleteUnidentifiedOnly;
	}
}