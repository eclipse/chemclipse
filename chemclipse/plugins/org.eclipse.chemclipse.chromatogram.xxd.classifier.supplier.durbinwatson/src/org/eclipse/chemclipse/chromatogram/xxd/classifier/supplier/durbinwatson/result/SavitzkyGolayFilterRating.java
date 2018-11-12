/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;

public class SavitzkyGolayFilterRating implements ISavitzkyGolayFilterRating {

	private double rating;
	private ChromatogramFilterSettings filterSettings;

	public SavitzkyGolayFilterRating(double rating, ChromatogramFilterSettings filterSettings) {
		this.rating = rating;
		this.filterSettings = filterSettings;
	}

	@Override
	public double getRating() {

		return rating;
	}

	@Override
	public ChromatogramFilterSettings getFilterSettings() {

		return filterSettings;
	}
}
