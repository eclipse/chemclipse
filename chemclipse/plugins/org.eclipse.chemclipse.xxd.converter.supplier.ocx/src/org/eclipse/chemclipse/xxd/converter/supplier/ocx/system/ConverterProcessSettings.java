/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.system;

import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.versions.ChromatogramVersion;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.versions.MethodVersion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ConverterProcessSettings implements ISystemProcessSettings {

	@JsonProperty(value = "Version Chromatogram (*.ocb)", defaultValue = "V_1501")
	@JsonPropertyDescription(value = "Defines the version to store the chromatogram data.")
	private ChromatogramVersion chromatogramVersion = ChromatogramVersion.V_1501;
	@JsonProperty(value = "Compression Level Chromatogram (*.ocb)", defaultValue = "0")
	@JsonPropertyDescription(value = "Compression level for the chromatogram, 0 = off, 9 = best.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_COMPRESSION_LEVEL, maxValue = PreferenceSupplier.MAX_COMPRESSION_LEVEL)
	private int chromatogramCompressionLevel = 0;
	@JsonProperty(value = "Export References Separately", defaultValue = "false")
	@JsonPropertyDescription(value = "Export all reference chromatograms into a separate file.")
	private boolean exportReferencesSeparately = false;
	@JsonProperty(value = "Header Field (Export References)", defaultValue = "DATA_NAME")
	@JsonPropertyDescription(value = "Use the data from the given header field to mark the exported references.")
	private HeaderField headerFieldReferencesExport = HeaderField.DATA_NAME;
	//
	@JsonProperty(value = "Version Method (*.ocm)", defaultValue = "V_1401")
	@JsonPropertyDescription(value = "Defines the version to store the method data.")
	private MethodVersion methodVersion = MethodVersion.V_1401;
	@JsonProperty(value = "Compression Level Method (*.ocm)", defaultValue = "0")
	@JsonPropertyDescription(value = "Compression level for the method, 0 = off, 9 = best.")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_COMPRESSION_LEVEL, maxValue = PreferenceSupplier.MAX_COMPRESSION_LEVEL)
	private int methodCompressionLevel = 0;

	public ChromatogramVersion getChromatogramVersion() {

		return chromatogramVersion;
	}

	public void setChromatogramVersion(ChromatogramVersion chromatogramVersion) {

		this.chromatogramVersion = chromatogramVersion;
	}

	public int getChromatogramCompressionLevel() {

		return chromatogramCompressionLevel;
	}

	public void setChromatogramCompressionLevel(int chromatogramCompressionLevel) {

		this.chromatogramCompressionLevel = chromatogramCompressionLevel;
	}

	public boolean isExportReferencesSeparately() {

		return exportReferencesSeparately;
	}

	public void setExportReferencesSeparately(boolean exportReferencesSeparately) {

		this.exportReferencesSeparately = exportReferencesSeparately;
	}

	public HeaderField getHeaderFieldReferencesExport() {

		return headerFieldReferencesExport;
	}

	public void setHeaderFieldReferencesExport(HeaderField headerFieldReferencesExport) {

		this.headerFieldReferencesExport = headerFieldReferencesExport;
	}

	public MethodVersion getMethodVersion() {

		return methodVersion;
	}

	public void setMethodVersion(MethodVersion methodVersion) {

		this.methodVersion = methodVersion;
	}

	public int getMethodCompressionLevel() {

		return methodCompressionLevel;
	}

	public void setMethodCompressionLevel(int methodCompressionLevel) {

		this.methodCompressionLevel = methodCompressionLevel;
	}
}