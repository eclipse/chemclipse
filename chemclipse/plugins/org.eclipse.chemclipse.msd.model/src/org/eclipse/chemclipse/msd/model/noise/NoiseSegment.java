/*******************************************************************************
 * Copyright (c) 2010, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.noise;

import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;

public class NoiseSegment implements INoiseSegment {

	private IAnalysisSegment analysisSegment;
	private ICombinedMassSpectrum noiseMassSpectrum;

	public NoiseSegment(IAnalysisSegment analysisSegment, ICombinedMassSpectrum noiseMassSpectrum) {
		this.analysisSegment = analysisSegment;
		this.noiseMassSpectrum = noiseMassSpectrum;
	}

	@Override
	public IAnalysisSegment getAnalysisSegment() {

		return analysisSegment;
	}

	@Override
	public ICombinedMassSpectrum getNoiseMassSpectrum() {

		return noiseMassSpectrum;
	}
}
