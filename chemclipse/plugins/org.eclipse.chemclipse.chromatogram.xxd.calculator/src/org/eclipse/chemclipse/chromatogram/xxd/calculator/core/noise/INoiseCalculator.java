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
 * Christoph Läubrich - refactor the API for more general use cases
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.core.runtime.IProgressMonitor;

public interface INoiseCalculator {

	/**
	 * Calculates the SignalToNoise ration for the given chromatogram, segment with and intensity
	 * 
	 * @param chromatogram
	 *            the chromatogram to use, might be <code>null</code> in which case the chromatogram is not taken into account for calculation
	 * @param segmentWidth
	 * @param intensity
	 * @return
	 */
	float getSignalToNoiseRatio(IChromatogram<?> chromatogram, float intensity);

	List<NoiseSegment> getNoiseSegments(IChromatogram<?> chromatogram, IProgressMonitor monitor);
}
