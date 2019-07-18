/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsRemover extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Scan Remover Pattern", defaultValue = PreferenceSupplier.DEF_REMOVER_PATTERN)
	@JsonPropertyDescription(value = "The pattern, which is used to remove scans.")
	@StringSettingsProperty(regExp = PreferenceSupplier.CHECK_REMOVER_PATTERM)
	private String scanRemoverPattern = PreferenceSupplier.DEF_REMOVER_PATTERN;

	@Override
	public void setSystemSettings() {

		scanRemoverPattern = PreferenceSupplier.getScanRemoverPattern();
	}

	public String getScanRemoverPattern() {

		return scanRemoverPattern;
	}

	public void setScanRemoverPattern(String scanRemoverPattern) {

		if(scanRemoverPattern != null) {
			this.scanRemoverPattern = scanRemoverPattern;
		}
	}
}
