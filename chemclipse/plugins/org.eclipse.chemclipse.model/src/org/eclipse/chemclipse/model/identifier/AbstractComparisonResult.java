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
	private float matchFactor;
	private float reverseMatchFactor;
	private float probability;
	private String advise = "";
	private boolean isMatch = true;

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor) {
		this.matchFactor = matchFactor;
		this.reverseMatchFactor = reverseMatchFactor;
		determineAdvise();
	}

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float probability) {
		this(matchFactor, reverseMatchFactor);
		this.probability = probability;
	}

	@Override
	public float getMatchFactor() {

		return matchFactor;
	}

	@Override
	public void adjustMatchFactor(float penalty) {

		if(penalty >= MIN_ALLOWED_PENALTY && penalty <= MAX_ALLOWED_PENALTY) {
			matchFactor -= penalty;
			if(matchFactor < 0) {
				matchFactor = 0.0f;
			}
		}
	}

	@Override
	public float getReverseMatchFactor() {

		return reverseMatchFactor;
	}

	@Override
	public void adjustReverseMatchFactor(float penalty) {

		if(penalty >= MIN_ALLOWED_PENALTY && penalty <= MAX_ALLOWED_PENALTY) {
			reverseMatchFactor -= penalty;
			if(reverseMatchFactor < 0) {
				reverseMatchFactor = 0.0f;
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
	public boolean isMatch() {

		return isMatch;
	}

	@Override
	public void setMatch(boolean match) {

		this.isMatch = match;
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
