/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

	/*
	 * Delta Calculation
	 */
	@JsonIgnore
	private DeltaCalculation deltaCalculation = DeltaCalculation.NONE;
	@JsonIgnore
	private float deltaWindow = 0.0f;
	/*
	 * Penalty Calculation
	 */
	@JsonIgnore
	private PenaltyCalculation penaltyCalculation = PenaltyCalculation.NONE;
	@JsonIgnore
	private float penaltyWindow = 0.0f;
	@JsonIgnore
	private float penaltyLevelFactor = IIdentifierSettings.DEF_PENALTY_LEVEL_FACTOR;
	@JsonIgnore
	private float maxPenalty = IIdentifierSettings.DEF_PENALTY_MATCH_FACTOR;
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
	public DeltaCalculation getDeltaCalculation() {

		return deltaCalculation;
	}

	@Override
	public void setDeltaCalculation(DeltaCalculation deltaCalculation) {

		this.deltaCalculation = deltaCalculation;
	}

	@Override
	public float getDeltaWindow() {

		return deltaWindow;
	}

	@Override
	public void setDeltaWindow(float deltaWindow) {

		this.deltaWindow = deltaWindow;
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
	public float getPenaltyWindow() {

		return penaltyWindow;
	}

	@Override
	public void setPenaltyWindow(float penaltyWindow) {

		this.penaltyWindow = penaltyWindow;
	}

	@Override
	public float getPenaltyLevelFactor() {

		return penaltyLevelFactor;
	}

	@Override
	public void setPenaltyLevelFactor(float penaltyLevelFactor) {

		this.penaltyLevelFactor = penaltyLevelFactor;
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