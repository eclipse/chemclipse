/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.msd.identifier.support;

import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class PenaltyCalculationSupport {

	private static final float NO_PENALTY = 0.0f;

	/**
	 * Calculate a penalty using the retention time. The value max penalty must
	 * be between: IComparisonResult.MIN_ALLOWED_PENALTY and
	 * IComparisonResult.MAX_ALLOWED_PENALTY
	 * 
	 * @param unknown
	 * @param reference
	 * @param retentionIndexWindow
	 * @param penaltyCalculationLevelFactor
	 * @param maxPenalty
	 * @return float
	 */
	public static float calculatePenaltyFromRetentionIndex(IScanMSD unknown, IScanMSD reference, float retentionIndexWindow, float penaltyCalculationLevelFactor, float maxPenalty) {

		if(unknown == null || reference == null) {
			return NO_PENALTY;
		}
		//
		if(retentionIndexWindow == 0.0f) {
			return NO_PENALTY;
		}
		//
		if(maxPenalty < IComparisonResult.MIN_ALLOWED_PENALTY || maxPenalty > IComparisonResult.MAX_ALLOWED_PENALTY) {
			return NO_PENALTY;
		}
		//
		return calculatePenalty(unknown.getRetentionIndex(), reference.getRetentionIndex(), retentionIndexWindow, penaltyCalculationLevelFactor, maxPenalty);
	}

	/**
	 * Calculate a penalty using the retention index. The value max penalty must
	 * be between: IComparisonResult.MIN_ALLOWED_PENALTY and
	 * IComparisonResult.MAX_ALLOWED_PENALTY
	 * 
	 * @param retentionTimeUnknown
	 * @param retentionTimeReference
	 * @param retentionTimeWindow
	 * @param penaltyCalculationLevelFactor
	 * @param maxPenalty
	 * @return float
	 */
	public static float calculatePenaltyFromRetentionTime(int retentionTimeUnknown, int retentionTimeReference, int retentionTimeWindow, float penaltyCalculationLevelFactor, float maxPenalty) {

		if(retentionTimeUnknown <= 0 || retentionTimeReference <= 0) {
			return NO_PENALTY;
		}
		//
		if(retentionTimeWindow == 0.0f) {
			return NO_PENALTY;
		}
		//
		if(maxPenalty < IComparisonResult.MIN_ALLOWED_PENALTY || maxPenalty > IComparisonResult.MAX_ALLOWED_PENALTY) {
			return NO_PENALTY;
		}
		//
		return calculatePenalty(retentionTimeUnknown, retentionTimeReference, retentionTimeWindow, penaltyCalculationLevelFactor, maxPenalty);
	}

	public static float calculatePenalty(float valueUnknown, float valueReference, float valueWindow, float penaltyCalculationLevelFactor, float maxPenalty) {

		/*
		 * Checks
		 */
		if(Float.isNaN(valueUnknown) || valueUnknown < 0) {
			return NO_PENALTY;
		}
		//
		if(Float.isNaN(valueReference) || valueReference < 0) {
			return NO_PENALTY;
		}
		//
		if(Float.isNaN(valueWindow) || valueWindow <= 0) {
			return NO_PENALTY;
		}
		//
		if(Float.isNaN(penaltyCalculationLevelFactor) || penaltyCalculationLevelFactor <= 0) {
			return NO_PENALTY;
		}
		//
		if(Float.isNaN(maxPenalty) || maxPenalty <= 0) {
			return NO_PENALTY;
		}
		/*
		 * Calculation
		 */
		final float windowRangeCount = Math.abs((valueUnknown - valueReference) / valueWindow);
		if(windowRangeCount <= 1.0f) {
			return NO_PENALTY;
		} else {
			final float result = (windowRangeCount - 1.0f) * penaltyCalculationLevelFactor;
			return (result > maxPenalty) ? maxPenalty : result;
		}
	}
}
