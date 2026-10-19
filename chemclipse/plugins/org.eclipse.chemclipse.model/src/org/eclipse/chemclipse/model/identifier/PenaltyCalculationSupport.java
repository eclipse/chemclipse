/*******************************************************************************
 * Copyright (c) 2016, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;

public class PenaltyCalculationSupport {

	private static final double NO_PENALTY = 0.0d;

	/**
	 * Calculate and apply the penalty on demand.
	 * 
	 * @param unknown
	 * @param reference
	 * @param comparisonResult
	 * @param penaltyCalculationSettings
	 */
	public static void applyPenalty(IScan unknown, IScan reference, IComparisonResult comparisonResult, IPenaltyCalculationSettings penaltyCalculationSettings) {

		/*
		 * Apply the penalty on demand.
		 */
		float penalty = calculatePenalty(unknown, reference, penaltyCalculationSettings);
		if(penalty > 0.0f) {
			comparisonResult.setPenalty(penalty);
		}
	}

	/**
	 * Calculate the penalty. It is always between 0 and 100.
	 * 
	 * @param unknown
	 * @param reference
	 * @param penaltyCalculationSettings
	 * @return float
	 */
	public static float calculatePenalty(IScan unknown, IScan reference, IPenaltyCalculationSettings penaltyCalculationSettings) {

		int retentionTimeUnknown = unknown.getRetentionTime();
		float retentionIndexUnknown = unknown.getRetentionIndex();
		int retentionTimeReference = reference.getRetentionTime();
		float retentionIndexReference = reference.getRetentionIndex();

		return calculatePenalty(retentionTimeUnknown, retentionIndexUnknown, retentionTimeReference, retentionIndexReference, penaltyCalculationSettings);
	}

	/**
	 * Calculate the penalty.
	 * 
	 * @param retentionTimeUnknown
	 * @param retentionIndexUnknown
	 * @param retentionTimeReference
	 * @param retentionIndexReference
	 * @param comparisonResult
	 * @param penaltyCalculationSettings
	 */
	private static float calculatePenalty(int retentionTimeUnknown, float retentionIndexUnknown, int retentionTimeReference, float retentionIndexReference, IPenaltyCalculationSettings penaltyCalculationSettings) {

		float penalty = 0.0f;
		switch(penaltyCalculationSettings.getPenaltyCalculation()) {
			case RETENTION_TIME_MS:
				if(retentionTimeUnknown > 0) {
					if(retentionTimeReference > 0) {
						penalty = (float)calculatePenalty(retentionTimeUnknown, retentionTimeReference, penaltyCalculationSettings.getPenaltyWindow(), penaltyCalculationSettings.getPenaltyLevelFactor(), penaltyCalculationSettings.getMaxPenalty());
					} else {
						penalty = penaltyCalculationSettings.getPenaltyMissingReference();
					}
				} else {
					penalty = penaltyCalculationSettings.getPenaltyMissingReference();
				}
				break;
			case RETENTION_TIME_MIN:
				if(retentionTimeUnknown > 0) {
					if(retentionTimeReference > 0) {
						penalty = (float)calculatePenalty(retentionTimeUnknown / IChromatogramOverview.MINUTE_CORRELATION_FACTOR, retentionTimeReference / IChromatogramOverview.MINUTE_CORRELATION_FACTOR, penaltyCalculationSettings.getPenaltyWindow(), penaltyCalculationSettings.getPenaltyLevelFactor(), penaltyCalculationSettings.getMaxPenalty());
					} else {
						penalty = penaltyCalculationSettings.getPenaltyMissingReference();
					}
				} else {
					penalty = penaltyCalculationSettings.getPenaltyMissingReference();
				}
				break;
			case RETENTION_INDEX:
				if(retentionIndexUnknown > 0) {
					if(retentionIndexReference > 0) {
						penalty = (float)calculatePenalty(retentionIndexUnknown, retentionIndexReference, penaltyCalculationSettings.getPenaltyWindow(), penaltyCalculationSettings.getPenaltyLevelFactor(), penaltyCalculationSettings.getMaxPenalty());
					} else {
						penalty = penaltyCalculationSettings.getPenaltyMissingReference();
					}
				} else {
					penalty = penaltyCalculationSettings.getPenaltyMissingReference();
				}
				break;
			default:
				break;
		}
		/*
		 * Validation
		 */
		if(penalty < 0) {
			penalty = 0;
		} else if(penalty > 100) {
			penalty = 100;
		}
		/*
		 * Calculated Penalty
		 */
		return penalty;
	}

	/**
	 * Calculates the penalty for the given values.
	 * 
	 * @param valueUnknown
	 * @param valueReference
	 * @param valueWindow
	 * @param penaltyCalculationLevelFactor
	 * @param maxPenalty
	 * @return double
	 */
	private static double calculatePenalty(double valueUnknown, double valueReference, double valueWindow, double penaltyCalculationLevelFactor, double maxPenalty) {

		/*
		 * Checks
		 */
		if(Double.isNaN(valueUnknown) || valueUnknown < 0) {
			return NO_PENALTY;
		}

		if(Double.isNaN(valueReference) || valueReference < 0) {
			return NO_PENALTY;
		}

		if(Double.isNaN(valueWindow) || valueWindow <= 0) {
			return NO_PENALTY;
		}

		if(Double.isNaN(penaltyCalculationLevelFactor) || penaltyCalculationLevelFactor <= 0) {
			return NO_PENALTY;
		}

		if(Double.isNaN(maxPenalty) || maxPenalty <= IPenaltyCalculationSettings.MIN_PENALTY_MATCH_FACTOR || maxPenalty > IPenaltyCalculationSettings.MAX_PENALTY_MATCH_FACTOR) {
			return NO_PENALTY;
		}
		/*
		 * Calculation
		 */
		final double windowRangeCount = Math.abs((valueUnknown - valueReference) / valueWindow);
		if(windowRangeCount <= 1.0f) {
			return NO_PENALTY;
		} else {
			final double result = (windowRangeCount - 1.0f) * penaltyCalculationLevelFactor;
			return (result > maxPenalty) ? maxPenalty : result;
		}
	}
}