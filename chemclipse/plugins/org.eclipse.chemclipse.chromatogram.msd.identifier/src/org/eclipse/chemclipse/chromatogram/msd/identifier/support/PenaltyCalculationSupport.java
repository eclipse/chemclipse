/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
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

	/**
	 * Calculate a penalty using the retention time.
	 * The value max penalty must be between:
	 * IComparisonResult.MIN_ALLOWED_PENALTY
	 * and
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

		// logger.debug("Calculate penalty from retention index");
		try {
			runPreConditionChecks(unknown, reference, retentionIndexWindow, maxPenalty);
			runRetentionIndexCheck(unknown, reference);
			return calculatePenalty(unknown.getRetentionIndex(), reference.getRetentionIndex(), retentionIndexWindow, penaltyCalculationLevelFactor, maxPenalty);
		} catch(Exception e) {
			e.printStackTrace();
			return 0.0f;
		}
	}

	/**
	 * Calculate a penalty using the retention index.
	 * The value max penalty must be between:
	 * IComparisonResult.MIN_ALLOWED_PENALTY
	 * and
	 * IComparisonResult.MAX_ALLOWED_PENALTY
	 * 
	 * @param unknown
	 * @param reference
	 * @param retentionTimeWindow
	 * @param penaltyCalculationLevelFactor
	 * @param maxPenalty
	 * @return float
	 */
	public static float calculatePenaltyFromRetentionTime(IScanMSD unknown, IScanMSD reference, int retentionTimeWindow, float penaltyCalculationLevelFactor, float maxPenalty) {

		// logger.debug("Calculate penalty from retention time");
		try {
			runPreConditionChecks(unknown, reference, retentionTimeWindow, maxPenalty);
			runRetentionTimeCheck(unknown, reference);
			return calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, maxPenalty);
		} catch(Exception e) {
			e.printStackTrace();
			return 0.0f;
		}
	}

	public static float calculatePenalty(float valueUnknown, float valueReference, float valueWindow, float penaltyCalculationLevelFactor, float maxPenalty) {

		if(Float.isNaN(valueUnknown) || valueUnknown < 0)
			throw new IllegalArgumentException("" + valueUnknown);
		if(Float.isNaN(valueReference) || valueReference < 0)
			throw new IllegalArgumentException("" + valueReference);
		if(Float.isNaN(valueWindow) || valueWindow <= 0)
			throw new IllegalArgumentException("" + valueWindow);
		if(Float.isNaN(penaltyCalculationLevelFactor) || penaltyCalculationLevelFactor <= 0)
			throw new IllegalArgumentException("" + penaltyCalculationLevelFactor);
		if(Float.isNaN(maxPenalty) || maxPenalty <= 0)
			throw new IllegalArgumentException("" + maxPenalty);
		// logger.debug("unkown " + valueReference + ", reference " + valueReference + ", window size " + valueWindow);
		float windowRangeCount = Math.abs((valueUnknown - valueReference) / valueWindow);
		// logger.debug("window count " + windowRangeCount);
		if(windowRangeCount <= 1.0f) {
			return 0.0f;
		} else {
			float result = (windowRangeCount - 1.0f) * penaltyCalculationLevelFactor;
			// logger.debug("penalty result " + result);
			return (result > maxPenalty) ? maxPenalty : result;
		}
	}

	private static void runPreConditionChecks(IScanMSD unknown, IScanMSD reference, float valueWindow, float maxPenalty) {

		if(unknown == null || reference == null) {
			throw new NullPointerException();
		}
		//
		if(valueWindow == 0.0f) {
			throw new IllegalArgumentException();
		}
		//
		if(maxPenalty < IComparisonResult.MIN_ALLOWED_PENALTY || maxPenalty > IComparisonResult.MAX_ALLOWED_PENALTY) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Don't calculate penalties if the retention index is not set.
	 * 
	 * @param unknown
	 * @param reference
	 * @throws Exception
	 */
	private static void runRetentionIndexCheck(IScanMSD unknown, IScanMSD reference) {

		if(unknown.getRetentionIndex() == 0.0f || reference.getRetentionIndex() == 0.0f) {
			throw new IllegalArgumentException("The retention index of the unknown or reference is not set.");
		}
	}

	/**
	 * Don't calculate penalties if the retention time is not set.
	 * 
	 * @param unknown
	 * @param reference
	 * @throws Exception
	 */
	private static void runRetentionTimeCheck(IScanMSD unknown, IScanMSD reference) {

		if(unknown.getRetentionTime() == 0 || reference.getRetentionTime() == 0) {
			throw new IllegalArgumentException("The retention time of the unknown or reference is not set.");
		}
	}
}
