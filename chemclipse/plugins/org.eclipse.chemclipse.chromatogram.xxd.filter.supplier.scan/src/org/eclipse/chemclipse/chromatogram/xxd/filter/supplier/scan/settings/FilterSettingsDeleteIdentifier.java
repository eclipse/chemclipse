/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsDeleteIdentifier extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Delete Scan Target(s)", defaultValue = "false")
	@JsonPropertyDescription(value = "Confirm to delete the scan target(s).")
	private boolean deleteScanIdentifications;

	public boolean isDeleteScanIdentifications() {

		return deleteScanIdentifications;
	}

	public void setDeleteScanIdentifications(boolean deleteScanIdentifications) {

		this.deleteScanIdentifications = deleteScanIdentifications;
	}
}