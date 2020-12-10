/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - don't extract ion signal more than once, use lazy result
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.comparator;

import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

public class CosineComparator extends AbstractCosineComparator {

	@Override
	protected double getVectorValue(IExtractedIonSignal signal, int i) {

		return signal.getAbundance(i);
	}
};