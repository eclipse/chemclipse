/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.support;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.xxd.model.filter.peaks.ProcessPeaksByAreaFilterSettings;

public class ProcessPeaksByAreaFilterUtils {

	public static double[] getAreaLimits(ProcessPeaksByAreaFilterSettings configuration, ProcessPeaksByAreaFilterAreaTypeSelection selection) {
		
		double[] areaLimits = new double[2];
		if(selection==ProcessPeaksByAreaFilterAreaTypeSelection.INTEGRATED_AREA) {
			areaLimits[0] = configuration.getMinimumAreaValue();
			areaLimits[1] = configuration.getMaximumAreaValue();
		} else {
			areaLimits[0] = configuration.getMinimumPercentageAreaValue();
			areaLimits[1] = configuration.getMaximumPercentageAreaValue();
		}
		if(areaLimits[0]>areaLimits[1]) {
			throw new IllegalArgumentException("Please check the values of the specified limits!");
		}
		return areaLimits;
	}

	public static <X extends IPeak> double calculateAreaCompareValue(ProcessPeaksByAreaFilterAreaTypeSelection selection, X peak, Collection<X> read) {
		
		double compareValue;
		if(selection==ProcessPeaksByAreaFilterAreaTypeSelection.INTEGRATED_AREA) {
			compareValue = peak.getIntegratedArea();
		} else {
			compareValue = (100/calculateAreaSum(read))*peak.getIntegratedArea();
		}
		return compareValue;
	}

	private static <X extends IPeak> double calculateAreaSum(Collection<X> read) {
		
		double areaSum = 0;		
		for(X peak : read) {
			areaSum = areaSum + peak.getIntegratedArea();
		}
		return areaSum;
	}
}
