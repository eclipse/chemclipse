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
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.settings.IProcessSettings;

public interface IIdentifierSettings extends IProcessSettings {

	String[][] PENALTY_CALCULATION_OPTIONS = new String[][]{//
			{PenaltyCalculation.NONE.getLabel(), PenaltyCalculation.NONE.name()}, //
			{PenaltyCalculation.RETENTION_INDEX.getLabel(), PenaltyCalculation.RETENTION_INDEX.name()}, //
			{PenaltyCalculation.RETENTION_TIME.getLabel(), PenaltyCalculation.RETENTION_TIME.name()}, //
			{PenaltyCalculation.BOTH.getLabel(), PenaltyCalculation.BOTH.name()}};
	//
	float DEF_PENALTY_CALCULATION_LEVEL_FACTOR = 5.0f;
	float MIN_PENALTY_CALCULATION_LEVEL_FACTOR = 1.0f;
	float MAX_PENALTY_CALCULATION_LEVEL_FACTOR = 1000.0f;

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
	 * Retention Time / Index Penalty Calculation
	 */
	PenaltyCalculation getPenaltyCalculation();

	void setPenaltyCalculation(PenaltyCalculation penaltyCalculation);

	int getRetentionTimeWindow();

	void setRetentionTimeWindow(int retentionTimeWindow);

	float getRetentionIndexWindow();

	void setRetentionIndexWindow(float retentionIndex);

	float getPenaltyCalculationLevelFactor();

	void setPenaltyCalculationLevelFactor(float penaltyCalculationLevelFactor);

	float getMaxPenalty();

	void setMaxPenalty(float penaltyCalculationMaxValue);
}