/*******************************************************************************
 * Copyright (c) 2012, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extend configuration
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.settings;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.DefaultChromatogramReportSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ReportSettings3 extends DefaultChromatogramReportSettings {

	@JsonProperty(value = "Print Header", defaultValue = "true")
	@JsonPropertyDescription(value = "Include the header in the report.")
	private boolean printHeader = true;

	public boolean isPrintHeader() {

		return printHeader;
	}

	public void setPrintHeader(boolean printHeader) {

		this.printHeader = printHeader;
	}
}