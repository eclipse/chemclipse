/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Subtract Mass Spectrum", defaultValue = "18:200;28:1000;32:500")
	@JsonPropertyDescription(value = "This is the mass spectrum used for subtraction.")
	private String subtractMassSpectrum = "18:200;28:1000;32:500";
	@JsonProperty(value = "Use Nominal Mass", defaultValue = "true")
	@JsonPropertyDescription(value = "Use the nominal mass schema.")
	private boolean useNominalMasses = true;
	@JsonProperty(value = "Normalize Data", defaultValue = "true")
	@JsonPropertyDescription(value = "Normalize the intensities.")
	private boolean useNormalize = true;

	public void setSubtractMassSpectrum(String subtractMassSpectrum) {

		this.subtractMassSpectrum = subtractMassSpectrum;
	}

	public IScanMSD getSubtractMassSpectrum() {

		return org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getMassSpectrum(subtractMassSpectrum);
	}

	public boolean isUseNominalMasses() {

		return useNominalMasses;
	}

	public void setUseNominalMasses(boolean useNominalMasses) {

		this.useNominalMasses = useNominalMasses;
	}

	public boolean isUseNormalize() {

		return useNormalize;
	}

	public void setUseNormalize(boolean useNormalize) {

		this.useNormalize = useNormalize;
	}
}
