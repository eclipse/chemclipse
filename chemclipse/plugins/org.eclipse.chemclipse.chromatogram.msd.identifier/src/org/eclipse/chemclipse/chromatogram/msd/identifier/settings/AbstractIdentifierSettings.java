/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AbstractIdentifierSettings implements IIdentifierSettings {

	@JsonIgnore
	private String massSpectrumComparatorId = "";
	@JsonIgnore
	private boolean setResultAutomatically;
	@JsonIgnore
	private IMarkedIons excludedIons;
	/*
	 * Retention Time / Index Penalty Calculation
	 */
	@JsonIgnore
	private String penaltyCalculation;
	@JsonIgnore
	private int retentionTimeWindow; // milliseconds
	@JsonIgnore
	private float retentionIndexWindow;
	@JsonIgnore
	private float penaltyCalculationLevelFactor;
	@JsonIgnore
	private float maxPenalty; // between 0 and 100, see IIdentifierSettings

	public AbstractIdentifierSettings() {
		excludedIons = new MarkedIons();
	}

	@Override
	public String getMassSpectrumComparatorId() {

		return massSpectrumComparatorId;
	}

	@Override
	public void setMassSpectrumComparatorId(String massSpectrumComparatorId) {

		this.massSpectrumComparatorId = massSpectrumComparatorId;
	}

	@Override
	public boolean isSetResultAutomatically() {

		return setResultAutomatically;
	}

	@Override
	public void setSetResultAutomatically(boolean setResultAutomatically) {

		this.setResultAutomatically = setResultAutomatically;
	}

	@Override
	public IMarkedIons getExcludedIons() {

		return excludedIons;
	}

	/*
	 * Identification
	 */
	@Override
	public String getPenaltyCalculation() {

		return penaltyCalculation;
	}

	@Override
	public void setPenaltyCalculation(String penaltyCalculation) {

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
