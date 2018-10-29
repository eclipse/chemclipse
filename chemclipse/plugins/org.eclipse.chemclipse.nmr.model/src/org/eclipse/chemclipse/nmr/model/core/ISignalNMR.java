/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.io.Serializable;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.model.core.ISignal;

public interface ISignalNMR extends ISignal, Serializable {

	double getChemicalShift();

	void setChemicalShift(double chemicalShift);

	double getIntensity();

	void setIntensity(double intensity);

	Complex getFourierTransformedData();

	void setFourierTransformedData(Complex fourierTransformedData);

	Complex getPhaseCorrectedData();

	void setPhaseCorrectedData(Complex phaseCorrectedData);

	Complex getBaselineCorrectedData();

	void setBaselineCorrectedData(Complex baselineCorrectedData);
}