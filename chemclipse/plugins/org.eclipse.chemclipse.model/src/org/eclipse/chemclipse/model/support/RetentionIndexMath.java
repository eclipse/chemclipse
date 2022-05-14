/*******************************************************************************
 * Copyright (c) 2014, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Matthias Mailänder - match alkanes by CAS or IUPAC name
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;

public class RetentionIndexMath {

	public static final int RETENTION_INDEX_MISSING = 0;
	public static final int RETENTION_TIME_MISSING = -1;
	public static final String ALKANE_PREFIX = "C";

	/**
	 * Calculate the value if both entries exists.
	 * See AMDIS manual:
	 * RIcomp = RIlo + ( (RIhi - RIlo) * (RTact - RTlo) / (RThi - RTlo) )
	 */
	public static float calculateRetentionIndex(int retentionTime, ISeparationColumnIndices separationColumnIndices) {

		float retentionIndex = RETENTION_INDEX_MISSING;
		Map.Entry<Integer, IRetentionIndexEntry> floorEntry = separationColumnIndices.floorEntry(retentionTime);
		Map.Entry<Integer, IRetentionIndexEntry> ceilingEntry = separationColumnIndices.ceilingEntry(retentionTime);
		//
		if(floorEntry != null && ceilingEntry != null) {
			/*
			 * Get the values.
			 */
			IRetentionIndexEntry floorIndex = floorEntry.getValue();
			IRetentionIndexEntry ceilingIndex = ceilingEntry.getValue();
			float retentionIndexLow = floorIndex.getRetentionIndex();
			int retentionTimeLow = floorIndex.getRetentionTime();
			float retentionIndexHigh = ceilingIndex.getRetentionIndex();
			int retentionTimeHigh = ceilingIndex.getRetentionTime();
			retentionIndex = calculateRetentionIndex(retentionTime, retentionTimeLow, retentionTimeHigh, retentionIndexLow, retentionIndexHigh);
		}
		//
		return retentionIndex;
	}

	/**
	 * Returns -1 if no retention time could be calculated.
	 * 
	 * @param retentionIndex
	 * @param retentionIndexMap
	 * @return int
	 */
	public static int calculateRetentionTime(int retentionIndex, TreeMap<Integer, Integer> retentionIndexMap) {

		int retentionTime = RETENTION_TIME_MISSING;
		Map.Entry<Integer, Integer> floorEntry = retentionIndexMap.floorEntry(retentionIndex);
		Map.Entry<Integer, Integer> ceilingEntry = retentionIndexMap.ceilingEntry(retentionIndex);
		//
		if(floorEntry != null && ceilingEntry != null) {
			/*
			 * Get the values.
			 */
			float retentionIndexLow = floorEntry.getKey();
			int retentionTimeLow = floorEntry.getValue();
			float retentionIndexHigh = ceilingEntry.getKey();
			int retentionTimeHigh = ceilingEntry.getValue();
			retentionTime = calculateRetentionTime(retentionIndex, retentionIndexLow, retentionIndexHigh, retentionTimeLow, retentionTimeHigh);
		}
		//
		return retentionTime;
	}

	public static float calculateRetentionIndex(int retentionTime, int retentionTimeLow, int retentionTimeHigh, float retentionIndexLow, float retentionIndexHigh) {

		return (float)calculateX(retentionTime, retentionTimeLow, retentionTimeHigh, retentionIndexLow, retentionIndexHigh);
	}

	public static int calculateRetentionTime(int retentionIndex, float retentionIndexLow, float retentionIndexHigh, int retentionTimeLow, int retentionTimeHigh) {

		return (int)Math.round(calculateX(retentionIndex, retentionIndexLow, retentionIndexHigh, retentionTimeLow, retentionTimeHigh));
	}

	private static double calculateX(double y, double yLower, double yHigher, double xLower, double xHigher) {

		double x = 0;
		//
		if(yLower == yHigher) {
			return xLower;
		}
		/*
		 * Execute the calculation.
		 */
		double factorX = xHigher - xLower;
		double nominatorRT = y - yLower;
		double denominatorRT = yHigher - yLower;
		//
		if(denominatorRT != 0) {
			x = xLower + factorX * nominatorRT / denominatorRT;
		}
		//
		return x;
	}
}