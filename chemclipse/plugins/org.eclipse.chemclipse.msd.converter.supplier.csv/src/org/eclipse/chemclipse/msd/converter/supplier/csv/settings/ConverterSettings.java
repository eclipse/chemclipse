/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.settings;

import org.eclipse.chemclipse.model.settings.Delimiter;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ConverterSettings implements ISystemProcessSettings {

	@JsonProperty(value = "Import Delimiter", defaultValue = "COMMA")
	@JsonPropertyDescription(value = "Select the column delimiter.")
	private Delimiter importDelimiter = Delimiter.COMMA;
	@JsonProperty(value = "Import Zero Marker", defaultValue = "0.0")
	@JsonPropertyDescription(value = "Define the zero marker to detect 0 values.")
	@StringSettingsProperty(regExp = "(0\\.)(\\d+)", description = "0.0 is the minimum marker.")
	private String importZeroMarker = "0.0";
	@JsonProperty(value = "Export Use TIC", defaultValue = "false")
	@JsonPropertyDescription(value = "Export only TIC values.")
	private boolean exportUseTic = false;

	public Delimiter getImportDelimiter() {

		return importDelimiter;
	}

	public void setImportDelimiter(Delimiter importDelimiter) {

		this.importDelimiter = importDelimiter;
	}

	public String getImportZeroMarker() {

		return importZeroMarker;
	}

	public void setImportZeroMarker(String importZeroMarker) {

		this.importZeroMarker = importZeroMarker;
	}

	public boolean isExportUseTic() {

		return exportUseTic;
	}

	public void setExportUseTic(boolean exportUseTic) {

		this.exportUseTic = exportUseTic;
	}
}