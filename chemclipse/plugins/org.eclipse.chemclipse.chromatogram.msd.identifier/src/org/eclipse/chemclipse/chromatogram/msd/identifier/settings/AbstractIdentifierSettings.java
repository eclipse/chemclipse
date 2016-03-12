/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

public class AbstractIdentifierSettings implements IIdentifierSettings {

	private String massSpectrumComparatorId = "";
	private boolean setResultAutomatically;
	private IMarkedIons excludedIons;
	/*
	 * Retention Time / Index Penalty Calculation
	 */
	private String penaltyCalculation;
	private int retentionTimeWindow; // milliseconds
	private float retentionIndexWindow;
	private float penaltyCalculationLevelFactor;
	private float penaltyCalculationMaxValue;

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
	public float getPenaltyCalculationMaxValue() {

		return penaltyCalculationMaxValue;
	}

	@Override
	public void setPenaltyCalculationMaxValue(float penaltyCalculationMaxValue) {

		this.penaltyCalculationMaxValue = penaltyCalculationMaxValue;
	}
}
