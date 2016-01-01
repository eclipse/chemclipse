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

public interface IIdentifierSettings {

	/**
	 * Returns the id of the MS comparator that shall be used
	 * for the mass spectrum identification.
	 * 
	 * @return String
	 */
	String getMassSpectrumComparatorId();

	/**
	 * Set the id of the mass spectrum comparator. E.g.:
	 * org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos
	 * 
	 * 
	 * @param massSpectrumComparatorId
	 */
	void setMassSpectrumComparatorId(String massSpectrumComparatorId);

	/**
	 * Returns if the result should be stored automatically in the peak or
	 * chromatogram.
	 * 
	 * @return boolean
	 */
	boolean isSetResultAutomatically();

	/**
	 * Sets if the result should be stored automatically in the peak or
	 * chromatogram.
	 * 
	 * @param setResultAutomatically
	 */
	void setSetResultAutomatically(boolean setResultAutomatically);

	/**
	 * Returns the excludedIons instance.
	 * 
	 * @return {@link IMarkedIons}
	 */
	IMarkedIons getExcludedIons();

	float getPenaltyCalculationLevelFactor();

	void setPenaltyCalculationLevelFactor(float penaltyCalculationLevelFactor);

	float getPenaltyCalculationMaxValue();

	void setPenaltyCalculationMaxValue(float maxPenaltyCalculationValue);

	// Identification
	String getForceMatchFactorPenaltyCalculationForIdentification();

	void setForceMatchFactorPenaltyCalculationForIdentification(String forceMatchFactorPenaltyCalculation);

	float getRetentionIndexWindowForIdentification();

	void setRetentionIndexWindowForIdentification(float retentionIndexWindow);

	float getRetentionTimeWindowForIdentification();

	void setRetentionTimeWindowForIdentification(float retentionTimeWindow);

	// Database
	String getForceMatchFactorPenaltyCalculationForDatabase();

	void setForceMatchFactorPenaltyCalculationForDatabase(String forceMatchFactorPenaltyCalculation);

	float getRetentionIndexWindowForDatabase();

	void setRetentionIndexWindowForDatabase(float retentionIndexWindow);

	float getRetentionTimeWindowForDatabase();

	void setRetentionTimeWindowForDatabase(float retentionTimeWindow);
}
