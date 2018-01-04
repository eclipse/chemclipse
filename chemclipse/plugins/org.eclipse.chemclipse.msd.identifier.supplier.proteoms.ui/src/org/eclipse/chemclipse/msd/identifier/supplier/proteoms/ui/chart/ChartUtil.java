/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.chart;

import java.util.ArrayList;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.Peak;

public class ChartUtil {

	public static double getMaxValue(double[] arr) {

		double max = Double.MIN_VALUE;
		for(double d : arr) {
			max = Math.max(max, d);
		}
		return max;
	}

	public static double findMaxintensity(ArrayList<Peak> peaks) {

		double maxI = Double.MAX_VALUE;
		for(Peak p : peaks) {
			maxI = Math.max(maxI, p.getIntensity());
		}
		return maxI;
	}
}
