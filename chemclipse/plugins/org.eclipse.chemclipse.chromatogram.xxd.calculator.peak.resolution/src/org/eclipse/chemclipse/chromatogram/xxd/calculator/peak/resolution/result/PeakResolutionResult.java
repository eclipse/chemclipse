/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.peak.resolution.result;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.AbstractChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.model.core.IPeakResolution;

public class PeakResolutionResult extends AbstractChromatogramClassifierResult implements IPeakResolutionResult {

	private List<IPeakResolution> peakResolutions = new ArrayList<>();

	public PeakResolutionResult(ResultStatus resultStatus, String description) {

		super(resultStatus, description);
	}

	@Override
	public List<IPeakResolution> getPeakResolutions() {

		return peakResolutions;
	}
}
