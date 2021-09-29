/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.settings.AbstractProcessSettings;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class AbstractIdentifierSettings extends AbstractProcessSettings implements IIdentifierSettings {

	@JsonProperty(value = "Penalty Calculation", defaultValue = "NONE")
	@JsonPropertyDescription(value = "Select the strategy, how penalties are calculated.")
	private PenaltyCalculation penaltyCalculation = PenaltyCalculation.NONE;
	@JsonProperty(value = "Retention Time Window [ms]", defaultValue = "2000")
	@JsonPropertyDescription(value = "The retention time window, given in milliseconds.")
	@IntSettingsProperty(minValue = 0, maxValue = Integer.MAX_VALUE)
	private int retentionTimeWindow = 2000;
	@JsonProperty(value = "Retention Index Window", defaultValue = "20")
	@JsonPropertyDescription(value = "The retention index window.")
	@FloatSettingsProperty(minValue = 0.0f, maxValue = Float.MAX_VALUE)
	private float retentionIndexWindow = 20.0f;
	@JsonProperty(value = "Penalty Calculation Level Factor", defaultValue = "5.0")
	@JsonPropertyDescription(value = "The penalty calculation level factor.")
	@FloatSettingsProperty(minValue = IIdentifierSettings.MIN_PENALTY_CALCULATION_LEVEL_FACTOR, maxValue = IIdentifierSettings.MAX_PENALTY_CALCULATION_LEVEL_FACTOR)
	private float penaltyCalculationLevelFactor = IIdentifierSettings.DEF_PENALTY_CALCULATION_LEVEL_FACTOR;
	@JsonProperty(value = "Max Penalty", defaultValue = "20")
	@JsonPropertyDescription(value = "The max penalty. Values between 0 (no penalty) and 100 (max penalty) are allowed.")
	@FloatSettingsProperty(minValue = 0.0f, maxValue = 100.0f)
	private float maxPenalty = 20.0f;
	//
	@JsonIgnore
	private boolean setResultAutomatically;

	@Override
	public boolean isSetResultAutomatically() {

		return setResultAutomatically;
	}

	@Override
	public void setSetResultAutomatically(boolean setResultAutomatically) {

		this.setResultAutomatically = setResultAutomatically;
	}

	@Override
	public PenaltyCalculation getPenaltyCalculation() {

		return penaltyCalculation;
	}

	@Override
	public void setPenaltyCalculation(PenaltyCalculation penaltyCalculation) {

		this.penaltyCalculation = penaltyCalculation;
	}

	@Override
	public int getRetentionTimeWindow() {

		return retentionTimeWindow;
	}

	@Override
	public void setRetentionTimeWindow(int retentionTimeWindow) {

		this.retentionTimeWindow = retentionTimeWindow;
	}

	@Override
	public float getRetentionIndexWindow() {

		return retentionIndexWindow;
	}

	@Override
	public void setRetentionIndexWindow(float retentionIndexWindow) {

		this.retentionIndexWindow = retentionIndexWindow;
	}

	@Override
	public float getPenaltyCalculationLevelFactor() {

		return penaltyCalculationLevelFactor;
	}

	@Override
	public void setPenaltyCalculationLevelFactor(float penaltyCalculationLevelFactor) {

		this.penaltyCalculationLevelFactor = penaltyCalculationLevelFactor;
	}

	@Override
	public float getMaxPenalty() {

		return maxPenalty;
	}

	@Override
	public void setMaxPenalty(float maxPenalty) {

		this.maxPenalty = maxPenalty;
	}
}
