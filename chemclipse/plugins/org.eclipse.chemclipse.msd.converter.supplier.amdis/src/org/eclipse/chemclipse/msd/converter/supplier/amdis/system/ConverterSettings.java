/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.system;

import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.support.text.CharsetNIO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ConverterSettings implements ISystemProcessSettings {

	@JsonProperty(value = "Charset Import MSL", defaultValue = "US_ASCII")
	@JsonPropertyDescription(value = "The following charset is used to import the *.msl files.")
	private CharsetNIO charsetImportMSL = CharsetNIO.US_ASCII;
	@JsonProperty(value = "Charset Import MSP", defaultValue = "US_ASCII")
	@JsonPropertyDescription(value = "The following charset is used to import the *.msp files.")
	private CharsetNIO charsetImportMSP = CharsetNIO.US_ASCII;
	@JsonProperty(value = "Charset Import FIN", defaultValue = "US_ASCII")
	@JsonPropertyDescription(value = "The following charset is used to import the *.fin files.")
	private CharsetNIO charsetImportFIN = CharsetNIO.US_ASCII;
	@JsonProperty(value = "Charset Import ELU", defaultValue = "US_ASCII")
	@JsonPropertyDescription(value = "The following charset is used to import the *.elu files.")
	private CharsetNIO charsetImportELU = CharsetNIO.US_ASCII;

	public CharsetNIO getCharsetImportMSL() {

		return charsetImportMSL;
	}

	public void setCharsetImportMSL(CharsetNIO charsetImportMSL) {

		this.charsetImportMSL = charsetImportMSL;
	}

	public CharsetNIO getCharsetImportMSP() {

		return charsetImportMSP;
	}

	public void setCharsetImportMSP(CharsetNIO charsetImportMSP) {

		this.charsetImportMSP = charsetImportMSP;
	}

	public CharsetNIO getCharsetImportFIN() {

		return charsetImportFIN;
	}

	public void setCharsetImportFIN(CharsetNIO charsetImportFIN) {

		this.charsetImportFIN = charsetImportFIN;
	}

	public CharsetNIO getCharsetImportELU() {

		return charsetImportELU;
	}

	public void setCharsetImportELU(CharsetNIO charsetImportELU) {

		this.charsetImportELU = charsetImportELU;
	}
}