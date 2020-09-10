/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Default settings class, which sets all identifier settings values
 * to their defaults. Additionally, no JsonAnnotations are declared, so
 * that each identifier settings class, which don't need the underlying
 * settings, can re-use this default class.
 */
public abstract class IdentifierAdapterSettings extends AbstractProcessSettings implements IIdentifierSettings {

	@JsonIgnore
	private PenaltyCalculation penaltyCalculation = PenaltyCalculation.NONE;
	@JsonIgnore
	private int retentionTimeWindow = 2000;
	@JsonIgnore
	private float retentionIndexWindow = 20.0f;
	@JsonIgnore
	private float penaltyCalculationLevelFactor = IIdentifierSettings.DEF_PENALTY_CALCULATION_LEVEL_FACTOR;
	@JsonIgnore
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
