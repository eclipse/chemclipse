/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.comparator;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

public class CosineMultiplyComparator extends CosineComparator implements IMassSpectrumComparator {

	@Override
	protected double getVectorValue(IExtractedIonSignal signal, int i) {

		int mz = i;
		float intensity = signal.getAbundance(i);
		return mz * intensity;
	}
};