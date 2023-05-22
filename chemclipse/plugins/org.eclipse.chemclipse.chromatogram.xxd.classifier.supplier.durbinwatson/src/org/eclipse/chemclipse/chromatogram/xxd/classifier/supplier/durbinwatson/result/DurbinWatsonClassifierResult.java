/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.AbstractChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;

public class DurbinWatsonClassifierResult extends AbstractChromatogramClassifierResult implements IDurbinWatsonClassifierResult {

	private List<ISavitzkyGolayFilterRating> savitzkyGolayFilterRatings = new ArrayList<>();

	public DurbinWatsonClassifierResult(ResultStatus resultStatus, String description) {

		super(resultStatus, description);
	}

	@Override
	public List<ISavitzkyGolayFilterRating> getSavitzkyGolayFilterRatings() {

		return savitzkyGolayFilterRatings;
	}
}
