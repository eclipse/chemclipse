/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.AbstractChromatogramCalculatorSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class CalculatorSettings extends AbstractChromatogramCalculatorSettings implements IRetentionIndexFilterSettings {

	@JsonProperty(value = "Retention Index Files", defaultValue = "")
	@JsonPropertyDescription("This is the list of RI files.")
	private List<String> retentionIndexFiles;

	@Override
	public List<String> getRetentionIndexFiles() {

		return retentionIndexFiles;
	}

	@Override
	public void setRetentionIndexFiles(List<String> retentionIndexFiles) {

		this.retentionIndexFiles = retentionIndexFiles;
	}
}
