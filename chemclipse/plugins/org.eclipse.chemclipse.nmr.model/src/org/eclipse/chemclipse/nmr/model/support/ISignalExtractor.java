/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.support;

import org.apache.commons.math3.complex.Complex;

public interface ISignalExtractor {

	Complex[] extractFourierTransformedData();

	Complex[] extractPhaseCorrectedData();

	Complex[] extractBaselineCorrectedData();

	double[] extractFourierTransformedDataRealPart();

	double[] extractPhaseCorrectedDataRealPart();

	double[] extractBaselineCorrectedDataRealPart();

	double[] extractChemicalShift();

	void createScans(Complex[] fourierTransformedData, double[] chemicalShift);

	void setIntesity(double[] intensities);

	void setIntesity(Complex[] intensities);

	void setPhaseCorrection(Complex[] phaseCorrection, boolean resetIntesityValue);

	void setBaselineCorrection(Complex[] baseleniCorrection, boolean resetIntensityValue);
}
