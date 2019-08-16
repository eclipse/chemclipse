/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.exceptions.CodaCalculatorException;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

/**
 * @author eselmeister
 */
public class MassChromatographicQualityCalculator {

	/**
	 * Use only static methods.
	 */
	private MassChromatographicQualityCalculator() {
	}

	/**
	 * Returns a new {@link IMassChromatographicQualityResult} from the given
	 * values.<br/>
	 * 
	 * @param chromatogramSelection
	 * @param codaThreshold
	 * @param windowSize
	 * @return {@link IMassChromatographicQualityResult}
	 * @throws CodaCalculatorException
	 */
	public static IMassChromatographicQualityResult calculate(IChromatogramSelectionMSD chromatogramSelection, float codaThreshold, WindowSize windowSize) throws CodaCalculatorException {

		IMassChromatographicQualityResult result = new MassChromatographicQualityResult(chromatogramSelection, codaThreshold, windowSize);
		return result;
	}
}
