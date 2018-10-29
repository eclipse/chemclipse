/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 *
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public class ComparisonResult extends AbstractComparisonResult implements IComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 6897854010927446632L;
	private static ComparisonResult COMPARISON_RESULT_NO_MATCH = null;

	/**
	 * Constructs the result.
	 *
	 * @param matchQuality
	 * @param reverseMatchQuality
	 */
	public ComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect) {
		super(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect);
	}

	public ComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {
		super(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
	}

	public ComparisonResult(IComparisonResult comparisonResult) {
		super(comparisonResult);
	}

	@Override
	public ComparisonResult setMatch(boolean match) {

		super.setMatch(match);
		return this;
	}

	public static ComparisonResult createNoMatchComparisonResult() {

		if(COMPARISON_RESULT_NO_MATCH == null) {
			COMPARISON_RESULT_NO_MATCH = new ComparisonResult(FACTOR_NO_MATCH, FACTOR_NO_MATCH, FACTOR_NO_MATCH, FACTOR_NO_MATCH).setMatch(false);
		}
		return COMPARISON_RESULT_NO_MATCH;
	}

	public static ComparisonResult createBestMatchComparisonResult() {

		return new ComparisonResult(FACTOR_BEST_MATCH, FACTOR_BEST_MATCH, FACTOR_BEST_MATCH, FACTOR_BEST_MATCH).setMatch(true);
	}
}
