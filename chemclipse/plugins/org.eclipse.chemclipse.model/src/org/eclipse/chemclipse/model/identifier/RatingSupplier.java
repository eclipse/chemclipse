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

public class RatingSupplier extends AbstractComparisonRatingSupplier {

	private static final long serialVersionUID = 80242122334050614L;
	//
	private static final String ADVISE_INCOMPLETE = "Incomplete Target (Bad Conditions)";
	private static final String ADVISE_IMPURITIES = "Convoluted Target (Impurities)";
	//
	private static final float MIN_LIMIT_MATCH_FACTOR = 20.0f;
	private static final float MAX_LIMIT_MATCH_FACTOR = 80.0f;
	private static final float MIN_LIMIT_REVERSE_MATCH_FACTOR = 20.0f;
	private static final float MAX_LIMIT_REVERSE_MATCH_FACTOR = 80.0f;

	public RatingSupplier(IComparisonResult comparisonResult) {

		updateComparisonResult(comparisonResult);
	}

	@Override
	public String getAdvise() {

		String advise = "";
		/*
		 * Only take match and reverse match factor into account (classical search)
		 */
		IComparisonResult comparisonResult = getComparisonResult();
		float matchFactor = comparisonResult.getMatchFactor();
		float reverseMatchFactor = comparisonResult.getReverseMatchFactor();
		//
		if(matchFactor > 0 && reverseMatchFactor > 0) {
			if(matchFactor >= MAX_LIMIT_MATCH_FACTOR && reverseMatchFactor <= MIN_LIMIT_REVERSE_MATCH_FACTOR) {
				advise = ADVISE_INCOMPLETE;
			} else if(matchFactor <= MIN_LIMIT_MATCH_FACTOR && reverseMatchFactor >= MAX_LIMIT_REVERSE_MATCH_FACTOR) {
				advise = ADVISE_IMPURITIES;
			}
		}
		//
		return advise;
	}

	@Override
	public float getScore() {

		/*
		 * 0 -> probably value is not set.
		 */
		IComparisonResult comparisonResult = getComparisonResult();
		/*
		 * Match Factor
		 */
		float matchFactor = comparisonResult.getMatchFactor();
		float rating = matchFactor;
		/*
		 * Reverse Match Factor
		 */
		float reverseMatchFactor = comparisonResult.getReverseMatchFactor();
		if(reverseMatchFactor > 0.0f) {
			rating = (rating + reverseMatchFactor) / 2.0f;
		}
		/*
		 * Match Factor Direct
		 */
		float matchFactorDirect = comparisonResult.getMatchFactorDirect();
		if(matchFactorDirect > 0.0f) {
			rating = (rating + matchFactorDirect) / 2.0f;
		}
		/*
		 * Reverse Match Factor Direct
		 */
		float reverseMatchFactorDirect = comparisonResult.getReverseMatchFactorDirect();
		if(reverseMatchFactorDirect > 0.0f) {
			rating = (rating + reverseMatchFactorDirect) / 2.0f;
		}
		//
		return rating;
	}
}