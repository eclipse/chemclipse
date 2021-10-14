/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - list ions to integrate
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AbstractPeakIntegrationSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.util.IonSettingUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakIntegrationSettings extends AbstractPeakIntegrationSettings {

	@JsonProperty(value = "Ions to integrate", defaultValue = "0")
	@JsonPropertyDescription(value = "List the ions to integrate, separated by a white space. 0 = TIC")
	@StringSettingsProperty(regExp = "(\\d+[;|\\s]?)+", isMultiLine = false)
	private String ionsToIntegrate = "0";

	@Override
	public IMarkedIons getSelectedIons() {

		IonSettingUtil ionSettingUtil = new IonSettingUtil();
		int[] ions = ionSettingUtil.extractIons(ionSettingUtil.deserialize(ionsToIntegrate));
		return new MarkedIons(ions, IMarkedIons.IonMarkMode.INCLUDE);
	}

	public void setSelectedIon(String ionsToIntegrate) {

		this.ionsToIntegrate = ionsToIntegrate;
	}

	@JsonProperty(value = "Include Background", defaultValue = "false")
	@JsonPropertyDescription(value = "This value should be false. If true, the complete background is included.")
	private boolean includeBackground = false;

	public boolean isIncludeBackground() {

		return includeBackground;
	}

	public void setIncludeBackground(boolean includeBackground) {

		this.includeBackground = includeBackground;
	}
}