/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
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

public abstract class AbstractComparisonResult implements IComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 7832661625546592609L;
	//
	private boolean isMatch = true;
	private float matchFactor;
	private float matchFactorDirect;
	private float reverseMatchFactor;
	private float reverseMatchFactorDirect;
	private float probability;
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

	@Override
	public boolean isMatch() {

		return isMatch;
	}

	@Override
	public void setMatch(boolean match) {

		this.isMatch = match;
	}

	@Override
	public float getMatchFactor() {

		return matchFactor;
	}

	@Override
	public float getMatchFactorDirect() {

		return matchFactorDirect;
	}

	@Override
	public void adjustMatchFactor(float penalty) {

		if(penalty >= MIN_ALLOWED_PENALTY && penalty <= MAX_ALLOWED_PENALTY) {
			//
			matchFactor -= penalty;
			if(matchFactor < 0) {
				matchFactor = 0.0f;
			}
			//
			matchFactorDirect -= penalty;
			if(matchFactorDirect < 0) {
				matchFactorDirect = 0.0f;
			}
		}
	}

	@Override
	public float getReverseMatchFactor() {

		return reverseMatchFactor;
	}

	@Override
	public float getReverseMatchFactorDirect() {

		return reverseMatchFactorDirect;
	}

	@Override
	public void adjustReverseMatchFactor(float penalty) {

		if(penalty >= MIN_ALLOWED_PENALTY && penalty <= MAX_ALLOWED_PENALTY) {
			//
			reverseMatchFactor -= penalty;
			if(reverseMatchFactor < 0) {
				reverseMatchFactor = 0.0f;
			}
			//
			reverseMatchFactorDirect -= penalty;
			if(reverseMatchFactorDirect < 0) {
				reverseMatchFactorDirect = 0.0f;
			}
		}
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
		 * Legacy: contains probability or direct hits?
		 */
		if(probability > 0.0f) {
			rating = (rating + probability) / 2.0f;
		}
		//
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

	// ----------------------------------------private methods
	/**
	 * Determines the advise.
	 */
	private void determineAdvise() {

		if(matchFactor >= MAX_LIMIT_MATCH_FACTOR && reverseMatchFactor <= MIN_LIMIT_REVERSE_MATCH_FACTOR) {
			advise = ADVISE_INCOMPLETE;
		} else if(matchFactor <= MIN_LIMIT_MATCH_FACTOR && reverseMatchFactor >= MAX_LIMIT_REVERSE_MATCH_FACTOR) {
			advise = ADVISE_IMPURITIES;
		}
	}

	// ----------------------------hashCode, equals, toString
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
	public int hashCode() {

		return 7 * Float.valueOf(matchFactor).hashCode() + 11 * Float.valueOf(reverseMatchFactor).hashCode() + 13 * Float.valueOf(probability).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("matchQuality=" + matchFactor);
		builder.append(",");
		builder.append("reverseMatchQuality=" + reverseMatchFactor);
		builder.append(",");
		builder.append("probability=" + probability);
		builder.append("]");
		return builder.toString();
	}
	// ----------------------------hashCode, equals, toString
}
