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
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.util.IonSettingUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakIntegrationSettings extends AbstractPeakIntegrationSettings {

	@JsonIgnore
	private static final String TIC = "0";
	//
	@JsonProperty(value = "Ions to integrate", defaultValue = TIC)
	@JsonPropertyDescription(value = "List the ions to integrate, separated by a white space. 0 = TIC")
	@StringSettingsProperty(regExp = "(\\d+[;|\\s]?)+", isMultiLine = false)
	private String ionsToIntegrate = TIC;
	/*
	 * The selected ions are handled separately.
	 * They must not be persisted. If selected ions is
	 * empty, TIC will be integrated.
	 */
	@JsonIgnore
	private IMarkedIons selectedIons = null;

	@Override
	public IMarkedIons getSelectedIons() {

		if(selectedIons == null) {
			selectedIons = super.getSelectedIons();
			if(!ionsToIntegrate.equals(TIC)) {
				IonSettingUtil ionSettingUtil = new IonSettingUtil();
				int[] ions = ionSettingUtil.extractIons(ionSettingUtil.deserialize(ionsToIntegrate));
				for(int ion : ions) {
					selectedIons.add(new MarkedIon(ion));
				}
			}
		}
		//
		return selectedIons;
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