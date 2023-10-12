/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public abstract class AbstractComparisonRatingSupplier implements IRatingSupplier {

	private static final float RATING_VERY_GOOD = 90.0f;
	private static final float RATING_GOOD = 80.0f;
	private static final float RATING_AVERAGE = 70.0f;
	private static final float RATING_BAD = 60.0f;
	/*
	 * By default, the no match result is set.
	 * The comparison result can be updated via the method.
	 */
	private IComparisonResult comparisonResult = ComparisonResult.COMPARISON_RESULT_NO_MATCH;

	@Override
	public RatingStatus getStatus() {

		float rating = getScore();
		RatingStatus ratingScheme;
		//
		if(rating >= RATING_VERY_GOOD) {
			ratingScheme = RatingStatus.VERY_GOOD;
		} else if(rating >= RATING_GOOD) {
			ratingScheme = RatingStatus.GOOD;
		} else if(rating >= RATING_AVERAGE) {
			ratingScheme = RatingStatus.AVERAGE;
		} else if(rating >= RATING_BAD) {
			ratingScheme = RatingStatus.BAD;
		} else {
			ratingScheme = RatingStatus.VERY_BAD;
		}
		//
		return ratingScheme;
	}

	@Override
	public void updateComparisonResult(IComparisonResult comparisonResult) {

		this.comparisonResult = comparisonResult;
	}

	protected IComparisonResult getComparisonResult() {

		return comparisonResult;
	}
}