/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
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
	private float penaltyCalculationLevelFactor;
	private float penaltyCalculationMaxValue;
	/*
	 * Identification
	 */
	private float retentionIndexWindowForIdentification;
	private float retentionTimeWindowForIdentification;
	private String forceMatchFactorPenaltyCalculationForIdentification;
	/*
	 * Database
	 * TODO need revision - NIST and File identifier don't enable the user to store mass spectra.
	 */
	private float retentionIndexWindowForDatabase;
	private float retentionTimeWindowForDatabase;
	private String forceMatchFactorPenaltyCalculationForDatabase;

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
	public void setPenaltyCalculationMaxValue(float maxPenaltyCalculationValue) {

		this.penaltyCalculationMaxValue = maxPenaltyCalculationValue;
	}

	/*
	 * Identification
	 */
	@Override
	public String getForceMatchFactorPenaltyCalculationForIdentification() {

		return forceMatchFactorPenaltyCalculationForIdentification;
	}

	@Override
	public void setForceMatchFactorPenaltyCalculationForIdentification(String forceMatchFactorPenaltyCalculationForIdentification) {

		this.forceMatchFactorPenaltyCalculationForIdentification = forceMatchFactorPenaltyCalculationForIdentification;
	}

	@Override
	public float getRetentionIndexWindowForIdentification() {

		return retentionIndexWindowForIdentification;
	}

	@Override
	public void setRetentionIndexWindowForIdentification(float retentionIndexWindowForIdentification) {

		this.retentionIndexWindowForIdentification = retentionIndexWindowForIdentification;
	}

	@Override
	public float getRetentionTimeWindowForIdentification() {

		return retentionTimeWindowForIdentification;
	}

	@Override
	public void setRetentionTimeWindowForIdentification(float retentionTimeWindowForIdentification) {

		this.retentionTimeWindowForIdentification = retentionTimeWindowForIdentification;
	}

	/*
	 * Database
	 */
	@Override
	public String getForceMatchFactorPenaltyCalculationForDatabase() {

		return forceMatchFactorPenaltyCalculationForDatabase;
	}

	@Override
	public void setForceMatchFactorPenaltyCalculationForDatabase(String forceMatchFactorPenaltyCalculationForDatabase) {

		this.forceMatchFactorPenaltyCalculationForDatabase = forceMatchFactorPenaltyCalculationForDatabase;
	}

	@Override
	public float getRetentionIndexWindowForDatabase() {

		return retentionIndexWindowForDatabase;
	}

	@Override
	public void setRetentionIndexWindowForDatabase(float retentionIndexWindowForDatabase) {

		this.retentionIndexWindowForDatabase = retentionIndexWindowForDatabase;
	}

	@Override
	public float getRetentionTimeWindowForDatabase() {

		return retentionTimeWindowForDatabase;
	}

	@Override
	public void setRetentionTimeWindowForDatabase(float retentionTimeWindowForDatabase) {

		this.retentionTimeWindowForDatabase = retentionTimeWindowForDatabase;
	}
}
