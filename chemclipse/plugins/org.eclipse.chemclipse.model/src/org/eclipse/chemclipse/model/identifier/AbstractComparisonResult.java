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
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public abstract class AbstractComparisonResult implements IComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 1295884624032029498L;
	//
	private boolean isMatch = false;
	private float matchFactor;
	private float matchFactorDirect;
	private float reverseMatchFactor;
	private float reverseMatchFactorDirect;
	private float probability;
	private float penalty;
	private String advise = "";

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect) {
		this.matchFactor = matchFactor;
		this.reverseMatchFactor = reverseMatchFactor;
		this.matchFactorDirect = matchFactorDirect;
		this.reverseMatchFactorDirect = reverseMatchFactorDirect;
		determineAdvise();
	}

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {
		this(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect);
		if(probability >= MIN_ALLOWED_PROBABILITY && probability <= MAX_ALLOWED_PROBABILITY) {
			this.probability = probability;
		}
	}

	public AbstractComparisonResult(IComparisonResult comparisonResult) {
		this(comparisonResult.getMatchFactor(), comparisonResult.getReverseMatchFactor(), comparisonResult.getMatchFactorDirect(), comparisonResult.getReverseMatchFactorDirect());
	}

	@Override
	public float getPenalty() {

		return penalty;
	}

	@Override
	public void clearPenalty() {

		this.penalty = 0;
	}

	@Override
	public void setPenalty(float penalty) {

		if(penalty >= MIN_ALLOWED_PENALTY && penalty <= MAX_ALLOWED_PENALTY) {
			this.penalty = penalty;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void addPenalty(float penalty) {

		float newPenalty = getPenalty() + penalty;
		if(newPenalty > MAX_ALLOWED_PENALTY) {
			setPenalty(MAX_ALLOWED_PENALTY);
		} else if(newPenalty < MIN_ALLOWED_PENALTY) {
			setPenalty(MIN_ALLOWED_PENALTY);
		} else {
			setPenalty(newPenalty);
		}
	}

	@Override
	public boolean isMatch() {

		return isMatch;
	}

	@Override
	public AbstractComparisonResult setMatch(boolean match) {

		this.isMatch = match;
		return this;
	}

	@Override
	public float getMatchFactor() {

		return getAdjustedValue(matchFactor, penalty);
	}

	@Override
	public float getMatchFactorDirect() {

		return getAdjustedValue(matchFactorDirect, penalty);
	}

	@Override
	public float getMatchFactorNotAdjusted() {

		return matchFactor;
	}

	@Override
	public float getMatchFactorDirectNotAdjusted() {

		return matchFactorDirect;
	}

	@Override
	public float getReverseMatchFactor() {

		return getAdjustedValue(reverseMatchFactor, penalty);
	}

	@Override
	public float getReverseMatchFactorDirect() {

		return getAdjustedValue(reverseMatchFactorDirect, penalty);
	}

	@Override
	public float getReverseMatchFactorNotAdjusted() {

		return reverseMatchFactor;
	}

	@Override
	public float getReverseMatchFactorDirectNotAdjusted() {

		return reverseMatchFactorDirect;
	}

	@Override
	public void adjustMatchFactor(float penalty) {

		setPenalty(penalty);
	}

	@Override
	public void adjustReverseMatchFactor(float penalty) {

		setPenalty(penalty);
	}

	@Override
	public float getProbability() {

		return probability;
	}

	@Override
	public String getAdvise() {

		return advise;
	}

	@Override
	public float getRating() {

		float rating = (matchFactor + reverseMatchFactor) / 2.0f;
		/*
		 * Shall the probability be used too?
		 */
		if(matchFactorDirect > 0.0f) {
			rating = (rating + matchFactorDirect) / 2.0f;
		}
		//
		if(reverseMatchFactorDirect > 0.0f) {
			rating = (rating + reverseMatchFactorDirect) / 2.0f;
		}
		//
		return rating;
	}

	public static float getAdjustedValue(float value, float penalty) {

		float result = value - penalty;
		if(result < 0) {
			return 0;
		}
		return result;
	}

	/**
	 * Determines the advise.
	 */
	private void determineAdvise() {

		if(getMatchFactor() >= MAX_LIMIT_MATCH_FACTOR && getReverseMatchFactor() <= MIN_LIMIT_REVERSE_MATCH_FACTOR) {
			advise = ADVISE_INCOMPLETE;
		} else if(getMatchFactor() <= MIN_LIMIT_MATCH_FACTOR && getReverseMatchFactor() >= MAX_LIMIT_REVERSE_MATCH_FACTOR) {
			advise = ADVISE_IMPURITIES;
		}
	}

	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(this.getClass() != other.getClass()) {
			return false;
		}
		IComparisonResult otherResult = (IComparisonResult)other;
		return getMatchFactor() == otherResult.getMatchFactor() && getReverseMatchFactor() == otherResult.getReverseMatchFactor() && getProbability() == otherResult.getProbability();
	}

	@Override
	public int compareTo(IComparisonResult o) {

		int result = Boolean.compare(this.isMatch(), o.isMatch());
		if(result == 0) {
			result = Float.compare(this.getMatchFactor(), o.getMatchFactor());
		}
		if(result == 0) {
			result = Float.compare(this.getReverseMatchFactor(), o.getReverseMatchFactor());
		}
		if(result == 0) {
			result = Float.compare(this.getMatchFactorDirect(), o.getMatchFactorDirect());
		}
		if(result == 0) {
			result = Float.compare(this.getReverseMatchFactorDirect(), o.getReverseMatchFactorDirect());
		}
		return result;
	}

	@Override
	public int hashCode() {

		return 7 * Float.valueOf(getMatchFactor()).hashCode() + 11 * Float.valueOf(getReverseMatchFactor()).hashCode() + 13 * Float.valueOf(probability).hashCode();
	}

	@Override
	public String toString() {

		return "AbstractComparisonResult [isMatch=" + isMatch + ", matchFactor=" + matchFactor + ", matchFactorDirect=" + matchFactorDirect + ", reverseMatchFactor=" + reverseMatchFactor + ", reverseMatchFactorDirect=" + reverseMatchFactorDirect + ", probability=" + probability + ", penalty=" + penalty + ", advise=" + advise + "]";
	}
}
