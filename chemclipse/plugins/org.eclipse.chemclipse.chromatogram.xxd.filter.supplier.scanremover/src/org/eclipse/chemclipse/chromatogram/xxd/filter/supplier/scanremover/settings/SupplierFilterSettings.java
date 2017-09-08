/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SupplierFilterSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterSettings {

	@JsonProperty(value = "Scan Remover Pattern", defaultValue = "XO")
	@JsonPropertyDescription(value = "The pattern which is used to remove scans.")
	private String scanRemoverPattern;

	@Override
	public String getScanRemoverPattern() {

		return scanRemoverPattern;
	}

	@Override
	public void setScanRemoverPattern(String scanRemoverPattern) {

		if(scanRemoverPattern != null) {
			this.scanRemoverPattern = scanRemoverPattern;
		}
	}
}
