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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.support;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class PenaltyCalculationSupport {

	/**
	 * Calculate a penalty using the retention time.
	 * 
	 * @param unknown
	 * @param reference
	 * @param retentionIndexWindow
	 * @param penaltyCalculationLevelFactor
	 * @param penaltyCalculationMaxValue
	 * @return float
	 */
	public float calculatePenaltyFromRetentionIndex(IScanMSD unknown, IScanMSD reference, float retentionIndexWindow, float penaltyCalculationLevelFactor, float penaltyCalculationMaxValue) {

		float retentionIndexUnknown = unknown.getRetentionIndex();
		float retentionIndexReference = reference.getRetentionIndex();
		float retentionIndexWindowCount = Math.abs((retentionIndexUnknown - retentionIndexReference) / retentionIndexWindow);
		if(retentionIndexWindowCount <= 1.0f) {
			return 0.0f;
		} else {
			float result = (retentionIndexWindowCount - 1.0f) * penaltyCalculationLevelFactor;
			return (result > penaltyCalculationMaxValue) ? penaltyCalculationMaxValue : result;
		}
	}

	/**
	 * Calculate a penalty using the retention index.
	 * 
	 * @param unknown
	 * @param reference
	 * @param retentionTimeWindow
	 * @param penaltyCalculationLevelFactor
	 * @param penaltyCalculationMaxValue
	 * @return float
	 */
	public float calculatePenaltyFromRetentionTime(IScanMSD unknown, IScanMSD reference, int retentionTimeWindow, float penaltyCalculationLevelFactor, float penaltyCalculationMaxValue) {

		int retentionTimeUnknown = unknown.getRetentionTime();
		int retentionTimeReference = reference.getRetentionTime();
		int retentionTimeWindowCount = Math.abs((retentionTimeUnknown - retentionTimeReference) / retentionTimeWindow);
		if(retentionTimeWindowCount <= 1) {
			return 1.0f;
		} else {
			float result = (retentionTimeWindowCount - 1) * penaltyCalculationLevelFactor;
			return (result > penaltyCalculationMaxValue) ? penaltyCalculationMaxValue : result;
		}
	}
}
