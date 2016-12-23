/*******************************************************************************
 * Copyright (c) 2010, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public class ComparisonResult extends AbstractComparisonResult implements IComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -1880511926278090160L;

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

	@Override
	public ComparisonResult setMatch(boolean match) {

		super.setMatch(match);
		return this;
	}

	public static ComparisonResult createNoMatchComparisonResult() {

		return new ComparisonResult(FACTOR_NO_MATCH, FACTOR_NO_MATCH, FACTOR_NO_MATCH, FACTOR_NO_MATCH).setMatch(false);
	}

	public static ComparisonResult createBestMatchComparisonResult() {

		return new ComparisonResult(FACTOR_BEST_MATCH, FACTOR_BEST_MATCH, FACTOR_BEST_MATCH, FACTOR_BEST_MATCH).setMatch(false);
	}
}
