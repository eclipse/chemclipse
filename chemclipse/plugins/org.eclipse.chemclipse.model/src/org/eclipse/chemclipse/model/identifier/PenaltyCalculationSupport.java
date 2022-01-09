/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public class PenaltyCalculationSupport {

	private static final double NO_PENALTY = 0.0d;

	/**
	 * Calculate and apply the penalty on demand.
	 * 
	 * @param unknown
	 * @param reference
	 * @param comparisonResult
	 * @param identifierSettings
	 */
	public static void applyPenalty(IScan unknown, IScan reference, IComparisonResult comparisonResult, IIdentifierSettings identifierSettings) {

		int retentionTimeUnknown = unknown.getRetentionTime();
		float retentionIndexUnknown = unknown.getRetentionIndex();
		int retentionTimeReference = reference.getRetentionTime();
		float retentionIndexReference = reference.getRetentionIndex();
		applyPenalty(retentionTimeUnknown, retentionIndexUnknown, retentionTimeReference, retentionIndexReference, comparisonResult, identifierSettings);
	}

	/**
	 * Calculate and apply the penalty on demand.
	 * 
	 * @param retentionTimeUnknown
	 * @param retentionIndexUnknown
	 * @param retentionTimeReference
	 * @param retentionIndexReference
	 * @param comparisonResult
	 * @param identifierSettings
	 */
	public static void applyPenalty(int retentionTimeUnknown, float retentionIndexUnknown, int retentionTimeReference, float retentionIndexReference, IComparisonResult comparisonResult, IIdentifierSettings identifierSettings) {

		final float penalty;
		switch(identifierSettings.getPenaltyCalculation()) {
			case RETENTION_TIME_MS:
				penalty = (float)calculatePenalty(retentionTimeUnknown, retentionTimeReference, identifierSettings.getPenaltyWindow(), identifierSettings.getPenaltyLevelFactor(), identifierSettings.getMaxPenalty());
				break;
			case RETENTION_TIME_MIN:
				penalty = (float)calculatePenalty(retentionTimeUnknown / IChromatogram.MINUTE_CORRELATION_FACTOR, retentionTimeReference / IChromatogram.MINUTE_CORRELATION_FACTOR, identifierSettings.getPenaltyWindow(), identifierSettings.getPenaltyLevelFactor(), identifierSettings.getMaxPenalty());
				break;
			case RETENTION_INDEX:
				penalty = (float)calculatePenalty(retentionIndexUnknown, retentionIndexReference, identifierSettings.getPenaltyWindow(), identifierSettings.getPenaltyLevelFactor(), identifierSettings.getMaxPenalty());
				break;
			default:
				penalty = 0.0f;
				break;
		}
		/*
		 * Apply the penalty on demand.
		 */
		if(penalty != 0.0f) {
			comparisonResult.setPenalty(penalty);
		}
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
	public static double calculatePenalty(double valueUnknown, double valueReference, double valueWindow, double penaltyCalculationLevelFactor, double maxPenalty) {

		/*
		 * Checks
		 */
		if(Double.isNaN(valueUnknown) || valueUnknown < 0) {
			return NO_PENALTY;
		}
		//
		if(Double.isNaN(valueReference) || valueReference < 0) {
			return NO_PENALTY;
		}
		//
		if(Double.isNaN(valueWindow) || valueWindow <= 0) {
			return NO_PENALTY;
		}
		//
		if(Double.isNaN(penaltyCalculationLevelFactor) || penaltyCalculationLevelFactor <= 0) {
			return NO_PENALTY;
		}
		//
		if(Double.isNaN(maxPenalty) || maxPenalty <= IIdentifierSettings.MIN_PENALTY_MATCH_FACTOR || maxPenalty > IIdentifierSettings.MAX_PENALTY_MATCH_FACTOR) {
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