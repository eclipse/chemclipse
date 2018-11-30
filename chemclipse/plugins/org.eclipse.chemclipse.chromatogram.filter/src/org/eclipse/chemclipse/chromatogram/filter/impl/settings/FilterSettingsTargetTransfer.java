/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsTargetTransfer extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Transfer Closest Scan", defaultValue = "false")
	@JsonPropertyDescription(value = "If this value is true, only the closest scan to the peak maximum will be used to transfer targets.")
	private boolean transferClosestScan = false;

	public boolean isTransferClosestScan() {

		return transferClosestScan;
	}

	public void setTransferClosestScan(boolean transferClosestScan) {

		this.transferClosestScan = transferClosestScan;
	}
}
