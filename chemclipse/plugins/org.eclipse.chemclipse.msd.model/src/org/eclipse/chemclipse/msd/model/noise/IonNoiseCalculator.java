/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.noise;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IonNoiseCalculator extends INoiseCalculator {

	/**
	 * Calculates the Noisesegments for the given ion on the chromatogram
	 * 
	 * @param chromatogram
	 * @param ion
	 * @param monitor
	 * @return
	 */
	List<NoiseSegment> getNoiseSegments(IChromatogram<?> chromatogram, double ion, IProgressMonitor monitor);
}
