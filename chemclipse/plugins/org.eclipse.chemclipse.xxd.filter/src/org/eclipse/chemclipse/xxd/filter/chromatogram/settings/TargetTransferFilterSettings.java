/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.chromatogram.settings;

import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.xxd.filter.preferences.PreferenceSupplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class TargetTransferFilterSettings {

	@JsonProperty(value = "Limit Match Factor", defaultValue = "80.0")
	@JsonPropertyDescription(value = "Run an identification if no target exists with a Match Factor >= the given limit.")
	@FloatSettingsProperty(minValue = IIdentifierSettings.MIN_LIMIT_MATCH_FACTOR, maxValue = IIdentifierSettings.MAX_LIMIT_MATCH_FACTOR)
	private float limitMatchFactor = 80.0f;
	@JsonProperty(value = "Transfer Best Target Only", defaultValue = "false")
	@JsonPropertyDescription(value = "If this value is true, only the best target will be transfered.")
	private boolean useBestTargetOnly = false;
	@JsonProperty(value = "Match Quality", defaultValue = "80.0")
	@JsonPropertyDescription(value = "The match quality is set as the Match Factor.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float matchQuality = 80.0f;

	public float getLimitMatchFactor() {

		return limitMatchFactor;
	}

	public void setLimitMatchFactor(float limitMatchFactor) {

		this.limitMatchFactor = limitMatchFactor;
	}

	public boolean isUseBestTargetOnly() {

		return useBestTargetOnly;
	}

	public void setUseBestTargetOnly(boolean useBestTargetOnly) {

		this.useBestTargetOnly = useBestTargetOnly;
	}

	public float getMatchQuality() {

		return matchQuality;
	}

	public void setMatchQuality(float matchQuality) {

		this.matchQuality = matchQuality;
	}
}