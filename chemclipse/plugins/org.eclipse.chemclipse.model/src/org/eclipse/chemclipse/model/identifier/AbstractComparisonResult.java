/*******************************************************************************
 * Copyright (c) 2010, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - getPenalty and Matchfactors should be accessed by getters and not direct field access
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
	private String advise;

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect) {
		this(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, MAX_ALLOWED_PROBABILITY);
	}

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {
		this.probability = probability;
		this.matchFactor = matchFactor;
		this.reverseMatchFactor = reverseMatchFactor;
		this.matchFactorDirect = matchFactorDirect;
		this.reverseMatchFactorDirect = reverseMatchFactorDirect;
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

		setPenalty(0);
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
	public final float getMatchFactor() {

		return getAdjustedValue(getMatchFactorNotAdjusted(), getPenalty());
	}

	@Override
	public final float getMatchFactorDirect() {

		return getAdjustedValue(getMatchFactorDirectNotAdjusted(), getPenalty());
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
	public final float getReverseMatchFactor() {

		return getAdjustedValue(getReverseMatchFactorNotAdjusted(), getPenalty());
	}

	@Override
	public final float getReverseMatchFactorDirect() {

		return getAdjustedValue(getReverseMatchFactorDirectNotAdjusted(), getPenalty());
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

		if(advise == null) {
			if(getMatchFactor() >= MAX_LIMIT_MATCH_FACTOR && getReverseMatchFactor() <= MIN_LIMIT_REVERSE_MATCH_FACTOR) {
				advise = ADVISE_INCOMPLETE;
			} else if(getMatchFactor() <= MIN_LIMIT_MATCH_FACTOR && getReverseMatchFactor() >= MAX_LIMIT_REVERSE_MATCH_FACTOR) {
				advise = ADVISE_IMPURITIES;
			} else {
				advise = "";
			}
		}
		return advise;
	}

	@Override
	public float getRating() {

		float rating = (getMatchFactorNotAdjusted() + getReverseMatchFactorNotAdjusted()) / 2.0f;
		/*
		 * Shall the probability be used too?
		 */
		if(getMatchFactorDirectNotAdjusted() > 0.0f) {
			rating = (rating + getMatchFactorDirectNotAdjusted()) / 2.0f;
		}
		//
		if(getReverseMatchFactorDirectNotAdjusted() > 0.0f) {
			rating = (rating + getReverseMatchFactorDirectNotAdjusted()) / 2.0f;
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

	protected void setMatchFactor(float matchFactor) {

		this.matchFactor = matchFactor;
	}

	protected void setMatchFactorDirect(float matchFactorDirect) {

		this.matchFactorDirect = matchFactorDirect;
	}

	protected void setReverseMatchFactor(float reverseMatchFactor) {

		this.reverseMatchFactor = reverseMatchFactor;
	}

	protected void setReverseMatchFactorDirect(float reverseMatchFactorDirect) {

		this.reverseMatchFactorDirect = reverseMatchFactorDirect;
	}
}
