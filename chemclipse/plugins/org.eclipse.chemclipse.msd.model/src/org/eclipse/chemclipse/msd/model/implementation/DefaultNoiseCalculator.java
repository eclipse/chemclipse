/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - reimplemented to use extension point as default implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.model.core.IChromatogram;

/*
 * S/N = intensity / noiseValue
 */
public class DefaultNoiseCalculator implements INoiseCalculator {

	private static final String DEFAULT_ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.dyson";
	private INoiseCalculator calculator;

	private INoiseCalculator getCalculator() {

		if(calculator == null) {
			calculator = NoiseCalculator.getNoiseCalculator(DEFAULT_ID);
		}
		return calculator;
	}

	@Override
	public float getSignalToNoiseRatio(IChromatogram<?> chromatogram, int segmentWidth, float intensity) {

		INoiseCalculator noiseCalculator = getCalculator();
		if(noiseCalculator != null) {
			return noiseCalculator.getSignalToNoiseRatio(chromatogram, segmentWidth, intensity);
		}
		return 0;
	}
}
