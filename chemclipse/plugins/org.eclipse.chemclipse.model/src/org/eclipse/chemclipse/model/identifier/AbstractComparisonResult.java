/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
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

	private float matchFactor;
	private float reverseMatchFactor;
	private float probability;
	private String advise = "";
	/*
	 * The borders should be determined to give an advise if necessary.<br/> If
	 * the fit value is high and the rfit value is low, the unknown mass
	 * spectrum is maybe convoluted by impurities.
	 */
	private static final float MIN_MF_BORDER = 20.0f;
	private static final float MAX_MF_BORDER = 80.0f;
	private static final float MIN_RMF_BORDER = 20.0f;
	private static final float MAX_RMF_BORDER = 80.0f;

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
	public float getReverseMatchFactor() {

		return reverseMatchFactor;
	}

	@Override
	public float getProbability() {

		return probability;
	}

	@Override
	public String getAdvise() {

		return advise;
	}

	// ----------------------------------------private methods
	/**
	 * Determines the advise.
	 */
	private void determineAdvise() {

		if(matchFactor >= MAX_MF_BORDER && reverseMatchFactor <= MIN_RMF_BORDER) {
			advise = ADVISE_INCOMPLETE;
		} else if(matchFactor <= MIN_MF_BORDER && reverseMatchFactor >= MAX_RMF_BORDER) {
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
