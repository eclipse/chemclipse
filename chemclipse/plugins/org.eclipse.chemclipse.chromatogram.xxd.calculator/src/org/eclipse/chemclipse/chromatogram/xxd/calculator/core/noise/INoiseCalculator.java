/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise;

import org.eclipse.chemclipse.model.core.IChromatogram;

public interface INoiseCalculator {

	@SuppressWarnings("rawtypes")
	void setChromatogram(IChromatogram chromatogram, int segmentWidth);

	void recalculate();

	float getSignalToNoiseRatio(float intensity);
}
